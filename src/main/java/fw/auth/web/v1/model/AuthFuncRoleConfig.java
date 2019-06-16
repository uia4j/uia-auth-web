package fw.auth.web.v1.model;

public class AuthFuncRoleConfig {

	public long authRole;
	
	public String accessType;
	
	public AuthFuncRoleConfig() {
		this.accessType = "DENY";
	}
}
