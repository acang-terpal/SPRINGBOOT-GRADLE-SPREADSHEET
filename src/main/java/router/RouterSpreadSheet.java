package router;

import configuration.ControllerLog;
import configuration.DependencyConfig;
import configuration.FreeMarkerConfig;
import entity.EntityConfig;
import helper.Helper;
import helper.Utilities;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component("routerSpreadSheet")
@Scope("prototype")
@Order(4)
public class RouterSpreadSheet {
    private final EntityConfig entityConfig;
    private final ControllerLog controllerLog;
    private final FreeMarkerConfig freeMarkerCfg;
    private final Cache cacheMaster;
    private final Cache cacheCookies;
    private final Helper helper;
    private final Utilities utilities;
    private final String uuid;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public RouterSpreadSheet(DependencyConfig dependencyConfig, FreeMarkerConfig freeMarkerCfg, Utilities utilities, Helper hlp) {
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.cacheMaster = dependencyConfig.getCacheMaster();
        this.controllerLog = dependencyConfig.getControllerLog();
        this.freeMarkerCfg = freeMarkerCfg;
        this.utilities = utilities;
        this.helper = hlp;
        this.uuid = UUID.randomUUID().toString(); // Assign a unique ID on creation
        //System.out.println("PrototypeBean instance created with UUID: " + uuid);
    }

    public String getUniqueId() {
        return uuid;
    }

    public ResponseEntity<StreamingResponseBody> route(HttpServletRequest request) {
        ResponseEntity<StreamingResponseBody> response = new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        try {
            String fullUrl = request.getRequestURI();
            String fullUri = request.getRequestURI().substring(1);
            String baseUrl = request.getRequestURL().toString().replace(fullUrl, "");
            String data = "Internal Error";
            byte[] dataOut = data.getBytes(StandardCharsets.UTF_8);
            String[] uriSplit = fullUri.split("/");
            for (String uri : uriSplit) {
                //System.out.println("uriSplit : " + uri);
            }
            //handle exteral/resource file
            response = this.helper.routeDefaultHandler(request);

            if (response.getStatusCode().value() == 200) {
                return response;
            } else {
                String controller = uriSplit[1];
                String methode = uriSplit[2];

                //invoking class pure java without spring dependency injection
//                String controllerClassName = "controller." + controller;
//                Class<?> controllerClass = Class.forName(controllerClassName); // convert string classname to class
//                Object controllerObject = controllerClass.getDeclaredConstructor(); // invoke new constructor
//                Class<?>[] paramTypes = {HttpServletRequest.class};
//                Method callControllerMethod = controllerObject.getClass().getMethod(methode, paramTypes);
//                response = (ResponseEntity<byte[]>) callControllerMethod.invoke(controllerObject, request);

                //using spring dependency injection
                Object controllerObject = this.applicationContext.getBean(controller);
                //make sure class is statefull not singleton
                //Method getUniqueId = controllerObject.getClass().getMethod("getUniqueId");
                //System.out.println(methode + " Unique ID: " + getUniqueId.invoke(controllerObject));
                Class<?>[] paramTypes = {HttpServletRequest.class};
                Method callControllerMethod = controllerObject.getClass().getMethod(methode, paramTypes);
                response = (ResponseEntity<StreamingResponseBody>) callControllerMethod.invoke(controllerObject, request);
            }
        } catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
