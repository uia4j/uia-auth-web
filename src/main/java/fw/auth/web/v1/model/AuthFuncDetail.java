package fw.auth.web.v1.model;

import java.util.ArrayList;
import java.util.List;

import uia.auth.AuthValidator.AccessType;
import uia.auth.db.ViewAuthFuncRole;
import uia.auth.db.ViewAuthFuncUser;

public class AuthFuncDetail {

	public List<UserInfo> users;

	public List<RoleInfo> roles;
	
	public AuthFuncDetail() {
		this.users = new ArrayList<UserInfo>();
		this.roles = new ArrayList<RoleInfo>();
	}
	
	public AuthFuncDetail(List<UserInfo> users, List<RoleInfo> roles) {
		this.users = users;
		this.roles = roles;
	}
	
	public static class UserInfo {
		
		private long authFunc;
		
		private long authUser;
		
		private String userId;
		
		private String userName;
		
		private AccessType accessType;
		
		private boolean enabled;
				
		private String funcUserArgs;

		public UserInfo() {
		}

		public UserInfo(ViewAuthFuncUser user) {
			this.authFunc = user.getAuthFunc();
			this.authUser = user.getAuthUser();
			this.userId = user.getUserId();
			this.userName = user.getUserName();
			this.accessType = AccessType.codeOf(user.getAccessType());
			this.enabled = "Y".equalsIgnoreCase(user.getUserEnabled());
			this.funcUserArgs = user.getFuncUserArgs();
		}

		public long getAuthFunc() {
			return authFunc;
		}

		public long getAuthUser() {
			return authUser;
		}

		public String getUserId() {
			return userId;
		}

		public String getUserName() {
			return userName;
		}

		public AccessType getAccessType() {
			return accessType;
		}

		public boolean isEnabled() {
			return this.enabled;
		}

		public String getFuncUserArgs() {
			return funcUserArgs;
		}

	}

	public static class RoleInfo {
		
		private long authFunc;
		
		private long authRole;
		
		private String roleName;
		
		private AccessType accessType;
		
		private boolean enabled;
		
		private String funcRoleArgs;
		
		public RoleInfo() {
		}

		public RoleInfo(ViewAuthFuncRole role) {
			this.authFunc = role.getAuthFunc();
			this.authRole = role.getAuthRole();
			this.roleName = role.getRoleName();
			this.accessType = AccessType.codeOf(role.getAccessType());
			this.enabled = "Y".equalsIgnoreCase(role.getRoleEnabled());
			this.funcRoleArgs = role.getFuncRoleArgs();
		}

		public long getAuthFunc() {
			return authFunc;
		}

		public long getAuthRole() {
			return authRole;
		}

		public String getRoleName() {
			return roleName;
		}

		public AccessType getAccessType() {
			return accessType;
		}

		public boolean isEnabled() {
			return this.enabled;
		}

		public String getFuncRoleArgs() {
			return funcRoleArgs;
		}
	}
}
