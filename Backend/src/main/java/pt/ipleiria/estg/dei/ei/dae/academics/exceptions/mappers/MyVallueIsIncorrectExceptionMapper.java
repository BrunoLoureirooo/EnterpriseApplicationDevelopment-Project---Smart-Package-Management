package pt.ipleiria.estg.dei.ei.dae.academics.exceptions.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.util.logging.Logger;

@Provider
public class MyVallueIsIncorrectExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<MyValueIsIncorrectException> {
    private static final Logger logger =
            Logger.getLogger(MyValueIsIncorrectException.class.getCanonicalName());
    
    @Override
    public Response toResponse(MyValueIsIncorrectException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMsg)
                .build();
    }
}
