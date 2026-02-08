package pt.ipleiria.estg.dei.ei.dae.academics.ws;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.ClienteDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.ProdutoDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.ClienteBean;
import jakarta.ejb.EJB;
import jakarta.ws.rs.core.MediaType;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.ProdutoBean;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Cliente;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Produto;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Authenticated;

import java.util.List;
@Path("produto") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class ProdutoService {
    @EJB
    private ProdutoBean produtoBean;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/student/”
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor","Cliente","Funcionario"})
    public Response getAllProdutos() {
        List<Produto> produtos = produtoBean.findAll();
        return Response.ok(ProdutoDTO.from(produtos)).build();
    }

    @GET
    @Path("/{produto_id}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor","Cliente","Funcionario"})
    public Response getProdutoByNome(@PathParam("produto_id") long produtoId) throws MyEntityNotFoundException {
        Produto produto = produtoBean.find(produtoId);
        if (produto == null) {
            throw new MyEntityNotFoundException("Product #" + produtoId + " not found");
        }
        return Response.ok(ProdutoDTO.from(produto)).build();
    }

    @POST
    @Path("/")
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response createProduto(ProdutoDTO produtoDTO) throws MyEntityNotFoundException {
        try{
            Produto produto = produtoBean.create(produtoDTO.getNome(), produtoDTO.getDescricao(), produtoDTO.getCategoria(), produtoDTO.getTreshold(), produtoDTO.getTresholdTipo());
            return Response.ok(ProdutoDTO.from(produto)).build();
        } catch (MyValueIsIncorrectException e){
            throw new MyEntityNotFoundException("Product with name " + produtoDTO.getNome() + " already exists");
        }
    }

    @DELETE
    @Path("/{produto_id}")
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response deleteProduto(@PathParam("produto_id") long produtoId) throws Exception {
        try {
            produtoBean.delete(produtoId);
            return Response.ok().build();
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException("Product #" + produtoId + " not found");
        } catch (Exception e){
            throw new Exception("Product #" + produtoId + " not found");
        }
    }

    @PATCH
    @Path("/{produto_id}")
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response updateProduto(@PathParam("produto_id") long produtoId, ProdutoDTO produtoDTO) throws MyEntityNotFoundException {
        try {
            Produto produto = produtoBean.update(produtoId, produtoDTO.getNome(), produtoDTO.getDescricao(), produtoDTO.getCategoria(), produtoDTO.getTreshold(), produtoDTO.getTresholdTipo());
            return Response.ok(ProdutoDTO.from(produto)).build();
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException("Product #" + produtoId + " not found");
        }
    }

}