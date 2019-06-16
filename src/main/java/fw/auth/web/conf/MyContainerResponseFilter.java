package fw.auth.web.conf;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;

import uia.auth.ee.AuthHeaders;
import uia.auth.ee.Secured;

public final class MyContainerResponseFilter implements ContainerResponseFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Headers", "auth-user, auth-session, auth-access, X-Requested-With, Content-Type, X-Codingpedia, Authorization");
        headers.add("Access-Control-Expose-Headers", "auth-user, auth-session, auth-access");

        if (this.resourceInfo == null) {
            return;
        }

        Secured secured = this.resourceInfo.getResourceMethod().getAnnotation(Secured.class);
        if (secured != null) {
        	String user = requestContext.getHeaderString(AuthHeaders.USER);
        	String session = requestContext.getHeaderString(AuthHeaders.SESSION);

        	headers.putSingle(AuthHeaders.USER, user);
        	headers.putSingle(AuthHeaders.SESSION, session);
        	headers.addAll(
        			"Set-Cookie", 
        			new NewCookie(AuthHeaders.USER, user), 
        			new NewCookie(AuthHeaders.SESSION, session));
        }
    }
}
