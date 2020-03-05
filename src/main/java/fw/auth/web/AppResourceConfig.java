package fw.auth.web;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import fw.auth.web.conf.MyContainerResponseFilter;
import fw.auth.web.conf.MyContextResolver;
import fw.auth.web.conf.MyJacksonFeature;
import uia.auth.ee.AuthRequestFilter;
import uia.auth.ee.AuthResponseFilter;

@ApplicationPath("/api/v1")
public class AppResourceConfig extends ResourceConfig {

    public AppResourceConfig() {

        register(SQLExceptionMapper.class);
        register(DaoExceptionMapper.class);
    	
    	packages("fw.auth.web.v1");
        register(MyContainerResponseFilter.class);
        register(MyContextResolver.class);
        register(MyJacksonFeature.class);

        register(AuthRequestFilter.class);
        register(AuthResponseFilter.class);
    }
}
