package fw.auth.web.v1;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uia.auth.AuthValidator.AccessType;
import uia.auth.ee.AuthHeaders;

public abstract class AbstractWebService {

	@Context
    private HttpHeaders httpHeaders;

	protected final Gson gson;

	protected AbstractWebService() {
	    this.gson = new GsonBuilder()
	            .setPrettyPrinting()
	            .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
	            .create();
	}
	
    protected String user() { 
    	String user = this.httpHeaders.getHeaderString(AuthHeaders.ADMIN);
    	if(user != null) {
        	user =this.httpHeaders.getHeaderString(AuthHeaders.USER);
    	}
    	return user;
    }
    
    protected String session() { 
        return this.httpHeaders.getHeaderString(AuthHeaders.SESSION);
    }
    
    protected AccessType access() {
    	String at = this.httpHeaders.getHeaderString(AuthHeaders.ACCESS);
        return at == null ? AccessType.READONLY : AccessType.valueOf(at);
    }
}
