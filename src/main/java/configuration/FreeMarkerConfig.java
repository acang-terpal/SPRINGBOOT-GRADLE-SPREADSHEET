package configuration;

import freemarker.cache.NullCacheStorage;
import freemarker.template.TemplateExceptionHandler;
import main.Main;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Scope("singleton")
@Order(3)
public class FreeMarkerConfig {
    private final freemarker.template.Configuration freeMarkerCfgMamenComponent;
    private final freemarker.template.Configuration freeMarkerCfgMamen;

    public FreeMarkerConfig() {
        this.freeMarkerCfgMamen = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        this.freeMarkerCfgMamenComponent = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        try {
            //webroot
            freeMarkerCfgMamen.setDirectoryForTemplateLoading(new File(Main.conf.getMasterFolder() + File.separator + "webroot" + File.separator + "SpreadSheet"));
            freeMarkerCfgMamen.setDefaultEncoding("UTF-8");
            freeMarkerCfgMamen.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            freeMarkerCfgMamen.setLogTemplateExceptions(false);
            freeMarkerCfgMamen.setWrapUncheckedExceptions(true);
            freeMarkerCfgMamen.setFallbackOnNullLoopVariable(false);
//            freeMarkerCfgMamen.setCacheStorage(new freemarker.cache.NullCacheStorage());
            freeMarkerCfgMamen.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));
            freeMarkerCfgMamen.setTemplateUpdateDelayMilliseconds(5000); // Cek update template setiap 5 detik (0 kalau mau selamanya di cache)

            //component
            freeMarkerCfgMamenComponent.setDirectoryForTemplateLoading(new File(Main.conf.getMasterFolder() + File.separator + "webroot" + File.separator + "SpreadSheet" + File.separator + "component"));
            freeMarkerCfgMamenComponent.setDefaultEncoding("UTF-8");
            freeMarkerCfgMamenComponent.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            freeMarkerCfgMamenComponent.setLogTemplateExceptions(false);
            freeMarkerCfgMamenComponent.setWrapUncheckedExceptions(true);
            freeMarkerCfgMamenComponent.setFallbackOnNullLoopVariable(false);
//            freeMarkerCfgMamen.setCacheStorage(new freemarker.cache.NullCacheStorage());
            freeMarkerCfgMamenComponent.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));
            freeMarkerCfgMamenComponent.setTemplateUpdateDelayMilliseconds(5000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public freemarker.template.Configuration getObjectMamen() {
        return this.freeMarkerCfgMamen;
    }

    public freemarker.template.Configuration getObjectMamenComponent() {
        return this.freeMarkerCfgMamenComponent;
    }
}
