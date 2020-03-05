package fw.auth.web.v1.model;

public class ScanRequest {

	private String userId;
	
	private String funcName;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	
}
