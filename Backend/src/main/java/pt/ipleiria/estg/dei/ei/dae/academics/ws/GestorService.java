package pt.ipleiria.estg.dei.ei.dae.academics.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.ClienteDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.GestorDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.ClienteBean;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.GestorBean;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Cliente;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Gestor;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Authenticated;

import java.util.List;

@Path("gestor") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@Authenticated // this service requires authentication
@RolesAllowed({"Gestor"}) // this service requires the user to have the role “Gestor”
public class GestorService {
    @EJB
    private GestorBean gestorBean;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/student/”
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGestores() {
        List<Gestor> gestores = gestorBean.findAll();
        return Response.ok(GestorDTO.from(gestores)).build();
    }

    @GET
    @Path("/{gestor_username}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGestorByUsername(@PathParam("gestor_username") String gestorUsername) throws MyEntityNotFoundException {
        Gestor gestor = gestorBean.find(gestorUsername);
        if (gestor == null) {
            throw new MyEntityNotFoundException("Gestor with username " + gestorUsername + " not found");
        }
        return Response.ok(GestorDTO.from(gestor)).build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createGestor(GestorDTO gestorDTO) throws MyConstraintViolationException, MyValueIsIncorrectException, MyEntityExistsException {
        Gestor gestor = gestorBean.create(gestorDTO.getUsername(), gestorDTO.getPassword(), gestorDTO.getNome(), gestorDTO.getEmail());
        return Response.status(Response.Status.CREATED).entity(GestorDTO.from(gestor)).build();
    }

    //update method
    @PATCH
    @Path("/{gestor_username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateGestor(@PathParam("gestor_username") String gestorUsername, GestorDTO gestorDTO) throws MyEntityNotFoundException, MyConstraintViolationException, MyValueIsIncorrectException {
        // with try-catch block, we can catch the exception and return a response with the error message
        try {
            gestorBean.update(gestorUsername, gestorDTO.getNome(), gestorDTO.getEmail());
            Gestor gestor = gestorBean.find(gestorUsername);
            return Response.ok(GestorDTO.from(gestor)).build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    //delete method
    @DELETE
    @Path("/{gestor_username}")
    public Response deleteGestor(@PathParam("gestor_username") String gestorUsername) {
        // with try-catch block, we can catch the exception and return a response with the error message
        try {
            gestorBean.delete(gestorUsername);
            return Response.ok().build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
