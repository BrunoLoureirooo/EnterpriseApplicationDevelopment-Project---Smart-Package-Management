package pt.ipleiria.estg.dei.ei.dae.academics.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.FuncionarioDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.GestorDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.FuncionarioBean;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.GestorBean;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Funcionario;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Gestor;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Authenticated;

import java.util.List;

@Path("funcionario") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@Authenticated // this service requires authentication
@RolesAllowed({"Funcionario","Gestor"}) // this service requires the user to have the role “Funcionario”
public class FuncionarioService {
    @EJB
    private FuncionarioBean funcionarioBean;

    @Context
    private SecurityContext securityContext;
    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/student/”
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response getAllFuncionarios() {
        List<Funcionario> funcionarios = funcionarioBean.findAll();
        return Response.ok(FuncionarioDTO.from(funcionarios)).build();
    }
    @GET
    @Path("/{funcionario_username}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Funcionario","Gestor"})
    public Response getFuncionarioByUsername(@PathParam("funcionario_username") String funcionarioUsername) throws MyEntityNotFoundException {
        var principal = securityContext.getUserPrincipal();
        if(!principal.getName().equals(funcionarioUsername) && !securityContext.isUserInRole("Gestor")){
            throw new MyEntityNotFoundException("Funcionario with username " + funcionarioUsername + " not found");
        }

        Funcionario funcionario = funcionarioBean.find(funcionarioUsername);
        if (funcionario == null) {
            throw new MyEntityNotFoundException("Funcionario with username " + funcionarioUsername + " not found");
        }
        return Response.ok(FuncionarioDTO.from(funcionario)).build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response createFuncionario(FuncionarioDTO funcionarioDTO) throws MyConstraintViolationException, MyValueIsIncorrectException, MyEntityExistsException {
        try{
            Funcionario funcionario = funcionarioBean.create(funcionarioDTO.getUsername(), funcionarioDTO.getPassword(), funcionarioDTO.getNome(), funcionarioDTO.getEmail());
            return Response.ok(FuncionarioDTO.from(funcionario)).build();
        } catch (MyEntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    // delete a Funcionario
    @DELETE
    @Path("/{funcionario_username}")
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response deleteFuncionario(@PathParam("funcionario_username") String funcionarioUsername) throws MyEntityNotFoundException {
        try {
            funcionarioBean.delete(funcionarioUsername);
            return Response.ok().build();
        } catch (Exception e) {
            throw new MyEntityNotFoundException("Funcionario with username " + funcionarioUsername + " not found");
        }
    }

    // update a Funcionario
    @PATCH
    @Path("/{funcionario_username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response updateFuncionario(@PathParam("funcionario_username") String funcionarioUsername, FuncionarioDTO funcionarioDTO) throws MyEntityNotFoundException, MyValueIsIncorrectException, MyConstraintViolationException, MyEntityExistsException {
        try{
            funcionarioBean.update(funcionarioUsername, funcionarioDTO.getNome(), funcionarioDTO.getEmail());
            Funcionario funcionario = funcionarioBean.find(funcionarioUsername);
            return Response.ok(FuncionarioDTO.from(funcionario)).build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


}
