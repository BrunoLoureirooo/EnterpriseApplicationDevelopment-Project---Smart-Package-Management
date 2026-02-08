package pt.ipleiria.estg.dei.ei.dae.academics.ws;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.EncomendaDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.ProdutoDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.VolumeDTO;
import jakarta.ejb.EJB;
import jakarta.ws.rs.core.MediaType;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.VolumeProdutoDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.EncomendaBean;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.ProdutoBean;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.VolumeBean;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.VolumeProdutoBean;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Encomenda;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Produto;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Volume;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.VolumeProduto;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Authenticated;

import java.util.ArrayList;
import java.util.List;

@Path("volume") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class VolumeService {
    @EJB
    private VolumeBean volumeBean;
    @EJB
    private ProdutoBean produtoBean;
    @EJB
    private VolumeProdutoBean volumeProdutoBean;
    @EJB
    private EncomendaBean encomendaBean;
    @Context
    private SecurityContext securityContext;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/student/”
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor","Funcionario"})
    public Response getAllVolumes() {
        List<Volume> volumes = volumeBean.findAll();
        return Response.ok(VolumeDTO.from(volumes)).build();
    }

    @GET
    @Path("/{volume_id}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor","Funcionario","Cliente"})
    public Response getVolumeById(@PathParam("volume_id") Long volumeID) throws MyEntityNotFoundException {
        Volume volume = volumeBean.find(volumeID);

        var principal = securityContext.getUserPrincipal();

        //if it's a client
        if(securityContext.isUserInRole("Cliente")){
            if(!principal.getName().equals(volume.getEncomenda().getCliente().getUsername())){
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        }

        if (volume == null) {
            throw new MyEntityNotFoundException("Product with name " + volumeID + " not found");
        }
        return Response.status(Response.Status.OK).entity(VolumeDTO.from(volume)).build();
    }

    @GET
    @Path("/{volume_id}/produto/{produto_id}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor","Funcionario","Cliente"})
    public Response getProdutoByIdByVolumeById(@PathParam("volume_id") Long volumeID, @PathParam("produto_id") Long produtoID) throws MyEntityNotFoundException {
        Volume volume = volumeBean.find(volumeID);
        Produto produto = produtoBean.find(produtoID);

        var principal = securityContext.getUserPrincipal();

        if(!principal.getName().equals(volume.getEncomenda().getCliente().getUsername()) && securityContext.isUserInRole("Cliente")){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (volume == null) {
            throw new MyEntityNotFoundException("Volune with id " + volumeID + " not found");
        }
        if (produto == null) {
            throw new MyEntityNotFoundException("Product with id " + produtoID + " not found");
        }

        VolumeProduto volumeProduto = volumeProdutoBean.find(volume, produto);
        if (volumeProduto == null) {
            throw new MyEntityNotFoundException("VolumeProduto with produto " + produtoID + " and volume " + volumeID + " not found");
        }

        //return Response.status(Response.Status.OK).entity(ProdutoDTO.from(produto)).build();
        return Response.status(Response.Status.OK).entity(VolumeProdutoDTO.from(volumeProduto)).build();
    }

    @POST
    @Path("/")
    @Authenticated
    @RolesAllowed({"Cliente"})
    public Response createVolume(VolumeDTO volumeDTO) throws MyEntityNotFoundException {
        try {
            int size = volumeDTO.getProdutosIds().size();
            // Prepare the product list for the EncomendaBean
            List<Object[]> produtos = new ArrayList<>();
            for(int i = 0; i < size; i++){
                produtos.add(new Object[]{volumeDTO.getProdutosIds().get(i), volumeDTO.getProdutosQuants().get(i)});
            }

            // Call the business logic to create the entity
            List<Volume> volumes = volumeBean.create(
                    produtos
            );

            List<VolumeDTO> volumesDTO = new ArrayList<>();
            for (Volume volume : volumes) {
                VolumeDTO createdVolumeDTO = VolumeDTO.from(volume);
                volumesDTO.add(createdVolumeDTO);
            }

            return Response.status(Response.Status.CREATED).entity(volumesDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Path("/{volume_id}")
    @Authenticated
    @RolesAllowed({"Gestor","Funcionario"})
    public Response updateEncomenda(@PathParam("volume_id") long volumeID, VolumeDTO volumeDTO) throws MyEntityNotFoundException {
        try {
            Volume volume = volumeBean.update(volumeID, volumeDTO.getEstado());
            return Response.ok(VolumeDTO.from(volume)).build();
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException("Volume #" + volumeID + " not found");
        } catch (MyValueIsIncorrectException e) {
            throw new RuntimeException(e);
        }
    }



}