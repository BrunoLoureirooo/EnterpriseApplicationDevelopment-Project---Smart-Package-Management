package pt.ipleiria.estg.dei.ei.dae.academics.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.EmailDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.EmailBean;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.UtilizadorBean;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Authenticated;

@Path("email")
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@Authenticated
@RolesAllowed({"Gestor","Funcionario","Cliente"})
public class EmailService {
    @EJB
    private EmailBean emailBean;
    @EJB
    private UtilizadorBean utilizadorBean;

    @POST
    @Path("/{username}/send")
    public Response sendEmail(@PathParam("username") String username, EmailDTO emailDTO) {
        var utilizador = utilizadorBean.findOrFail(username);
        emailBean.send(utilizador.getEmail(), emailDTO.getSubject(), emailDTO.getBody());
        return Response.ok().entity("E-mail sent").build();
    }
}
