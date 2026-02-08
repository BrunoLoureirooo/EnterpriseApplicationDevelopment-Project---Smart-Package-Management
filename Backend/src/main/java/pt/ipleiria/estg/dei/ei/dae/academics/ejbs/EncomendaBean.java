package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.*;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoEncomenda;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoProduto;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoVolume;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@Stateless
public class EncomendaBean {
    @PersistenceContext
    private EntityManager entityManager;
    @EJB
    private VolumeBean volumeBean;
    @EJB
    private ClienteBean clienteBean;

    @EJB
    private EmailBean emailBean;

    public Encomenda create(List<Volume> volumes, String morada, String cliente_username) throws MyEntityNotFoundException, MyValueIsIncorrectException, MyEntityExistsException {
        Cliente cliente = clienteBean.find(cliente_username);
        if (cliente == null) {
            throw new MyEntityNotFoundException("Cliente not found");
        }

        // Criar encomenda com estado PENDENTE
        Encomenda encomenda = new Encomenda("PENDENTE", cliente, morada);

        // Associar volumes à encomenda
        for (Volume volume : volumes) {
            encomenda.addVolume(volume);
            volume.setEncomenda(encomenda);
        }

        // Associar o cliente à encomenda
        encomenda.setCliente(cliente);
        cliente.addEncomenda(encomenda);

        // Persistir a encomenda e o cliente
        entityManager.persist(encomenda);
        entityManager.persist(cliente);

        for (Volume volume : volumes) {
            entityManager.merge(volume);
        }

        return encomenda;
    }



    public Encomenda find(long codigo) throws MyEntityNotFoundException {
        Encomenda encomenda = entityManager.find(Encomenda.class, codigo);
        if (encomenda == null) {
            throw new MyEntityNotFoundException("Encomenda com codigo:" + codigo + ", não existe");
        }
        return encomenda;
    }
    public List<Encomenda> findByEstado(String estado) {
        return entityManager
                .createNamedQuery("getEncomendaByEstado", Encomenda.class)
                .setParameter("estado", estado) // Bind the parameter
                .getResultList();
    }

    public List<Encomenda> findByCliente(String username) {
        return entityManager
                .createNamedQuery("getEncomendaByCliente", Encomenda.class)
                .setParameter("username", username) // Bind the parameter
                .getResultList();
    }

    public List<Encomenda> findAll() {
        return entityManager.createNamedQuery("getAllEncomendas", Encomenda.class).getResultList();
    }

    public List<Volume> getVolumes(long codigo) throws MyEntityNotFoundException {
        Encomenda encomenda = this.find(codigo);
        if (encomenda == null) {
            throw new MyEntityNotFoundException("Cliente com username:" + codigo + ", não existe");
        }
        return encomenda.getVolumes();
    }

    public void addVolume(long codigo) throws MyEntityNotFoundException, MyValueIsIncorrectException, MyEntityExistsException {
        Volume volume = volumeBean.find(codigo);
        if(volume == null){
            throw new MyEntityNotFoundException("Volume not found");
        }
        Encomenda encomenda = entityManager.find(Encomenda.class, codigo);
        if (encomenda == null) {
            throw new MyEntityNotFoundException("Encomenda not found");
        }

        encomenda.addVolume(volume);
        volume.setEncomenda(encomenda);
        entityManager.persist(volume);
        entityManager.persist(encomenda);
    }

    public void removeVolume(long encomenda_codigo, long volume_codigo, String estado) throws MyEntityNotFoundException, MyValueIsIncorrectException {
        Encomenda encomenda = entityManager.find(Encomenda.class, encomenda_codigo);
        if (encomenda == null) {
            throw new MyEntityNotFoundException("Encomenda not found");
        }
        Volume volume = volumeBean.find(volume_codigo);
        if(volume == null){
            throw new MyEntityNotFoundException("Volume not found");
        }

        encomenda.removeVolume(volume);
        EstadoVolume estadoVolume;
        try{
            estadoVolume = EstadoVolume.valueOf(estado.toUpperCase());
        }catch(IllegalArgumentException e){
            throw new MyValueIsIncorrectException("Invalid estado value: "
                    + estado + ". Allowed ones are: " + Arrays.toString(EstadoVolume.values()));
        }

        volume.setEstado(estadoVolume);

    }

    public Encomenda update(long encomendaId, String estado, String morada) throws MyValueIsIncorrectException, MyEntityNotFoundException {
        Encomenda encomenda = entityManager.find(Encomenda.class, encomendaId);
        if (encomenda == null) {
            throw new MyEntityNotFoundException("Encomenda #" + encomendaId + " not found");
        }
        EstadoEncomenda estadoEncomenda;
        try{
            estadoEncomenda = EstadoEncomenda.valueOf(estado.toUpperCase());
            if(!encomenda.getEstado().equals(String.valueOf(estadoEncomenda))){
                emailBean.send(encomenda.getCliente().getEmail(), "Estado da encomenda #" + encomenda.getCodigo() + " alterado", "O estado da sua encomenda #" + encomenda.getCodigo() + " foi alterado para " + estado);
            }
            encomenda.setEstado(String.valueOf(estadoEncomenda));
        } catch (IllegalArgumentException e){
            throw new MyValueIsIncorrectException("Invalid estado value: "
                    + estado + ". Allowed ones are: " + Arrays.toString(EstadoEncomenda.values()));
        }
        encomenda.setMorada(morada);
        entityManager.merge(encomenda);
        return encomenda;
    }
}
