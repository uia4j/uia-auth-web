package fw.auth.web.v1;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import uia.auth.AuthUserHelper;
import uia.auth.ee.Secured;

@Path("/system")
public class SystemWebService extends AbstractWebService {

    @POST
    @Path("/users/{userId}/_resetPassword")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authorization = "AUTH")
    public Map<String, Object> resetPassword(@PathParam("userId") String userId) {
    	String password = UUID.randomUUID().toString().substring(0, 6);

    	TreeMap<String, Object> data = new TreeMap<>();
    	try(AuthUserHelper helper = new AuthUserHelper()) {
			helper.chanagePassword(userId, password);
        	data.put("result", true);
        	data.put("passwrod", password);
		} catch (Exception e) {
        	data.put("result", false);
		}

    	return data;
    } 

    @POST
    @Path("/test1")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> test1() {
    	TreeMap<String, Object> data = new TreeMap<>();
    	data.put("result", "test1");
    	return data;
    } 

    @POST
    @Path("/test2")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authorization = "DISP")
    public Map<String, Object> test2() {
    	TreeMap<String, Object> data = new TreeMap<>();
    	data.put("user", user());
    	data.put("session", session());
    	return data;
    }

    @POST
    @Path("/test3")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authorization = "DISP")
    public Map<String, Object> test3() {
    	TreeMap<String, Object> data = new TreeMap<>();
    	data.put("user", user());
    	data.put("session", session());
    	return data;
    }

    @GET
    @Path("/test4")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authorization = "DISP")
    public Map<String, Object> test4() {
    	TreeMap<String, Object> data = new TreeMap<>();
    	data.put("user", user());
    	data.put("session", session());
    	return data;
    }

    @GET
    @Path("/test5")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authorization = "DISP")
    public Map<String, Object> test5() {
    	TreeMap<String, Object> data = new TreeMap<>();
    	data.put("user", user());
    	data.put("session", session());
    	return data;
    }
}
