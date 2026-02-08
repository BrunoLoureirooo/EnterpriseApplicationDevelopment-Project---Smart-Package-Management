package pt.ipleiria.estg.dei.ei.dae.academics.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoEncomenda;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity // Define que esta classe é uma entidade JPA
@Table(name = "encomendas",
        uniqueConstraints = { @UniqueConstraint(columnNames = "codigo") })
@NamedQueries({
        @NamedQuery(
                name = "getAllEncomendas",
                query = "SELECT e FROM Encomenda e ORDER BY e.codigo" // JPQL
        ),
        @NamedQuery(name = "getEncomendaByEstado", query = "SELECT e FROM Encomenda e WHERE e.estado = :estado"),
        @NamedQuery(
                name = "getEncomendaByCliente",
                query = "SELECT e FROM Encomenda e WHERE e.cliente.username = :username"
        )
})
public class Encomenda extends Versionable  implements Serializable {

    @Id // Define a chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long codigo;

    @NotBlank
    private String estado;

    @OneToMany(mappedBy = "encomenda", fetch = FetchType.EAGER)
    private List<Volume> volumes;


    @NotBlank
    private String morada;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cliente_username") // Define a chave estrangeira
    private Cliente cliente;

    // Construtor vazio (necessário pelo JPA)
    public Encomenda() {
        volumes = new ArrayList<>();
    }

    // Construtor com argumentos
    public Encomenda(String estado, Cliente cliente, String morada) throws MyValueIsIncorrectException {
        //check if estado is valid
        EstadoEncomenda estadoEncomenda;
        try{
            estadoEncomenda = EstadoEncomenda.valueOf(estado.toUpperCase());
        }catch(IllegalArgumentException e){
            throw new MyValueIsIncorrectException("Invalid estado value: "
            + estado + ". Allowed ones are: " + Arrays.toString(EstadoEncomenda.values()));
        }
        this.estado = String.valueOf(estadoEncomenda);
        this.cliente = cliente;
        this.morada = morada;
        volumes = new ArrayList<>();
    }

    // Getters e Setters
    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) throws MyValueIsIncorrectException {
        //check if estado is valid
        EstadoEncomenda estadoEncomenda;
        try{
            estadoEncomenda = EstadoEncomenda.valueOf(estado.toUpperCase());
        }catch(IllegalArgumentException e){
            throw new MyValueIsIncorrectException("Invalid estado value: "
                    + estado + ". Allowed ones are: " + Arrays.toString(EstadoEncomenda.values()));
        }
        this.estado = String.valueOf(estadoEncomenda);
    }

    public List<Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Volume> volumes) {
        this.volumes.clear();
        for (Volume volume : volumes) {
            addVolume(volume); // Sincroniza o relacionamento ao redefinir a lista
        }
    }

    public void addVolume(Volume volume) {
        if (!this.volumes.contains(volume)) {
            this.volumes.add(volume);
            volume.setEncomenda(this); // Sincroniza o lado oposto
        }
    }

    public void removeVolume(Volume volume) {
        if (this.volumes.contains(volume)) {
            this.volumes.remove(volume);
            volume.setEncomenda(null); // Remove a referência do outro lado
        }
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

}
