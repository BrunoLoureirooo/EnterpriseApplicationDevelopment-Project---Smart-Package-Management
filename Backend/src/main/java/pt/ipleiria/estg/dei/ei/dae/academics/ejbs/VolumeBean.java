package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Produto;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Volume;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Encomenda;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.VolumeProduto;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoEncomenda;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoProduto;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoVolume;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.util.*;


@Stateless
public class VolumeBean {

    @PersistenceContext
    private EntityManager entityManager;

    // Create a new volume
    public List<Volume> create(List<Object[]> produtos) throws MyEntityExistsException, MyValueIsIncorrectException, MyEntityNotFoundException {
        // Map to group products by their category
        Map<String, List<Object[]>> produtosByCategoria = new HashMap<>();

        // Group products by category
        for (Object[] produto : produtos) {
            Produto p = entityManager.find(Produto.class, produto[0]);
            if (p == null) {
                throw new MyEntityNotFoundException("Produto not found");
            }
            String categoria = p.getCategoria(); // Assuming Produto has a getCategoria() method
            produtosByCategoria
                    .computeIfAbsent(categoria, k -> new ArrayList<>())
                    .add(produto);
        }

        List<Volume> createdVolumes = new ArrayList<>();

        // Create a Volume for each category group
        for (Map.Entry<String, List<Object[]>> entry : produtosByCategoria.entrySet()) {
            String categoria = entry.getKey();
            List<Object[]> produtosInCategoria = entry.getValue();

            EstadoVolume estadoVolume = EstadoVolume.valueOf("ATIVO");
            Volume newVolume = new Volume(estadoVolume);

            for (Object[] produto : produtosInCategoria) {
                Produto p = entityManager.find(Produto.class, produto[0]);
                VolumeProduto volumeProduto = new VolumeProduto();
                volumeProduto.setProduto(p);
                volumeProduto.setVolume(newVolume);
                volumeProduto.setQuantidade((int) produto[1]);
                volumeProduto.setEstado(EstadoProduto.NOVO);
                newVolume.addVolumeProduto(volumeProduto);
                entityManager.persist(volumeProduto);
            }

            // Persist the new Volume
            entityManager.persist(newVolume);
            createdVolumes.add(newVolume);
        }

        return createdVolumes;
    }


    // Find a volume by its ID
    public Volume find(long id) throws MyEntityNotFoundException {
        Volume find = entityManager.find(Volume.class, id);
        if (find == null) {
            throw new MyEntityNotFoundException("Volume with ID " + id + " does not exist.");
        }
        return find;
    }

    // Find all volumes
    public List<Volume> findAll() {
        return entityManager.createNamedQuery("getAllVolumes", Volume.class).getResultList();
    }

    // Add a product to a volume
    public void addProductToVolume(long volumeId, long productId, int quantidade, EstadoProduto estado) throws MyEntityNotFoundException, MyValueIsIncorrectException {
        // Find the Volume by ID
        Volume volume = entityManager.find(Volume.class, volumeId);
        if (volume == null) {
            throw new MyEntityNotFoundException("Volume with ID " + volumeId + " does not exist.");
        }

        // Find the Product (Produto) by ID
        Produto produto = entityManager.find(Produto.class, productId);
        if (produto == null) {
            throw new MyEntityNotFoundException("Produto with ID " + productId + " does not exist.");
        }

        // Create a new VolumeProduto entity to track the relationship between Volume and Produto
        VolumeProduto volumeProduto = new VolumeProduto(volume, produto, quantidade, estado);

        // Add the VolumeProduto to the Volume and Produto
        volume.addVolumeProduto(volumeProduto);  // Update the Volume's list of VolumeProdutos
        produto.addVolumeProduto(volumeProduto); // Update the Produto's list of VolumeProdutos

        // Persist the new VolumeProduto entity
        entityManager.persist(volumeProduto);

        // Optionally, you can merge the Volume and Produto if necessary to update their associations
        entityManager.merge(volume);
        entityManager.merge(produto);
    }

    public Volume update(long volumeID, EstadoVolume estado) throws MyValueIsIncorrectException, MyEntityNotFoundException {
        Volume volume = entityManager.find(Volume.class, volumeID);
        if (volume == null) {
            throw new MyEntityNotFoundException("Encomenda #" + volumeID + " not found");
        }
        volume.setEstado(estado);
        return volume;
    }
}
