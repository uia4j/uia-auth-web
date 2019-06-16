package fw.auth.web.v1;

import org.junit.Test;

import fw.auth.web.v1.model.Job;
import fw.auth.web.v1.model.JobResult;

public class UserWebServiceTest extends AbstractWebServiceTest {

    @Test
    public void testValidate() throws Exception {
    	UserWebService serv = new UserWebService();
    	
    	JobResult result = serv.validate(1, new Job("or", new String[] { "DISP.MDS-HLIS-D711", "AUTH.FUNC.UPDATE" }));
    	System.out.println(result.accessType);
    }


    @Test
    public void testQueryAll() throws Exception {
    	UserWebService serv = new UserWebService();
    	serv.queryAll().forEach(System.out::println);
    }
}
