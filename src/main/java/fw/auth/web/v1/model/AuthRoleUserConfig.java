package fw.auth.web.v1.model;

import uia.auth.db.AuthRole;
import uia.auth.db.AuthUser;

public class AuthRoleUserConfig {

	public AuthUser user;
	
	public boolean selected;
	
	public AuthRoleUserConfig() {
	}
	
	public AuthRoleUserConfig(AuthUser user, boolean selected) {
		this.user = user;
		this.selected = selected;
	}
}
