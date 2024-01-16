package mitya.haha;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import mitya.haha.config.WebConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MainInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(final ServletContext sc) throws ServletException{

        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.register(WebConfig.class);
        sc.addListener(new ContextLoaderListener(rootContext));

        ServletRegistration.Dynamic appServlet = sc.addServlet(
                "mvc",
                new DispatcherServlet(new GenericWebApplicationContext())
        );
        appServlet.setLoadOnStartup(1);
        appServlet.addMapping("/");
    }
}

