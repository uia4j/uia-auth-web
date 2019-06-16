package fw.auth.web;

import javax.ws.rs.core.HttpHeaders;

import uia.auth.ee.AuthHeaders;

public interface AppConst {

    public static String user(HttpHeaders httpHeaders) { 
        return httpHeaders == null 
                ? "admin"
                : httpHeaders.getHeaderString(AuthHeaders.USER);
    }
    
    public static String session(HttpHeaders httpHeaders) { 
        return httpHeaders == null 
                ? "-"
                : httpHeaders.getHeaderString(AuthHeaders.SESSION);
    }

}
