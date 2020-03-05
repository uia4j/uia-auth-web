package fw.auth.web.v1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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

import fw.auth.web.v1.model.AuthFuncConfig;
import fw.auth.web.v1.model.AuthFuncDetail;
import fw.auth.web.v1.model.AuthFuncRoleConfig;
import fw.auth.web.v1.model.AuthFuncUserConfig;
import fw.auth.web.v1.model.ScanRequest;
import uia.auth.AuthFuncHelper;
import uia.auth.AuthFuncNode;
import uia.auth.AuthValidator;
import uia.auth.AuthValidator.AccessType;
import uia.auth.db.AuthFunc;
import uia.auth.db.AuthFuncRole;
import uia.auth.db.AuthFuncUser;
import uia.auth.db.AuthRole;
import uia.auth.db.AuthUser;
import uia.auth.db.conf.AuthDB;
import uia.auth.db.dao.AuthFuncRoleDao;
import uia.auth.db.dao.AuthFuncUserDao;
import uia.auth.db.dao.AuthRoleDao;
import uia.auth.db.dao.AuthUserDao;
import uia.dao.DaoException;

@Path("/funcs")
public class FuncWebService extends AbstractWebService {

    private static final Logger LOGGER = LogManager.getLogger(FuncWebService.class);
    
    @POST
    @Path("/_scan")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, AuthValidator.UserAccessInfo> scan(ScanRequest request) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/funcs/_scan %s", tx, this.gson.toJson(request)));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.scan(request.getUserId(), request.getFuncName());
        }
    } 

    @POST
    @Path("/_tree")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthFuncNode> queryTree() throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/funcs/_tree", tx));
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	List<AuthFuncNode> tree = helper.scanFuncNodes();
        	tree.get(0);
        	return tree;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<AuthFunc> queryAll() throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/funcs", tx));
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.searchFuncs();
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFunc insert(AuthFunc func) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][POST] v1/funcs? %s", tx, this.gson.toJson(func)));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	helper.insertFunc(func);
        	return func;
        }
    }
   
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFunc update(AuthFunc func) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT ] v1/funcs? %s", tx, this.gson.toJson(func)));
        
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	helper.updateFunc(func);
        	return func;
        }
    }

    @DELETE
    @Path("/{authFunc}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("authFunc") long authFunc) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][DEL ] v1/funcs/%s", tx, authFunc));
        if(authFunc < 100) {
        	return;
        }
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	helper.deleteFunc(authFunc);
        }
    }

    @GET
    @Path("/{authFunc}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFunc queryOne(@PathParam("authFunc") long authFunc) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][GET ] v1/funcs/%s", tx, authFunc));
        try (AuthFuncHelper helper = new AuthFuncHelper()) {
        	return helper.searchFunc(authFunc);
        }
    }

    @GET
    @Path("/{authFunc}/access")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFuncDetail queryAccess(@PathParam("authFunc") long authFunc) throws SQLException, IOException, DaoException {
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
    }

    @PUT
    @Path("/{authFunc}/access/_all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFuncDetail updateAccess(@PathParam("authFunc") long authFunc, AuthFuncConfig config) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT ] v1/funcs/%s/access/_all? %s", tx, authFunc, this.gson.toJson(config)));
        
        try (Connection conn = AuthDB.create()) {
        	conn.setAutoCommit(false);
        	
        	// users
        	AuthFuncUserDao afuDao = new AuthFuncUserDao(conn);
        	afuDao.delete(authFunc);
        	for(AuthFuncConfig.FuncUser user : config.users) {
        		afuDao.insert(new AuthFuncUser(authFunc, user.authUser, user.accessType, null));
        	}

        	// roles
        	AuthFuncRoleDao afrDao = new AuthFuncRoleDao(conn);
        	afrDao.delete(authFunc);
        	for(AuthFuncConfig.FuncRole role : config.roles) {
        		afrDao.insert(new AuthFuncRole(authFunc, role.authRole, role.accessType, null));
        	}
        	
        	conn.commit();
        }
        
        return queryAccess(authFunc);
    }

    @PUT
    @Path("/{authFunc}/users/_link")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFuncUser linkUser(@PathParam("authFunc") long authFunc, AuthFuncUserConfig config) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT ] v1/funcs/%s/users/_link? %s", tx, authFunc, this.gson.toJson(config)));
        
        try (Connection conn = AuthDB.create()) {
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
        		afuDao.deleteByPK(authFunc, user.getId());
        		return null;
        	}
        	else {
            	AuthFuncUser afu = afuDao.selectByPK(authFunc, user.getId());
            	if(afu == null) {
            		afu = new AuthFuncUser(authFunc, user.getId(), at.code, config.funcUserArgs);
            		afuDao.insert(afu);
            	}
            	else {
            		afu.setAccessType(at.code);
            		afuDao.update(afu);
            	}
            	return afu;
        	}
        }
    }

    @PUT
    @Path("/{authFunc}/roles/_link")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthFuncRole linkRole(@PathParam("authFunc") long authFunc, AuthFuncRoleConfig config) throws SQLException, IOException, DaoException {
        long tx = System.currentTimeMillis();
        LOGGER.info(String.format("[%s][PUT ] v1/funcs/%s/roles/_link? %s", tx, authFunc, this.gson.toJson(config)));
        
        try (Connection conn = AuthDB.create()) {
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
        		afrDao.deleteByPK(authFunc, role.getId());
        		return null;
        	}
        	else {
	        	if(afr == null) {
	        		afr = new AuthFuncRole(authFunc, role.getId(), at.code, config.funcRoleArgs);
	        		afrDao.insert(afr);
	        	}
	        	else {
	        		afr.setAccessType(at.code);
	        		afrDao.update(afr);
	        	}
	        	return afr;
        	}
        }
    }
}
