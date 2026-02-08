package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Medicao;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.SensorAuth;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Volume;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.TipoSensor;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.util.ArrayList;
import java.util.List;

@Stateless // Entreprise Java Bean (EJB) sem estado, ou seja, não guarda estado entre pedidos.
public class SensorBean {
    @PersistenceContext
    private EntityManager entityManager; // o 'EntityManager' é uma implementação do Hibernate.

    @EJB
    private MedicaoBean medicaoBean;

    public List<Medicao> getHistorico(Sensor sensor) throws MyEntityNotFoundException {
        return medicaoBean.findBySensor(sensor.getId());
    }

    public Sensor create(boolean ativo, String tipo, long volume_id) throws MyEntityExistsException, MyEntityNotFoundException, MyValueIsIncorrectException {
        Volume volume = entityManager.find(Volume.class, volume_id);
        if (volume == null) {
            throw new MyEntityNotFoundException("Volume com o id " + volume_id + " não existe");
        }
        TipoSensor newTipo;
        try{
            newTipo = TipoSensor.valueOf(tipo.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new MyValueIsIncorrectException("Tipo de sensor inválido: "
                    + tipo + ". Tipos válidos são: " + List.of(TipoSensor.values()));
        }

        Sensor sensor = new Sensor(ativo,  newTipo,  volume, null);
        entityManager.persist(sensor); // Persiste (adiciona) os dados na base de dados. NOTA: para update será utilizado o metodo 'merge()' em alternativa ao 'persist()'
        return sensor;
    }

    public List<Sensor> findAll() {
        // remember, maps to: “SELECT s FROM Student s ORDER BY s.name”
        return entityManager.createNamedQuery("getAllSensores", Sensor.class).getResultList();
    }

    public Sensor find(long id) throws MyEntityNotFoundException {
        Sensor sensor = entityManager.find(Sensor.class, id);
        if (sensor == null) {
            throw new MyEntityNotFoundException("Sensor " + id + " não encontrado");
        }
        return sensor;
    }

    //Adicionar sensor a um volume
    public void addSensorToVolume(long id, long volume_id) throws MyEntityNotFoundException {
        Sensor sensor = find(id);
        if (sensor == null) {
            throw new MyEntityNotFoundException("Sensor " + id + " não encontrado");
        }
        Volume volume = entityManager.find(Volume.class, volume_id);
        if (volume == null) {
            throw new MyEntityNotFoundException("Volume " + volume_id + " não encontrado");
        }
        sensor.setVolume(volume);
        volume.addSensor(sensor);
        entityManager.merge(volume);
        entityManager.merge(sensor);
    }

    //Remover sensor de um volume
    public void removeSensorFromVolume(long id) throws MyEntityNotFoundException {
        Sensor sensor = find(id);
        if (sensor == null) {
            throw new MyEntityNotFoundException("Sensor " + id + " não encontrado");
        }
        sensor.setVolume(null);
        Volume volume = entityManager.find(Volume.class, id);
        if (volume == null) {
            throw new MyEntityNotFoundException("Volume " + id + " não encontrado");
        }
        volume.removeSensor(sensor);

        entityManager.merge(volume);
        entityManager.merge(sensor);
    }

    public void removeMedicoesBySensor(long sensor_id) throws MyEntityNotFoundException {
        Sensor sensor = find(sensor_id);
        if (sensor == null) {
            throw new MyEntityNotFoundException("Sensor " + sensor_id + " não encontrado");
        }
        sensor.setHistorico(new ArrayList<>());
        medicaoBean.deleteBySensor(sensor_id);
    }


    public Sensor update(Long sensorID, boolean ativo, String tipo, long volumeId) throws MyEntityNotFoundException, MyValueIsIncorrectException {
        Sensor sensor = entityManager.find(Sensor.class, sensorID);
        if (sensor == null) {
            throw new MyEntityNotFoundException("Sensor com o id " + sensorID + " não existe");
        }
        Volume volume = entityManager.find(Volume.class, volumeId);
        if (volume == null) {
            throw new MyEntityNotFoundException("Volume com o id " + volumeId + " não existe");
        }
        TipoSensor newTipo;
        try{
            newTipo = TipoSensor.valueOf(tipo.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new MyValueIsIncorrectException("Tipo de sensor inválido: "
                    + tipo + ". Tipos válidos são: " + List.of(TipoSensor.values()));
        }
        sensor.setAtivo(ativo);
        sensor.setTipo(newTipo);
        if(sensor.getVolume() != volume){
            removeSensorFromVolume(sensorID);
            addSensorToVolume(sensorID, volumeId);
            removeMedicoesBySensor(sensorID);
        }
        entityManager.merge(sensor);
        return sensor;
    }

    public boolean setSensorAuth(long sensorID, String sensorAuthID) throws MyEntityNotFoundException {
        Sensor sensor = entityManager.find(Sensor.class, sensorID);
        if (sensor == null) {
            throw new MyEntityNotFoundException("Sensor com o id " + sensorID + " não existe");
        }
        SensorAuth sensorAuth = entityManager.find(SensorAuth.class, sensorAuthID);
        if (sensorAuth == null) {
            throw new MyEntityNotFoundException("SensorAuth com o id " + sensorAuthID + " não existe");
        }
        sensor.setSensorAuth(sensorAuth);
        entityManager.merge(sensor);
        return true;
    }
}