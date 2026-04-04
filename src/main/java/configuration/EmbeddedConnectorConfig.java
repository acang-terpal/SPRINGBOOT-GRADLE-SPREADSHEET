package configuration;

import main.Main;
import org.eclipse.jetty.compression.gzip.GzipCompression;
import org.eclipse.jetty.compression.server.CompressionConfig;
import org.eclipse.jetty.compression.server.CompressionHandler;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.VirtualThreadPool;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.jetty.JettyWebServer;
import org.springframework.boot.jetty.servlet.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.security.KeyStore;

@Configuration
@Order(2)
public class EmbeddedConnectorConfig {
    // this is to add more custom configuration for "springApps" on Main.class
    @Bean
    public WebServerFactoryCustomizer<JettyServletWebServerFactory> jettySslCustomizer() {
        return factory -> factory.addServerCustomizers(server -> {
            InputStream readStream = null;
            try {
                //--------------------------------------set virtual thread-----------------------------------------
                VirtualThreadPool vThreadPool = server.getBean(VirtualThreadPool.class);
                vThreadPool.setMaxConcurrentTasks(2000000);
                server.setAttribute("threadpool", vThreadPool);
                server.setStopTimeout(30000);

                //--------------------------------------set handler gzip-----------------------------------------
                ContextHandlerCollection handlerCollection = this.getHandlerCollection(server);
                server.setHandler(handlerCollection);

                //--------------------------------------custom http-----------------------------------------
                ServerConnector connectorHttpByConfig = new ServerConnector(server);
                connectorHttpByConfig.setPort(Integer.parseInt(Main.conf.getHttpPort()));
                connectorHttpByConfig.setIdleTimeout(120000);
                connectorHttpByConfig.setName("jetty http " + Main.conf.getHttpPort());
                server.addConnector(connectorHttpByConfig);

                ServerConnector connectorHttpHardCode = new ServerConnector(server);
                connectorHttpHardCode.setPort(8079);
                connectorHttpHardCode.setIdleTimeout(120000);
                connectorHttpHardCode.setName("jetty http 8079");
                server.addConnector(connectorHttpHardCode);

                //--------------------------------------custom https---------------------------------------
                KeyStore ks = KeyStore.getInstance("JKS");
                readStream = new ClassPathResource("TMMobileAgent.jks").getInputStream();
                ks.load(readStream, "smartcity123".toCharArray());
                //Key key = ks.getKey("keyAlias", "smartcity123".toCharArray());

                SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
                sslContextFactory.setKeyStore(ks);
                sslContextFactory.setKeyStorePassword("smartcity123");
                sslContextFactory.setEndpointIdentificationAlgorithm(null);
                sslContextFactory.setKeyStoreType("JKS");
                sslContextFactory.setExcludeProtocols("TLS", "TLSv1", "TLSv1.1");
                sslContextFactory.setIncludeProtocols("TLSv1.2", "TLSv1.3");

                HttpConfiguration httpConfiguration = new HttpConfiguration();
                httpConfiguration.setSendServerVersion(false);
                ServerConnector connectorHttpsHarcode = new ServerConnector(
                        server,
                        new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                        new HttpConnectionFactory(httpConfiguration));
                connectorHttpsHarcode.setPort(8080);
                connectorHttpsHarcode.setIdleTimeout(120000);
                connectorHttpsHarcode.setName("jetty https 8080");
                server.addConnector(connectorHttpsHarcode);

                connectorHttpByConfig.start();
                connectorHttpHardCode.start();
                connectorHttpsHarcode.start();

                // by pass check SNI (Server Name Indication)
                for (Connector connector : server.getConnectors()) {
                    if (connector instanceof ServerConnector sc) {
                        for (ConnectionFactory cf : sc.getConnectionFactories()) {
                            if (cf instanceof HttpConnectionFactory hcf) {
                                HttpConfiguration config = hcf.getHttpConfiguration();
                                // Cari SecureRequestCustomizer di dalam daftar customizers
                                for (HttpConfiguration.Customizer customizer : config.getCustomizers()) {
                                    if (customizer instanceof SecureRequestCustomizer src) {
                                        // turn off SNI Check for Port 20001 dan 8080
                                        src.setSniHostCheck(false);
                                        src.setSniRequired(false);
                                        src.setStsMaxAge(-1);
                                        src.setStsIncludeSubDomains(false);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to start connector", ex);
            } finally {
                try {
                    if (readStream != null) {
                        readStream.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public ContextHandlerCollection getHandlerCollection(Server server) {
        Handler currentHandler = server.getHandler();
        CompressionHandler compressionHandler = new CompressionHandler();
        GzipCompression gzip = new GzipCompression();
        compressionHandler.putCompression(gzip);

        var builder = CompressionConfig.builder();
        builder.compressIncludeMethod("GET");
        builder.compressIncludeMethod("POST");
        builder.compressIncludeMimeType("text/html");
        builder.compressIncludeMimeType("text/plain");
        builder.compressIncludeMimeType("text/xml");
        builder.compressIncludeMimeType("application/xhtml+xml");
        builder.compressIncludeMimeType("text/css");
        builder.compressIncludeMimeType("application/javascript");
        builder.compressIncludeMimeType("image/svg+xml");
        builder.compressIncludeMimeType("image/png");
        builder.compressIncludeMimeType("application/json");

        compressionHandler.putConfiguration("/", builder.build());
        compressionHandler.setHandler(currentHandler);
        ContextHandlerCollection handlerCollection = new ContextHandlerCollection();
        handlerCollection.addHandler(compressionHandler);
        return handlerCollection;
    }

    @Bean
    public JettyCustomizer jettyCustomizer(WebServerApplicationContext context) {
        return new JettyCustomizer((JettyWebServer) context.getWebServer());
    }

    private class JettyCustomizer implements ApplicationListener<ApplicationReadyEvent> {
        private final JettyWebServer jettyWebServer;

        public JettyCustomizer(JettyWebServer jettyWebServer) {
            this.jettyWebServer = jettyWebServer;
        }

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            Server server = this.jettyWebServer.getServer();
            Connector[] connectors = server.getConnectors();
            for (Connector conn : connectors) {
                System.out.println("protocol and running port : " +
                        conn.getProtocols() + " " +
                        (conn.getName() == null ? Integer.parseInt(Main.conf.getHttpPort()) + 1 : conn.getName()) + " ");
            }
            VirtualThreadPool vThreadPool = server.getBean(VirtualThreadPool.class);
            System.out.println("vThreadPool.getMaxConcurrentTasks : " + vThreadPool.getMaxConcurrentTasks());
        }
    }
}

