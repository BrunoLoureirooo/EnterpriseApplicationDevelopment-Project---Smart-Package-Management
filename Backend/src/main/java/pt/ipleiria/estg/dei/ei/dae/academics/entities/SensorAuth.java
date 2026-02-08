package pt.ipleiria.estg.dei.ei.dae.academics.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllSensoresAuth",
                query = "SELECT f FROM SensorAuth f ORDER BY f.nome"
        ),
        @NamedQuery(
                name = "getSensorAuthByUsername",
                query = "SELECT f FROM SensorAuth f WHERE f.username = :username"
        )
})
public class SensorAuth extends Utilizador implements Serializable {
    @OneToOne
    @JoinColumn(name = "sensor_id")
    @NotNull
    private Sensor sensor;

    public SensorAuth() {
    }

    public SensorAuth(String username, String password, String nome, String email, Sensor sensor) {
        super(username, password, nome, email);
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
