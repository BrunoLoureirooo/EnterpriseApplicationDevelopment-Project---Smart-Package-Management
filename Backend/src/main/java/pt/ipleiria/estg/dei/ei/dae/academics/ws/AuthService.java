package pt.ipleiria.estg.dei.ei.dae.academics.ws;


import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.AuthDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.ChangePasswordDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.UtilizadorDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.UtilizadorBean;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Authenticated;
import pt.ipleiria.estg.dei.ei.dae.academics.security.TokenIssuer;

@Path("auth")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class AuthService {
    @Inject
    private TokenIssuer issuer;
    @EJB
    private UtilizadorBean utilizadorBean;

    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/login")
    public Response authenticate(@Valid AuthDTO auth) {
        if (utilizadorBean.canLogin(auth.getUsername(), auth.getPassword())) {
            String token = issuer.issue(auth.getUsername());
            return Response.ok(token).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    /*
        receives  olspsswd and newpswd and confirmnewpswd
        checks if olspsswd is correct
        checks if newpswd and confirmnewpswd are equal
        changes the password
     */
    @POST
    @Path("/set-password")
    @Authenticated
    public Response setPassword(@Valid ChangePasswordDTO changePasswordDTO, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();

        if (!utilizadorBean.isPasswordCorrect(username, changePasswordDTO.getOldPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Old password is incorrect").build();
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("New passwords do not match").build();
        }

        utilizadorBean.updatePassword(username, changePasswordDTO.getNewPassword());
        return Response.ok("Password updated successfully").build();
    }

    /*
        returns the role of the user
     */
    @GET
    @Path("/user")
    @Authenticated
    public Response getAuthenticatedUser() {
        var username = securityContext.getUserPrincipal().getName();
        var utilizador = utilizadorBean.findOrFail(username);
        return Response.ok(UtilizadorDTO.from(utilizador)).build();
    }


}