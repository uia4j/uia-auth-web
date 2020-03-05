package fw.auth.web;

import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uia.auth.db.conf.AuthDB;

public class AppListener implements ServletContextListener {

    private static final Logger LOGGER = LogManager.getLogger(AppListener.class);

    public static final String VER = "0.19.0-SNAPSHOT";

    @Override
    public void contextDestroyed(ServletContextEvent evt) {
    }

    @Override
    public void contextInitialized(ServletContextEvent evt) {
        ServletContext sc = evt.getServletContext();
        try {
            String appPath = sc.getRealPath("/") + "WEB-INF" + System.getProperty("file.separator") + "app.properties";
            LOGGER.info("properties = " + appPath);

            try (FileInputStream fis = new FileInputStream(appPath)) {
                Properties p = new Properties(System.getProperties());
                p.load(fis);
                System.setProperties(p);
            }

            LOGGER.info(String.format("auth.db.%s = %s, user:%s",
                    System.getProperty("auth.db.env"),
                    System.getProperty("auth.db.connection"),
                    System.getProperty("auth.db.user")));
            AuthDB.config(
                    System.getProperty("auth.db.connection"),
                    System.getProperty("auth.db.user"),
                    System.getProperty("auth.db.pwd"));
        }
        catch (Exception e) {
            LOGGER.error("contextInitialized", e);
        }
    }
}
