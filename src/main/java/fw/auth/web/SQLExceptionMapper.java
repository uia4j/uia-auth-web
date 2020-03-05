package fw.auth.web;

import java.sql.SQLException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException> {

    @Override
    public Response toResponse(SQLException e) {
        return Response
                .status(Response.Status.EXPECTATION_FAILED)
                .header("Reason", "SQL: " + e.getMessage())
                .type("text/plain")
                .build();
    }
}