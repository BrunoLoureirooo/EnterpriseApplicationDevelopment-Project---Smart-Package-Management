package pt.ipleiria.estg.dei.ei.dae.academics.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoVolume;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "volumes", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
@NamedQueries({
        @NamedQuery(
                name = "getAllVolumes",
                query = "SELECT v FROM Volume v ORDER BY v.id"
        ),
        @NamedQuery(name = "getVolumeById", query = "SELECT v FROM Volume v WHERE v.id = :id")
})
public class Volume extends Versionable  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "volume", fetch = FetchType.EAGER)
    private List<VolumeProduto> volumeProdutos;  // Lista de VolumeProduto para a relação com Produto

    @ManyToOne
    private Encomenda encomenda;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "volume")
    private List<Sensor> sensores;

    private EstadoVolume estado;

    // Construtor padrão
    public Volume() {
        this.volumeProdutos = new ArrayList<>();
        this.sensores = new ArrayList<>();
    }

    // Construtor com encomenda e estado
    public Volume(EstadoVolume estado) {
        this.volumeProdutos = new ArrayList<>();
        this.sensores = new ArrayList<>();
        this.estado = estado;
    }

    // Getters e setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<VolumeProduto> getVolumeProdutos() {
        return volumeProdutos;
    }

    public void setVolumeProdutos(List<VolumeProduto> volumeProdutos) {
        this.volumeProdutos = volumeProdutos;
    }

    public Encomenda getEncomenda() {
        return encomenda;
    }

    public void setEncomenda(Encomenda encomenda) {
        this.encomenda = encomenda;
    }

    public List<Sensor> getSensores() {
        return sensores;
    }

    public void setSensores(List<Sensor> sensores) {
        this.sensores = sensores;
    }

    public EstadoVolume getEstado() {
        return estado;
    }

    public void setEstado(EstadoVolume estado) {
        this.estado = estado;
    }

    //remove sensor
    public void removeSensor(Sensor sensor) {
        this.sensores.remove(sensor);
        sensor.setVolume(null);
    }

    //add sensor
    public void addSensor(Sensor sensor) {
        this.sensores.add(sensor);
        sensor.setVolume(this);
    }

    public void addVolumeProduto(VolumeProduto volumeProduto) throws MyValueIsIncorrectException {
        if (volumeProduto == null) {
            throw new MyValueIsIncorrectException("VolumeProduto não pode ser nulo");
        }
        if (this.volumeProdutos.contains(volumeProduto)) {
            return;
        }
        this.volumeProdutos.add(volumeProduto);
    }

    public void removeVolumeProduto(VolumeProduto volumeProduto) throws MyValueIsIncorrectException {
        if (volumeProduto == null) {
            throw new MyValueIsIncorrectException("VolumeProduto não pode ser nulo");
        }
        if (!this.volumeProdutos.contains(volumeProduto)) {
            return;
        }
        this.volumeProdutos.remove(volumeProduto);
    }
}
