package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import  pt.ipleiria.estg.dei.ei.dae.academics.entities.Sensor;

import  pt.ipleiria.estg.dei.ei.dae.academics.entities.Medicao;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.TipoSensor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SensorDTO implements Serializable {
    @Id
    private long id;
    private boolean ativo;
    private String tipo;
    private long volumeId;
    private List<String> medicaoIds;
    private String sensorAuthUsername;

    public SensorDTO() {
        medicaoIds = new ArrayList<>();
    }

    public SensorDTO(long id, boolean ativo, String tipo, long volumeId, List<String> medicaoIds, String sensorAuthUsername) {
        this.id = id;
        this.ativo = ativo;
        this.tipo = tipo;
        this.volumeId = volumeId;
        this.medicaoIds = medicaoIds;
        this.sensorAuthUsername = sensorAuthUsername;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(long volumeId) {
        this.volumeId = volumeId;
    }

    public List<String> getMedicaoIds() {
        return medicaoIds;
    }

    public void setMedicaoIds(List<String> medicaoIds) {
        this.medicaoIds = medicaoIds;
    }

    public String getSensorAuthUsername() {
        return sensorAuthUsername;
    }

    public void setSensorAuthUsername(String sensorAuthUsername) {
        this.sensorAuthUsername = sensorAuthUsername;
    }

    // Converts an entity Student to a DTO Student class
    public static SensorDTO from(Sensor sensor) {
        String username = (sensor.getSensorAuth() != null) ? sensor.getSensorAuth().getUsername() : null;
        List<String> medicaoIds = sensor.getHistorico()
                .stream()
                .map(Medicao::getId) // Assuming Medicao has a getId() method
                .map(Object::toString) // Convert to string if needed
                .toList();
        return new SensorDTO(
                sensor.getId(),
                sensor.isAtivo(),
                String.valueOf(sensor.getTipo()),
                sensor.getVolume().getId(),
                medicaoIds,
                username
        );
    }
    // converts an entire list of entities into a list of DTOs
    public static List<SensorDTO> from(List<Sensor> sensores) {
        return sensores.stream().map(SensorDTO::from).collect(Collectors.toList());
    }

}


