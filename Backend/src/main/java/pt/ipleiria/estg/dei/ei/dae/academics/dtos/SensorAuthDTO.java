package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import pt.ipleiria.estg.dei.ei.dae.academics.entities.SensorAuth;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class SensorAuthDTO implements Serializable {

    private String username;
    private String nome;
    private String email;
    private String password;
    private long sensor_id;

    // Default constructor
    public SensorAuthDTO() {}

    // Constructor with parameters
    public SensorAuthDTO(String username, String nome, String email, String password, long sensor_id) {
        this.username = username;
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.sensor_id = sensor_id;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(int sensor_id) {
        this.sensor_id = sensor_id;
    }

    //convert an entity to a DTO
    public static SensorAuthDTO from(SensorAuth sensorAuth) {
        return new SensorAuthDTO(
                sensorAuth.getUsername(),
                sensorAuth.getNome(),
                sensorAuth.getEmail(),
                sensorAuth.getPassword(),
                sensorAuth.getSensor().getId()
        );
    }

    //convert a list of entities to a list of DTOs
    public static List<SensorAuthDTO> from(List<SensorAuth> sensorAuths) {
        return sensorAuths.stream().map(SensorAuthDTO::from).collect(Collectors.toList());
    }
}
