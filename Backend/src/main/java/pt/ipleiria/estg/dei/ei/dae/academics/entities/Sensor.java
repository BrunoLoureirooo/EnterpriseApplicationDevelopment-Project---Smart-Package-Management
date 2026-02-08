package pt.ipleiria.estg.dei.ei.dae.academics.entities;

import jakarta.persistence.*;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.TipoSensor;

import java.util.ArrayList;
import java.util.List;

@Entity // Anotação do Java Persistence API que indica que a classe é para persistir (ser mapeada) na base de dados
@Table(name = "sensores",
        uniqueConstraints = { @UniqueConstraint(columnNames = "id") })
@NamedQueries({
        @NamedQuery(
                name = "getAllSensores",
                query = "SELECT s FROM Sensor s ORDER BY s.id" // JPQL
        )
})
public class Sensor extends Versionable {
    @Id // Anotação que indica que este atributo será chave primária na base de dados
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Anotação que indica que o valor deste atributo será gerado automaticamente
    private long id;
    private boolean ativo;
    private TipoSensor tipo;

    @OneToOne(mappedBy = "sensor", fetch = FetchType.EAGER)
    private SensorAuth sensorAuth;

    @ManyToOne // Many sensors can be related to one volume
    @JoinColumn(name = "volume_id") // This creates the foreign key column in the sensores table
    private Volume volume;

    @OneToMany(mappedBy = "sensor", fetch = FetchType.EAGER)
    private List<Medicao> historico;

    //REQUIRED: O construtor vazio requisito é obrigatório em classes que são entidades JPA (Java Persistence API)
    public Sensor() {
        historico = new ArrayList<>();
    }

    public Sensor(boolean ativo, TipoSensor tipo, Volume volume, SensorAuth sensorAuth) {
        this.ativo = ativo;
        this.tipo = tipo;
        this.volume = volume;
        this.historico = new ArrayList<>();
        this.sensorAuth = sensorAuth;
    }

    //REQUIRED: Os getters e setters são requisitos obrigatórios em classes que são entidades JPA (Java Persistence API)
    public SensorAuth getSensorAuth() {
        return sensorAuth;
    }

    public void setSensorAuth(SensorAuth sensorAuth) {
        this.sensorAuth = sensorAuth;
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

    public TipoSensor getTipo() {
        return tipo;
    }

    public void setTipo(TipoSensor tipo) {
        this.tipo = tipo;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public List<Medicao> getHistorico() {
        return historico;
    }

    public void setHistorico(List<Medicao> historico) {
        this.historico = historico;
    }

    public void addHistorico(Medicao medicao) {
        historico.add(medicao);
    }

    public void removeHistorico(Medicao medicao) {
        historico.remove(medicao);
    }
}
