package fw.auth.web;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import uia.dao.DaoException;

@Provider
public class DaoExceptionMapper implements ExceptionMapper<DaoException> {

    @Override
    public Response toResponse(DaoException e) {
        return Response
                .status(Response.Status.EXPECTATION_FAILED)
                .header("Reason", "DAO: " + e.getMessage())
                .type("text/plain")
                .build();
    }
}