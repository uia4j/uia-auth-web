package fw.auth.web.v1;

import java.io.IOException;
import java.sql.SQLException;
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

import fw.auth.web.v1.model.AuthRoleUserConfig;
import uia.auth.AuthFuncHelper;
import uia.auth.AuthFuncNode;
import uia.auth.AuthUserHelper;
import uia.auth.db.ViewAuthFuncRole;
import uia.auth.db.AuthRole;
import uia.auth.db.AuthUser;
import uia.dao.DaoException;

@Path("/roles")
public class RoleWebService extends AbstractWebService {

    private static final Logger LOGGER = LogManager.getLogger(RoleWebService.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthRole> queryAll() throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/roloes", tx));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	switch(access()) {
	        	case SELF:
	        		return helper.searchUserRoles(user());
	        	case DENY:
	        		return new ArrayList<>();
	        	default:
	            	return helper.searchRoles();
        	}
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthRole insert(AuthRole role) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/roloes? %s", tx, this.gson.toJson(role)));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.insertRole(role);
        	return role;
        }
    }
   
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthRole update(AuthRole role) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT ] v1/roloes? %s", tx, this.gson.toJson(role)));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.updateRole(role);
        	return role;
        }
    }

    @DELETE
    @Path("/{authRole}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("authRole") long authRole) throws SQLException, IOException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][DEL ] v1/roloes/%s", tx, authRole));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.deleteRole(authRole);
        }
    }

    @GET
    @Path("/{authRole}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthRole queryOne(@PathParam("authRole") long authRole) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/roloes/%s", tx, authRole));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	return helper.searchRole(authRole);
        }
    }

    @GET
    @Path("/{authRole}/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthRoleUserConfig> queryUsers(@PathParam("authRole") long authRole, @QueryParam("all") boolean all) throws SQLException, IOException, DaoException {
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
        			.map(AuthUser::getUserId)
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
    }

    @POST
    @Path("/{authRole}/users/{authUser}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void insertUser(@PathParam("authRole") long authRole, @PathParam("authUser") long authUser) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/roles/%s/users/%s", tx, authRole, authUser));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.link(authUser, authRole);
        }
    }

    @DELETE
    @Path("/{authRole}/users/{authUser}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteUser(@PathParam("authRole") long authRole, @PathParam("authUser") long authUser) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][DEL ] v1/roles/%s/users/%s", tx, authRole, authUser));
        
        try (AuthUserHelper helper = new AuthUserHelper()) {
        	helper.unlink(authUser, authRole);
        }
    }

    @GET
    @Path("/{authRole}/funcs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ViewAuthFuncRole> queryFuncs(@PathParam("authRole") long authRole) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/roloes/%s/funcs", tx, authRole));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.searchRoleFuncs(authRole);
        }
    }

    @GET
    @Path("/{authRole}/funcTree")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthFuncNode> queryTree(@PathParam("authRole") long authRole) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/roloes/%s/funcTree", tx, authRole));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.scanRoleFuncNodes(authRole);
        }
    }
}
