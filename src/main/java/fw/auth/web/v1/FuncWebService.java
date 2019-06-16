package fw.auth.web.v1;

import static fw.auth.web.v1.AbstractWebService.logError;

import java.sql.Connection;
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
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fw.auth.web.v1.model.AuthFuncConfig;
import fw.auth.web.v1.model.AuthFuncDetail;
import fw.auth.web.v1.model.AuthFuncRoleConfig;
import fw.auth.web.v1.model.AuthFuncUserConfig;
import uia.auth.AuthFuncHelper;
import uia.auth.AuthFuncNode;
import uia.auth.AuthValidator.AccessType;
import uia.auth.db.AuthFunc;
import uia.auth.db.AuthFuncRole;
import uia.auth.db.AuthFuncUser;
import uia.auth.db.AuthRole;
import uia.auth.db.AuthUser;
import uia.auth.db.conf.DB;
import uia.auth.db.dao.AuthFuncRoleDao;
import uia.auth.db.dao.AuthFuncUserDao;
import uia.auth.db.dao.AuthRoleDao;
import uia.auth.db.dao.AuthUserDao;

@Path("/funcs")
public class FuncWebService {

    private static final Logger LOGGER = LogManager.getLogger(FuncWebService.class);

    private Gson gson;

    public FuncWebService() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .create();
    }

    @POST
    @Path("/_tree")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthFuncNode> queryTree() {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/funcs/_tree", tx));
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	List<AuthFuncNode> tree = helper.scanFuncNodes();
        	tree.get(0);
        	return tree;
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return new ArrayList<AuthFuncNode>();
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthFunc> queryAll() {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/funcs", tx));
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.searchFuncs();
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return new ArrayList<AuthFunc>();
        }
    }
    
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFunc insert(AuthFunc func) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/funcs? %s", tx, this.gson.toJson(func)));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	helper.insertFunc(func);
        	return func;
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
    public AuthFunc update(AuthFunc func) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT ] v1/funcs? %s", tx, this.gson.toJson(func)));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	helper.updateFunc(func);
        	return func;
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @DELETE
    @Path("/{authFunc}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("authFunc") long authFunc) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][DEL ] v1/funcs/%s", tx, authFunc));
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	helper.deleteFunc(authFunc);
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        }
    }

    @GET
    @Path("/{authFunc}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFunc queryOne(@PathParam("authFunc") long authFunc) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/funcs/%s", tx, authFunc));
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.searchFunc(authFunc);
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @GET
    @Path("/{authFunc}/access")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFuncDetail queryAccess(@PathParam("authFunc") long authFunc) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/funcs/%s/access", tx, authFunc));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	List<AuthFuncDetail.UserInfo> users = helper.searchFuncUsers(authFunc)
        			.stream()
        			.map(u -> new AuthFuncDetail.UserInfo(u))
        			.collect(Collectors.toList());
        	List<AuthFuncDetail.RoleInfo> roles = helper.searchFuncRoles(authFunc)
					.stream()
					.map(r -> new AuthFuncDetail.RoleInfo(r))
					.collect(Collectors.toList());
        	return new AuthFuncDetail(users, roles);
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @PUT
    @Path("/{authFunc}/access/_all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFuncDetail updateAccess(@PathParam("authFunc") long authFunc, AuthFuncConfig config) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT ] v1/funcs/%s/access/_all? %s", tx, authFunc, this.gson.toJson(config)));
        
        try (Connection conn = DB.create()) {
        	conn.setAutoCommit(false);
        	
        	// users
        	AuthFuncUserDao afuDao = new AuthFuncUserDao(conn);
        	afuDao.delete(authFunc);
        	for(AuthFuncConfig.FuncUser user : config.users) {
        		afuDao.insert(new AuthFuncUser(authFunc, user.authUser, user.accessType));
        	}

        	// roles
        	AuthFuncRoleDao afrDao = new AuthFuncRoleDao(conn);
        	afrDao.delete(authFunc);
        	for(AuthFuncConfig.FuncRole role : config.roles) {
        		afrDao.insert(new AuthFuncRole(authFunc, role.authRole, role.accessType));
        	}
        	
        	conn.commit();
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
        
        return queryAccess(authFunc);
    }

    @PUT
    @Path("/{authFunc}/users/_link")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFuncUser linkUser(@PathParam("authFunc") long authFunc, AuthFuncUserConfig config) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT ] v1/funcs/%s/users/_link? %s", tx, authFunc, this.gson.toJson(config)));
        
        try (Connection conn = DB.create()) {
        	// access type
        	AccessType at = AccessType.UNKNOWN;
        	try {
        		at = AccessType.valueOf(config.accessType.toUpperCase());
        	}
        	catch(Exception ex) {
        		at = AccessType.UNKNOWN;
        	}

        	// user
        	AuthUser user = new AuthUserDao(conn).selectByPK(config.authUser);
        	if(user == null || !"Y".equals(user.getEnabled())) {
        		return null;
        	}
        	
        	// authorization
        	AuthFuncUserDao afuDao = new AuthFuncUserDao(conn);
        	if(at == AccessType.UNKNOWN) {
        		afuDao.delete(authFunc, user.getId());
        		return null;
        	}
        	else {
            	AuthFuncUser afu = afuDao.selectByPK(authFunc, user.getId());
            	if(afu == null) {
            		afu = new AuthFuncUser(authFunc, user.getId(), at.code);
            		afuDao.insert(afu);
            	}
            	else {
            		afu.setAccessType(at.code);
            		afuDao.update(afu);
            	}
            	return afu;
        	}
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }

    @PUT
    @Path("/{authFunc}/roles/_link")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFuncRole linkRole(@PathParam("authFunc") long authFunc, AuthFuncRoleConfig config) {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT ] v1/funcs/%s/roles/_link? %s", tx, authFunc, this.gson.toJson(config)));
        
        try (Connection conn = DB.create()) {
        	// access type
        	AccessType at = AccessType.UNKNOWN;
        	try {
        		at = AccessType.valueOf(config.accessType.toUpperCase());
        	}
        	catch(Exception ex) {
        		at = AccessType.UNKNOWN;
        	}

        	// role
        	AuthRole role = new AuthRoleDao(conn).selectByPK(config.authRole);
        	if(role == null || !"Y".equals(role.getEnabled())) {
        		return null;
        	}

        	// authorization
        	AuthFuncRoleDao afrDao = new AuthFuncRoleDao(conn);
        	AuthFuncRole afr = afrDao.selectByPK(authFunc, role.getId());
        	if(at == AccessType.UNKNOWN) {
        		afrDao.delete(authFunc, role.getId());
        		return null;
        	}
        	else {
	        	if(afr == null) {
	        		afr = new AuthFuncRole(authFunc, role.getId(), at.code);
	        		afrDao.insert(afr);
	        	}
	        	else {
	        		afr.setAccessType(at.code);
	        		afrDao.update(afr);
	        	}
	        	return afr;
        	}
        }
        catch(Exception ex) {
            logError(LOGGER, tx, ex);
        	return null;
        }
    }
}
