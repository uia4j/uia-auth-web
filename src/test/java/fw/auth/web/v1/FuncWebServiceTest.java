package fw.auth.web.v1;

import org.junit.Test;

import fw.auth.web.v1.model.AuthFuncDetail;

public class FuncWebServiceTest extends AbstractWebServiceTest {

    @Test
    public void testQueryAll() throws Exception {
    	FuncWebService serv = new FuncWebService();
    	serv.queryTree().forEach(n -> n.println(""));
    }

    @Test
    public void testQueryDetail() throws Exception {
    	FuncWebService serv = new FuncWebService();
    	AuthFuncDetail detail = serv.queryAccess(10015);
    	System.out.println("users");
    	detail.users.forEach(System.out::println);
    	System.out.println("roles");
    	detail.roles.forEach(System.out::println);
    	
    }
}
