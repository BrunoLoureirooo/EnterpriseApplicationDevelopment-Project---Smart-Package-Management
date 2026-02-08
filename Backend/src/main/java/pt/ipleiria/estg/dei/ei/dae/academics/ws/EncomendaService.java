package pt.ipleiria.estg.dei.ei.dae.academics.ws;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.EncomendaDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.VolumeDTO;
import jakarta.ejb.EJB;
import jakarta.ws.rs.core.MediaType;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.*;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Encomenda;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Volume;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoEncomenda;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Authenticated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("encomenda") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class EncomendaService {
    @EJB
    private EncomendaBean encomendaBean;
    @EJB
    private VolumeBean volumeBean;


    @Context
    private SecurityContext securityContext;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/student/”
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response getAllEncomendas() {
        return Response.ok(EncomendaDTO.from(encomendaBean.findAll())).build();
    }

    @GET
    @Path("/{encomenda_id}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor","Cliente","Funcionario"})
    public Response getEncomendaByID(@PathParam("encomenda_id") Long encomendaID) throws MyEntityNotFoundException {
        Encomenda encomenda = encomendaBean.find(encomendaID);

        var principal = securityContext.getUserPrincipal();

        if(!principal.getName().equals(encomenda.getCliente().getUsername()) && !securityContext.isUserInRole("Gestor")){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (encomenda == null) {
            throw new MyEntityNotFoundException("Product with name " + encomendaID + " not found");
        }
        return Response.ok(EncomendaDTO.from(encomenda)).build();
    }
    @GET
    @Path("/estado/{encomenda_estado}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response getEncomendaByEstado(@PathParam("encomenda_estado") String encomendaEstado) throws MyValueIsIncorrectException {
        EstadoEncomenda estadoEncomenda;
        try{
            estadoEncomenda = EstadoEncomenda.valueOf(encomendaEstado.toUpperCase());
        }catch(IllegalArgumentException e){
            throw new MyValueIsIncorrectException("Invalid estado value: "
                    + encomendaEstado + ". Allowed ones are: " + Arrays.toString(EstadoEncomenda.values()));
        }
        return Response.ok(EncomendaDTO.from(encomendaBean.findByEstado(String.valueOf(estadoEncomenda)))).build();
    }

    @GET
    @Path("/cliente/{cliente_username}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Cliente","Gestor"})
    public Response getEncomendaByCliente(@PathParam("cliente_username") String clienteUsername){

        var principal = securityContext.getUserPrincipal();

        if(!principal.getName().equals(clienteUsername) && !securityContext.isUserInRole("Gestor")){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(EncomendaDTO.from(encomendaBean.findByCliente(clienteUsername))).build();
    }


    @POST
    @Path("/")
    @Authenticated
    @RolesAllowed({"Cliente"})
    public Response createEncomenda(EncomendaDTO encomendaDTO) {

        var principal = securityContext.getUserPrincipal();

        if(!principal.getName().equals(encomendaDTO.getClienteUsername())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        try {
            // Prepare the product list for the EncomendaBean
            List<Volume> volumes = new ArrayList<>();
            for (Long volumeID : encomendaDTO.getVolumeIds()) {
                // Assuming a quantity of 1 for each product by default
                volumes.add(volumeBean.find(volumeID));
            }

            // Call the business logic to create the entity
            Encomenda encomenda = encomendaBean.create(
                    volumes,
                    encomendaDTO.getMorada(),
                    encomendaDTO.getClienteUsername()
            );

            // Convert the created entity into a DTO for the response
            EncomendaDTO createdEncomendaDTO = EncomendaDTO.from(encomenda);

            return Response.status(Response.Status.CREATED).entity(createdEncomendaDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Path("/{encomenda_id}")
    @Authenticated
    @RolesAllowed({"Gestor","Funcionario"})
    public Response updateEncomenda(@PathParam("encomenda_id") long encomendaId, EncomendaDTO encomendaDTO) throws MyEntityNotFoundException {
        try {
            Encomenda encomenda = encomendaBean.update(encomendaId, encomendaDTO.getEstado(),encomendaDTO.getMorada());
            return Response.ok(EncomendaDTO.from(encomenda)).build();
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException("Encomenda #" + encomendaId + " not found");
        } catch (MyValueIsIncorrectException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/{encomenda_id}/volumes") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor","Cliente","Funcionario"})
    public Response getVolumesByEncomendaID(@PathParam("encomenda_id") Long encomendaID) throws MyEntityNotFoundException {
        Encomenda encomenda = encomendaBean.find(encomendaID);

        var principal = securityContext.getUserPrincipal();

        if(!principal.getName().equals(encomenda.getCliente().getUsername()) && !securityContext.isUserInRole("Gestor")){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (encomenda == null) {
            throw new MyEntityNotFoundException("Product with name " + encomendaID + " not found");
        }
        List<VolumeDTO> volumeDTOS = VolumeDTO.from(encomenda.getVolumes());
        return Response.ok(volumeDTOS).build();
    }

}