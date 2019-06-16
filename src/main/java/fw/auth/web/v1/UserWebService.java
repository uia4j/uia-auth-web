package fw.auth.web.v1;

import static fw.auth.web.v1.AbstractWebService.logError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fw.auth.web.v1.model.AuthUserRoleConfig;
import fw.auth.web.v1.model.Job;
import fw.auth.web.v1.model.Login;
import fw.auth.web.v1.model.LoginResult;
import fw.auth.web.v1.model.JobResult;
import uia.auth.AuthFuncHelper;
import uia.auth.AuthFuncNode;
import uia.auth.AuthUserHelper;
import uia.auth.AuthValidator;
import uia.auth.db.AuthFuncUserView;
import uia.auth.db.AuthSecurityView;
import uia.auth.db.AuthUser;
import uia.auth.ee.Secured;

@Path("/users")
public class UserWebService {

    private static final Logger LOGGER = LogManager.getLogger(UserWebService.class);

    private Gson gson;

    public UserWebService() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .create();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authentication = true, authorization = "AUTH.USER.QRY")
    public List<AuthUser> queryAll() {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/users", tx));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	return helper.searchUsers();
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authorization = "AUTH.USER.CREATE")
    public AuthUser insert(AuthUser user) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/users? ", tx, this.gson.toJson(user)));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.insertUser(user);
        	return user;
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }
   
    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authorization = "AUTH.USER.UPDATE")
    public AuthUser update(AuthUser user) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT] v1/users? ", tx, this.gson.toJson(user)));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.updateUser(user);
        	return user;
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @DELETE
    @Path("/{authUser}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authorization = "AUTH.USER.DELETE")
    public void delete(@PathParam("authUser") long authUser) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][DEL ] v1/users/%s", tx, authUser));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.deleteUser(authUser);
        } 
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        }
    }

    @GET
    @Path("/{authUser}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON) 
    @Secured(authorization = "AUTH.USER.QRY")
    public AuthUser queryOne(@PathParam("authUser") long authUser) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/users/%s", tx, authUser));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	return helper.searchUser(authUser);
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @POST
    @Path("/{authUser}/_resetPassword")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON) 
    @Secured(authorization = "AUTH.USER.UPDATE")
    public void reset(@PathParam("authUser") long authUser) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/users/%s/_resetPassword", tx, authUser));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.chanagePassword(authUser, "12345");
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        }
    }

    @GET
    @Path("/{authUser}/roles")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authorization = "AUTH.USER.QRY")
    public List<AuthUserRoleConfig> queryRoles(@PathParam("authUser") long authUser, @QueryParam("all") boolean all) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/users/%s/roles?all=%s", tx, authUser, all));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	if(!all) {
            	return helper.searchUserRoles(authUser)
                    	.stream()
                    	.map(r -> new AuthUserRoleConfig(r, true))
            			.collect(Collectors.toList());
        	}
        	
        	List<String> roles = helper.searchUserRoles(authUser)
        			.stream()
        			.map(r -> r.getRoleName())
        			.collect(Collectors.toList());
        	
        	return helper.searchRoles()
	        	.stream()
	        	.map(r -> new AuthUserRoleConfig(r, roles.contains(r.getRoleName())))
				.collect(Collectors.toList());
        	
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return new ArrayList<AuthUserRoleConfig>();
        }
    }

    @POST
    @Path("/{authUser}/roles/{authRole}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void insertRole(@PathParam("authUser") long authUser, @PathParam("authRole") long authRole) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/users/%s/roles/%s", tx, authUser, authRole));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.link(authUser, authRole);
        	
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        }
    }

    @DELETE
    @Path("/{authUser}/roles/{authRole}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteRole(@PathParam("authUser") long authUser, @PathParam("authRole") long authRole) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][DEL ] v1/users/%s/roles/%s", tx, authUser, authRole));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.unlink(authUser, authRole);
        	
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        }
    }

    @GET
    @Path("/{authUser}/funcs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthFuncUserView> queryFuncs(@PathParam("authUser") long authUser) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET] v1/users/%s/funcs", tx, authUser));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.searchUserFuncs(authUser);
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @GET
    @Path("/{authUser}/funcTree")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthFuncNode> queryTree(@PathParam("authUser") long authUser) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/users/%s/funcTree", tx, authUser));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.scanUserFuncNodes(authUser);
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return new ArrayList<AuthFuncNode>();
        }
    }

    @POST
    @Path("/{authUser}/_validate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JobResult validate(@PathParam("authUser") long authUser, Job job) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/users/%s/_validate, job=%s", tx, authUser, this.gson.toJson(job)));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	AuthValidator va = helper.validate(authUser);
        	for(String f : job.functions) {
        		if("and".equals(job.op)) {
            		va.and(f);
        		}
        		if("or".equals(job.op)) {
            		va.or(f); 
        		}
        	}
        	return new JobResult(va.getUserId(), job.functions, va.result().name());
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @POST
    @Path("/_login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LoginResult login(Login login) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/users/_login", tx));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	if(helper.validatePassword(login.userId,  login.password)) {
        		AuthSecurityView asv = helper.udpateToken(login.userId, 1800000);
                LOGGER.info(String.format("[%s]login, %s=%s", tx, login.userId, asv.getToken()));
        		return new LoginResult(login.userId, asv.getUserId(), 0, asv.getToken(), "success");
        	}
        	else {
                return new LoginResult(login.userId, "", 1, null, "passowrd is wrong");
        	}
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
            return new LoginResult(login.userId, "", -1, null, ex.getMessage());
        }
    }
}
