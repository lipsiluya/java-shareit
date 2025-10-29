package ru.practicum;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LaterApplication {

    public static void main(String[] args) throws LifecycleException {
        log.info("Starting application...");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", null);

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(ctx.getServletContext()); // Устанавливаем ServletContext
        context.register(ru.practicum.config.AppConfig.class, ru.practicum.config.WebConfig.class);
        context.refresh();

        DispatcherServlet servlet = new DispatcherServlet(context);
        Wrapper wrapper = tomcat.addServlet(ctx, "appServlet", servlet);
        wrapper.addMapping("/*");
        wrapper.setLoadOnStartup(1);

        tomcat.start();
        log.info("✅ Tomcat started on port 8080");
        log.info("✅ Application ready!");

        tomcat.getServer().await();
    }
}