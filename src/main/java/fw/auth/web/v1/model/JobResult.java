package fw.auth.web.v1.model;

public class JobResult {

	public String user;
	
	public String[] functions;

	public String accessType;
	
	public JobResult() {
		this.functions = new String[0];
	}
	
	public JobResult(String user, String[] functions, String accessType) {
		this.user = user;
		this.functions = functions;
		this.accessType = accessType;
	}
}
