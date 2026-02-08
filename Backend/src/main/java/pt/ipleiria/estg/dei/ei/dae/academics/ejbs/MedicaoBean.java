package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.*;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoProduto;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.util.List;

@Stateless // Entreprise Java Bean (EJB) sem estado, ou seja, não guarda estado entre pedidos.
public class MedicaoBean {
    @PersistenceContext
    private EntityManager entityManager; // o 'EntityManager' é uma implementação do Hibernate.

    public List<Medicao> getMedicoes(long sensorId) {
        return entityManager.createNamedQuery("getMedicaoBySensor", Medicao.class)
                .setParameter("sensorId", sensorId)
                .getResultList();
    }


    public Medicao create(long sensor_id, float valor, long data_hora) throws MyEntityNotFoundException, MyValueIsIncorrectException {
        var sensor = entityManager.find(Sensor.class, sensor_id);
        if(sensor == null){
            throw new MyEntityNotFoundException("Sensor " + sensor_id + " not found");
        }

        Volume volume = sensor.getVolume();
        List<VolumeProduto> produtos_id=volume.getVolumeProdutos();
        if(!produtos_id.isEmpty()){
            List<Medicao> medicoes = getMedicoes(sensor_id);
            for (VolumeProduto volumeProduto : produtos_id) {
                Produto produto = volumeProduto.getProduto();
                if(produto.getTresholdTipo()== sensor.getTipo()){
                    int counter=0;
                    if(volumeProduto.getEstado().equals(EstadoProduto.ESTRAGADO)){
                        continue;
                    }
                    for (Medicao medicao : medicoes) {
                        if(medicao.getValor() > volumeProduto.getProduto().getTreshold()){
                            counter++;
                            if(counter >= 3){
                                volumeProduto.setEstado(EstadoProduto.ESTRAGADO);
                                entityManager.merge(volumeProduto);
                                break;
                            }
                        }
                    }
                }
            }
        }

        var medicao = new Medicao(sensor, valor, data_hora);
        entityManager.persist(medicao);
        entityManager.merge(sensor); // Persiste (adiciona) os dados na base de dados. NOTA: para update será utilizado o metodo 'merge()' em alternativa ao 'persist()'
        return medicao;
    }

    public List<Medicao> findAll() {
        // remember, maps to: “SELECT s FROM Student s ORDER BY s.name”
        return entityManager.createNamedQuery("getAllMedicoes", Medicao.class).getResultList();
    }

    public Medicao find(String id) throws MyEntityNotFoundException {
        var medicao = entityManager.find(Medicao.class, id);
        if (medicao == null) {
            throw new MyEntityNotFoundException("Medicao " + id + " not found");
        }
        return medicao;
    }

    public List<Medicao> findBySensor(long sensor_id) throws MyEntityNotFoundException {
        var sensor = entityManager.find(Sensor.class, sensor_id);
        if(sensor == null){
            throw new MyEntityNotFoundException("sensor " + sensor_id + " not found");
        }
        return entityManager.createNamedQuery("getMedicaoBySensor", Medicao.class).setParameter("sensorId", sensor_id).getResultList();
    }

    public void deleteBySensor(long sensor_id) {
        entityManager.createNamedQuery("deleteMedicaoBySensor").setParameter("sensorId", sensor_id).executeUpdate();
    }
}