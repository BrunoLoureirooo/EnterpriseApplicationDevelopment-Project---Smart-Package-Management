package pt.ipleiria.estg.dei.ei.dae.academics.exceptions.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyArgumentsNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.util.logging.Logger;

@Provider
public class MyArgumentsNotFoundExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<MyArgumentsNotFoundException> {
    private static final Logger logger =
            Logger.getLogger(MyArgumentsNotFoundException.class.getCanonicalName());
    
    @Override
    public Response toResponse(MyArgumentsNotFoundException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMsg)
                .build();
    }
}
