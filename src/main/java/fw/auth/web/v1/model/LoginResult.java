package fw.auth.web.v1.model;

public class LoginResult {

	public String userId;

	public String userName;

	public int result;

	public String session;

	public String message;
	
	public LoginResult() {
	}
	
	public LoginResult(String userId, String userName, int result, String session, String message) {
		this.userId = userId;
		this.userName = userName;
		this.result = result;
		this.session = session;
		this.message = message;
	}
}
