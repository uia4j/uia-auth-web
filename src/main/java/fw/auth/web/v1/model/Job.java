package fw.auth.web.v1.model;

public class Job {

	public String op;
	
	public String[] functions;
	
	public Job() {
		this.op = "and";
		this.functions = new String[0];
	}
	
	public Job(String op, String[] functions) {
		this.op = op;
		this.functions = functions;
	}
}
