package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.SensorAuthDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.SensorAuth;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Hasher;

import java.util.List;

@Stateless
public class SensorAuthBean {
    // Injeção do EntityManager
    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private SensorBean sensorBean;

    private Hasher hasher = new Hasher();

    // Criar um novo SensorAuth
    public SensorAuth create(String username, String password, String nome, String email, long sensor_id) throws Exception {
        validateInput(username, password, nome, email, sensor_id);

        // Verificar se já existe um SensorAuth com o mesmo username
        if (find(username) != null) {
            throw new MyEntityExistsException("Já existe um funcionário com o username: " + username);
        }

        Sensor sensor = entityManager.find(Sensor.class, sensor_id);

        // Criar um novo SensorAuth
        try{
            SensorAuth SensorAuth = new SensorAuth(username, hasher.hash(password), nome, email, sensor);
            entityManager.persist(SensorAuth);
            sensorBean.setSensorAuth(SensorAuth.getSensor().getId(),SensorAuth.getUsername());
            return SensorAuth;
        } catch (ConstraintViolationException | MyEntityNotFoundException e){
            throw new Exception(e);
        }

    }

    // Atualizar um SensorAuth
    public void update(String username, String nome, String email) throws MyValueIsIncorrectException, MyEntityExistsException, MyConstraintViolationException, MyEntityNotFoundException {
        validateInput(username, nome, email);

        SensorAuth SensorAuth = find(username);
        if (SensorAuth != null) {
            try{
                SensorAuth.setNome(nome);
                SensorAuth.setEmail(email);
                entityManager.merge(SensorAuth);
            } catch (ConstraintViolationException e){
                throw new MyConstraintViolationException(e);
            }
        }else{
            throw new MyEntityNotFoundException("SensorAuth não encontrado");
        }

    }

    // Remover um SensorAuth
    public void delete(String username) throws MyValueIsIncorrectException {
        SensorAuth SensorAuth = find(username);
        if (SensorAuth != null) {
            entityManager.remove(SensorAuth);
        }else{
            throw new MyValueIsIncorrectException("SensorAuth não encontrado");
        }

    }

    // Validação de entrada
    private void validateInput(String username, String password, String nome, String email, long sensor_id) throws MyValueIsIncorrectException {
        validateInput(username, nome, email);
        if (email == null || email.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Email não pode ser vazio");
        }
        if (sensor_id <= 0) {
            Sensor sensor = entityManager.find(Sensor.class, sensor_id);
            if (sensor == null) {
                throw new MyValueIsIncorrectException("Sensor não encontrado para o ID: " + sensor_id);
            }
            throw new MyValueIsIncorrectException("Sensor ID não pode ser menor ou igual a 0");
        }
    }

    private void validateInput(String username, String nome, String email) throws MyValueIsIncorrectException {
        if (username == null || username.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Username não pode ser vazio");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Nome não pode ser vazio");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Email não pode ser vazio");
        }
    }

    // Encontrar um SensorAuth pelo username
    public SensorAuth find(String username) {
        return entityManager.find(SensorAuth.class, username);
    }

    // Remover um SensorAuth pelo username
    public void remove(String username) throws MyValueIsIncorrectException {
        SensorAuth SensorAuth = find(username);
        if (SensorAuth == null) {
            throw new MyValueIsIncorrectException("SensorAuth não encontrado para o username: " + username);
        }
        entityManager.remove(SensorAuth);
    }

    // Encontrar todos os SensorAuths
    public List<SensorAuth> findAll() {
        return entityManager.createNamedQuery("getAllSensoresAuth", SensorAuth.class).getResultList();
    }

    public SensorAuthDTO findBySensor(Long sensorID) throws MyEntityNotFoundException {
        Sensor sensor = entityManager.find(Sensor.class, sensorID);
        if (sensor == null) {
            throw new MyEntityNotFoundException("Sensor with ID " + sensorID + " not found");
        }
        return SensorAuthDTO.from(sensor.getSensorAuth());
    }
}
