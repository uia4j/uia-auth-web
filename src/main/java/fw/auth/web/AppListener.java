package fw.auth.web;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uia.auth.db.conf.DB;

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
            String appPath = "/opt/auth-web/app.properties";
            File file = new File(appPath);
            if (!file.exists()) {
                String path = sc.getRealPath("/") + "WEB-INF" + System.getProperty("file.separator");
                appPath = path + "app.properties";
            }
            LOGGER.info("properties = " + appPath);

            // APP properties
            Properties p = new Properties(System.getProperties());
            p.load(new FileInputStream(appPath));
            System.setProperties(p);

            try {
                LOGGER.info(String.format("auth.db.%s = %s, user:%s",
                        System.getProperty("auth.db.env"),
                        System.getProperty("auth.db.connection"),
                        System.getProperty("auth.db.user")));
                DB.config(
                        System.getProperty("auth.db.connection"),
                        System.getProperty("auth.db.user"),
                        System.getProperty("auth.db.pwd"));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
