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

import fw.auth.web.v1.model.AuthRoleUserConfig;
import uia.auth.AuthFuncHelper;
import uia.auth.AuthFuncNode;
import uia.auth.AuthUserHelper;
import uia.auth.db.AuthFuncRoleView;
import uia.auth.db.AuthRole;
import uia.auth.ee.Secured;

@Path("/roles")
public class RoleWebService {

    private static final Logger LOGGER = LogManager.getLogger(RoleWebService.class);

    private Gson gson;

    public RoleWebService() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .create();
    }
    
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(authorization = "AUTH.ROLE.QRY")
    public List<AuthRole> queryAll() {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/roloes", tx));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	return helper.searchRoles();
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
    public AuthRole insert(AuthRole role) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/roloes? ", tx, this.gson.toJson(role)));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.insertRole(role);
        	return role;
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
    public AuthRole update(AuthRole role) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT ] v1/roloes? ", tx, this.gson.toJson(role)));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.updateRole(role);
        	return role;
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @DELETE
    @Path("/{authRole}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("authRole") long authRole) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][DEL ] v1/roloes/%s", tx, authRole));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.deleteRole(authRole);
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        }
    }

    @GET
    @Path("/{authRole}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthRole queryOne(@PathParam("authRole") long authRole) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/roloes/%s", tx, authRole));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	return helper.searchRole(authRole);
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @GET
    @Path("/{authRole}/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthRoleUserConfig> queryUsers(@PathParam("authRole") long authRole, @QueryParam("all") boolean all) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/roloes/%s/users?all=%s", tx, authRole, all));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	if(!all) {
            	return helper.searchRoleUsers(authRole)
                    	.stream()
                    	.map(u -> new AuthRoleUserConfig(u, true))
            			.collect(Collectors.toList());
        	}
        	
        	List<String> roles = helper.searchRoleUsers(authRole)
        			.stream()
        			.map(u -> u.getUserId())
        			.collect(Collectors.toList());
        	
        	return helper.searchUsers()
	        	.stream()
	        	.map(u -> new AuthRoleUserConfig(u, roles.contains(u.getUserId())))
	        	.sorted((a, b) -> {
	        		if(a.selected == b.selected) {
	        			return a.user.getUserName().compareTo(b.user.getUserName());
	        		}
	        		else {
	        			return a.selected ? -1 : 1;
	        		}
	        	})
				.collect(Collectors.toList());
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return new ArrayList<AuthRoleUserConfig>();
        }
    }

    @POST
    @Path("/{authRole}/users/{authUser}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void insertUser(@PathParam("authRole") long authRole, @PathParam("authUser") long authUser) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/roles/%s/users/%s", tx, authRole, authUser));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.link(authUser, authRole);
        	
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        }
    }

    @DELETE
    @Path("/{authRole}/users/{authUser}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteUser(@PathParam("authRole") long authRole, @PathParam("authUser") long authUser) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][DEL ] v1/roles/%s/users/%s", tx, authRole, authUser));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.unlink(authUser, authRole);
        	
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        }
    }

    @GET
    @Path("/{authRole}/funcs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthFuncRoleView> queryFuncs(@PathParam("authRole") long authRole) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/roloes/%s/funcs", tx, authRole));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.searchRoleFuncs(authRole);
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @GET
    @Path("/{authRole}/funcTree")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthFuncNode> queryTree(@PathParam("authRole") long authRole) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/roloes/%s/funcTree", tx, authRole));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.scanRoleFuncNodes(authRole);
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return new ArrayList<AuthFuncNode>();
        }
    }
}
