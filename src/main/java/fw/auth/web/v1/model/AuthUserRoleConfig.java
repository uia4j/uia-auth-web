package fw.auth.web.v1.model;

import uia.auth.db.AuthRole;

public class AuthUserRoleConfig {

	public AuthRole role;
	
	public boolean selected;
	
	public AuthUserRoleConfig() {
	}
	
	public AuthUserRoleConfig(AuthRole role, boolean selected) {
		this.role = role;
		this.selected = selected;
	}
}
