package fw.auth.web.v1.model;

public class AuthFuncUserConfig {

	public long authUser;
	
	public String accessType;
	
	public String funcUserArgs;
	
	public AuthFuncUserConfig() {
		this.accessType = "DENY";
	}
}
