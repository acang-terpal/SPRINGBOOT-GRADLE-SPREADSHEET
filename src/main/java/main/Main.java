package main;

import configuration.ControllerConfig;
import entity.EntityConfig;
import helper.Utilities;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Hasan
 */
@SpringBootApplication
@ComponentScan(basePackages = {"rest_resource", "configuration", "router", "controller", "implement", "helper", "service"})
@EntityScan("entity")
public class Main {
    public static EntityConfig conf;
    public static String workDir;

    public static void main(String[] args) throws Exception {
//      workDir = System.getProperty("user.dir");
        workDir = args[0];
        System.out.println("Current working directory : " + Main.workDir);
        String[] paths = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String path : paths) {
            try {
                URL url = new File(path).toURI().toURL();
                System.out.println("classpath : " + url.getFile());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        //setup directory
        Utilities.setupFolder(Main.workDir);

        //----------------get the config data and pass to config entity--------------------
        Main.conf = new EntityConfig();
        ControllerConfig konf = new ControllerConfig(Main.conf, Main.workDir);
        Main.conf = konf.getData();

        //---------------------------springboot-------------------------------------
        HashMap<String, Object> props = new HashMap<>();
        props.put("spring.application.name", "SpreadSheet");
        props.put("server.ssl.enabled", true);
        props.put("server.port", Integer.parseInt(Main.conf.getHttpPort()) + 1);
        props.put("server.jetty.max-threads", Integer.parseInt(Main.conf.getMaxThreads()));
        props.put("server.jetty.min-threads", Integer.parseInt(Main.conf.getMinThreads()));
        props.put("server.jetty.max-http-post-size", 5120000); //5MB
        props.put("server.jetty.max-http-form-post-size", 5120000); //5MB
        props.put("server.ssl.key-store", "classpath:TMMobileAgent.jks");
        props.put("server.ssl.key-store-password", "smartcity123");
        props.put("server.ssl.key-password", "smartcity123");
        props.put("server.ssl.key-store-type", "JKS");
        props.put("server.compression.enabled", true);
        props.put("server.compression.mime-types", "application/json, application/xml, text/css, text/html, text/xml, text/plain, text/javascript, application/javascript, application/font-woff2, application/x-font-woff, font/woff2, application/octet-stream, image/x-icon, image/bmp, image/jpeg, image/jpg, image/png");
        props.put("server.compression.min-response-size", 1024); //Compress the response only if the response size is at least 1KB (value 1024 = 1Kb)
        props.put("spring.threads.virtual.enabled", "true");
        SpringApplication springApps = new SpringApplicationBuilder()
                .sources(Main.class)
                .properties(props)
                .build();
        ConfigurableApplicationContext server = springApps.run(args);

        ConfigurableEnvironment env = server.getEnvironment();
        System.out.println("Server Ready At : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        //configure all network
    }
}
