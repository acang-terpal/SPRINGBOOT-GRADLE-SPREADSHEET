package rest_resource;

import configuration.ControllerLog;
import configuration.DependencyConfig;
import configuration.FreeMarkerConfig;
import entity.EntityConfig;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import router.RouterSpreadSheet;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
//@RequestMapping("/")
public class ResourceSpreadSheet {
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();
    private final EntityConfig entityConfig;
    private final ControllerLog controllerLog;
    private final Cache cacheCookies;
    private final Cache cacheMaster;
    private final FreeMarkerConfig freeMarkerCfg;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public ResourceSpreadSheet(DependencyConfig dependencyConfig, FreeMarkerConfig freeMarkerCfg, RouterSpreadSheet routerSpreadSheet) {
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.cacheMaster = dependencyConfig.getCacheMaster();
        this.controllerLog = dependencyConfig.getControllerLog();
        this.freeMarkerCfg = freeMarkerCfg;
    }

    @RequestMapping(path = "/SpreadSheet/**")
    public ResponseEntity<StreamingResponseBody> index(HttpServletRequest request) {
        ResponseEntity<StreamingResponseBody> response = ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        try {
            RouterSpreadSheet routerSpreadSheet = this.applicationContext.getBean(RouterSpreadSheet.class);
            //System.out.println("RouterSpreadSheet Unique ID: " + routerSpreadSheet.getUniqueId());
            response = routerSpreadSheet.route(request);
            return response;
        } catch (Exception ex) {
            this.controllerLog.logErrorWriter(request.getRequestURI());
            ex.printStackTrace();
        } finally {

        }
        return response;
    }

    //example of streaming response
    @GetMapping("/sse")
    public SseEmitter handleSse() {
        SseEmitter emitter = new SseEmitter();
        nonBlockingService.execute(() -> {
            try {
                emitter.send("/sse" + " @ " + new Date());
                // we could send more events
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    //example of handling large download file
    @GetMapping("/srb")
    public ResponseEntity<StreamingResponseBody> handleRbe() {
        StreamingResponseBody stream = out -> {
            String msg = "/srb" + " @ " + new Date();
            out.write(msg.getBytes());
        };
        return new ResponseEntity(stream, HttpStatus.OK);
    }
}
