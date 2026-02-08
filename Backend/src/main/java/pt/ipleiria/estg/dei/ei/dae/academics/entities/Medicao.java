package pt.ipleiria.estg.dei.ei.dae.academics.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "medicoes",
        uniqueConstraints = { @UniqueConstraint(columnNames = "id") })
@NamedQueries({
        @NamedQuery(
                name = "getAllMedicoes",
                query = "SELECT m FROM Medicao m ORDER BY m.data_hora DESC" // JPQL
        ),
        @NamedQuery(
                name = "getMedicaoBySensor",
                query = "SELECT m FROM Medicao m WHERE m.sensor.id = :sensorId ORDER BY m.data_hora DESC"
        ),
        @NamedQuery(
                name="deleteMedicaoBySensor",
                query="DELETE FROM Medicao m WHERE m.sensor.id = :sensorId"
        )
})
public class Medicao extends Versionable  {
    @ManyToOne
    private Sensor sensor;
    private float valor;
    @Id
    private String id;
    private long data_hora;

    public Medicao() {
    }

    public Medicao(Sensor sensor, float valor, long data_hora) {
        this.sensor = sensor;
        this.valor = valor;
        this.data_hora = data_hora;
        this.id = data_hora + "-" + sensor.getId();
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public long getData_hora() {
        return data_hora;
    }

    public void setData_hora(long data_hora) {
        this.data_hora = data_hora;
    }

    public String getId() {
        return id;
    }
}
