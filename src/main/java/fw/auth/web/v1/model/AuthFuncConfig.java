package fw.auth.web.v1.model;

import java.util.ArrayList;
import java.util.List;

public class AuthFuncConfig {

	public List<FuncUser> users;

	public List<FuncRole> roles;
	
	public AuthFuncConfig() {
		this.users = new ArrayList<FuncUser>();
		this.roles = new ArrayList<FuncRole>();
	}
	
	public static class FuncRole {
		
		public long authRole;
		
		public String accessType;
	}

	
	public static class FuncUser {
		
		public long authUser;
		
		public String accessType;
		
	}
}
