package pt.ipleiria.estg.dei.ei.dae.academics.ws;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.ClienteDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.ClienteBean;
import jakarta.ejb.EJB;
import jakarta.ws.rs.core.MediaType;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Cliente;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyArgumentsNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Authenticated;

import java.security.Provider;
import java.util.List;

@Path("cliente") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”

public class ClienteService {
    @EJB
    private ClienteBean clienteBean;

    @Context
    private SecurityContext securityContext;



    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/student/”
    @Authenticated // this service requires authentication
    @RolesAllowed({"Gestor"}) // this service requires the user to have the role “Cliente”
    public List<ClienteDTO> getAllClientes() {
        return ClienteDTO.from(clienteBean.findAll());
    }

    @GET
    @Path("/{client_username}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Cliente","Gestor"})
    public Response getClienteByUsername(@PathParam("client_username") String clientUsername) throws MyEntityNotFoundException {
        var principal = securityContext.getUserPrincipal();

        if(!principal.getName().equals(clientUsername) && !securityContext.isUserInRole("Gestor")){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Cliente cliente = clienteBean.find(clientUsername);

        if (cliente == null) {
            throw new MyEntityNotFoundException("Client with username " + clientUsername + " not found");
        }

        var dto = ClienteDTO.from(cliente);
        return Response.ok(dto).build();
    }

    @POST
    @Path("/")
    public Response createCliente(ClienteDTO clienteDTO) throws MyEntityExistsException, MyArgumentsNotFoundException, MyConstraintViolationException, MyEntityNotFoundException {
        clienteBean.create(clienteDTO.getUsername(), clienteDTO.getPassword(), clienteDTO.getNome(), clienteDTO.getEmail());
        Cliente cliente = clienteBean.find(clienteDTO.getUsername());
        return Response.status(Response.Status.CREATED).entity(ClienteDTO.from(cliente)).build();
    }

    //update method
    // with try-catch block, we can catch the exception and return a response with the error message
    // only the client itself can update its own information or a gestor
    @PATCH
    @Path("/{client_username}")
    @Authenticated
    @RolesAllowed({"Cliente","Gestor"})
    public Response updateCliente(@PathParam("client_username") String clientUsername, ClienteDTO clienteDTO) throws MyEntityNotFoundException, MyConstraintViolationException, MyArgumentsNotFoundException {
        var principal = securityContext.getUserPrincipal();

        if(!principal.getName().equals(clientUsername) && !securityContext.isUserInRole("Gestor")){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        try {
            clienteBean.update(clienteDTO.getUsername(), clienteDTO.getNome(), clienteDTO.getEmail());
            Cliente cliente = clienteBean.find(clienteDTO.getUsername());
            return Response.ok(ClienteDTO.from(cliente)).build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    //delete method
    // with try-catch block, we can catch the exception and return a response with the error message
    // only the client itself can delete its own account or a gestor
    @DELETE
    @Path("/{client_username}")
    @Authenticated
    @RolesAllowed({"Cliente","Gestor"})
    public Response deleteCliente(@PathParam("client_username") String clientUsername) {
        var principal = securityContext.getUserPrincipal();

        if(!principal.getName().equals(clientUsername) && !securityContext.isUserInRole("Gestor")){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        try {
            clienteBean.delete(clientUsername);
            return Response.ok().build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


}