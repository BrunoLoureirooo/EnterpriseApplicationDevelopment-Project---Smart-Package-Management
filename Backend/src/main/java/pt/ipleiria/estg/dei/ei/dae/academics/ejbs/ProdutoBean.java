package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.*;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.CategoriaProduto;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.TipoSensor;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.util.Arrays;
import java.util.List;

@Stateless // Entreprise Java Bean (EJB) sem estado, ou seja, não guarda estado entre pedidos.
public class ProdutoBean {
    @PersistenceContext
    private EntityManager entityManager; // o 'EntityManager' é uma implementação do Hibernate.
    public Produto create(String nome, String descricao, String categoria, float threshold, String threshold_tipo) throws MyEntityNotFoundException, MyValueIsIncorrectException {
        CategoriaProduto categoriaProduto;
        try{
            categoriaProduto = CategoriaProduto.valueOf(categoria.toUpperCase());
        }catch(IllegalArgumentException e){
            throw new MyValueIsIncorrectException("Invalid categoria value: "
                    + categoria + ". Allowed ones are: " + Arrays.toString(CategoriaProduto.values()));
        }
        TipoSensor tipoThreshold;
        try{
            tipoThreshold = TipoSensor.valueOf(threshold_tipo.toUpperCase());
        }catch(IllegalArgumentException e){
            throw new MyValueIsIncorrectException("Invalid threshold_tipo value: "
                    + threshold_tipo + ". Allowed ones are: " + Arrays.toString(TipoSensor.values()));
        }
        if(threshold < 0){
            throw new MyValueIsIncorrectException("Threshold value must be positive");
        }

        var produto = new Produto(nome, descricao, String.valueOf(categoriaProduto), threshold, tipoThreshold);
        entityManager.persist(produto);
        return produto;
    }

    public List<Produto> findAll() {
        // remember, maps to: “SELECT s FROM Student s ORDER BY s.name”
        return entityManager.createNamedQuery("getAllProdutos", Produto.class).getResultList();
    }

    public Produto find(long id) throws MyEntityNotFoundException {
        var produto = entityManager.find(Produto.class, id);
        if (produto == null) {
            throw new MyEntityNotFoundException("Produto #" + id + " not found");
        }
        return produto;
    }

    public void addProductToVolume(long id, long volume_id) throws MyEntityNotFoundException, MyValueIsIncorrectException {
        Produto produto = entityManager.find(Produto.class, id);
        //check if subject exists using find method from SubjectBean
        if (produto == null) {
            throw new MyEntityNotFoundException("Produto #" + id + " not found");
        }
        Volume volume = entityManager.find(Volume.class, volume_id);
        if (volume == null) {
            throw new MyEntityNotFoundException("Volume #" + volume_id + " not found");
        }
        VolumeProduto volumeProduto = VolumeProdutoBean.find(volume, produto);

        //add the volumeProduct to the volume and produto
        volume.addVolumeProduto(volumeProduto);
        produto.addVolumeProduto(volumeProduto);

        //save the new volumeProduct
        entityManager.persist(volumeProduto);
        entityManager.merge(volume);
        entityManager.merge(produto);
    }

    public void delete(long produtoId) throws Exception {
        try{
            Produto produto = find(produtoId);
            entityManager.remove(produto);
        }catch (MyEntityNotFoundException e){
            throw new MyEntityNotFoundException("Product #" + produtoId + " not found");
        }catch (Exception e){
            throw new Exception("Product #" + produtoId + " cannot be deleted");
        }
    }

    public Produto update(long produtoId, String nome, String descricao, String categoria, float treshold, String tresholdTipo) throws MyEntityNotFoundException {
        Produto produto = entityManager.find(Produto.class, produtoId);
        if (produto == null) {
            throw new MyEntityNotFoundException("Produto #" + produtoId + " not found");
        }
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setCategoria(categoria);
        produto.setTreshold(treshold);
        produto.setTresholdTipo(TipoSensor.valueOf(tresholdTipo.toUpperCase()));
        entityManager.merge(produto);
        return produto;
    }
}