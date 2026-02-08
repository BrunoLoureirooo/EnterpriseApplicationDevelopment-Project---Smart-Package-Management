package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Produto;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Volume;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.VolumeProduto;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoProduto;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

@Stateless
public class VolumeProdutoBean {

    @PersistenceContext
    private EntityManager entityManager;

    // Metodo para adicionar um Produto a um Volume, criando um VolumeProduto
    public VolumeProduto addProductToVolume(Volume volume, Produto produto, int quantidade, EstadoProduto estado) throws MyEntityNotFoundException, MyValueIsIncorrectException {

        if (volume == null) {
            throw new MyEntityNotFoundException("Volume does not exist.");
        }

        if (produto == null) {
            throw new MyEntityNotFoundException("Produto does not exist.");
        }

        // Criar um novo VolumeProduto
        VolumeProduto volumeProduto = new VolumeProduto(volume, produto, quantidade, estado);

        // Adicionar o VolumeProduto à associação entre Produto e Volume
        volume.addVolumeProduto(volumeProduto);
        produto.addVolumeProduto(volumeProduto);

        // Persistir o VolumeProduto
        entityManager.persist(volumeProduto);

        // Também podemos fazer merge nos objetos Volume e Produto para garantir que as associações sejam atualizadas no banco de dados
        entityManager.merge(volume);
        entityManager.merge(produto);

        return volumeProduto;
    }

    // Metodo para remover um Produto de um Volume (e possivelmente deletar o VolumeProduto)
    public void removeProductFromVolume(long volumeId, long produtoId) throws MyEntityNotFoundException, MyValueIsIncorrectException {
        // Encontrar o Volume e Produto
        Volume volume = entityManager.find(Volume.class, volumeId);
        Produto produto = entityManager.find(Produto.class, produtoId);

        if (volume == null) {
            throw new MyEntityNotFoundException("Volume with ID " + volumeId + " does not exist.");
        }

        if (produto == null) {
            throw new MyEntityNotFoundException("Produto with ID " + produtoId + " does not exist.");
        }

        // Encontrar o VolumeProduto (a relação entre Volume e Produto)
        VolumeProduto volumeProduto = find(volume, produto);

        // Remover o VolumeProduto das coleções Volume e Produto
        if (volumeProduto != null) {
            volume.removeVolumeProduto(volumeProduto);
            produto.removeVolumeProduto(volumeProduto);

            // Remover o VolumeProduto da base de dados
            entityManager.remove(volumeProduto);

            // Persistir as alterações nas entidades Volume e Produto
            entityManager.merge(volume);
            entityManager.merge(produto);
        } else {
            throw new MyEntityNotFoundException("VolumeProduto with Volume ID " + volumeId + " and Produto ID " + produtoId + " does not exist.");
        }
    }

    // Metodo auxiliar para encontrar o VolumeProduto
    public static VolumeProduto find(Volume volume, Produto produto) {
        for (VolumeProduto volumeProduto : volume.getVolumeProdutos()) {
            if (volumeProduto.getProduto().getId() == produto.getId()) {
                return volumeProduto;
            }
        }
        return null;
    }
}
