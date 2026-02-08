package pt.ipleiria.estg.dei.ei.dae.academics.entities;

import jakarta.persistence.*;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoProduto;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "volumes_produtos", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class VolumeProduto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "volume_id")
    private Volume volume;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private int quantidade;

    @Enumerated(EnumType.STRING)
    private EstadoProduto estado;

    // Construtor padrão
    public VolumeProduto() {}

    // Construtor com parâmetros
    public VolumeProduto(Volume volume, Produto produto, int quantidade, EstadoProduto estado) {
        this.volume = volume;
        this.produto = produto;
        this.quantidade = quantidade;
        this.estado = estado;  // Inicializando o estado
    }

    // Getters e setters
    public long getId() {
        return id;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) throws MyValueIsIncorrectException {
        if (volume == null) {
            throw new MyValueIsIncorrectException("Volume não pode ser nulo");
        }
        this.volume = volume;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) throws MyValueIsIncorrectException {
        if (produto == null) {
            throw new MyValueIsIncorrectException("Produto não pode ser nulo");
        }
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) throws MyValueIsIncorrectException {
        if (quantidade < 1) {
            throw new MyValueIsIncorrectException("Quantidade com valor impossível: "+quantidade);
        }
        this.quantidade = quantidade;
    }

    public EstadoProduto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProduto estado) throws MyValueIsIncorrectException {
        if (estado == null) {
            throw new MyValueIsIncorrectException("Estado do Volume_Produtos não pode ser nulo");
        }
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VolumeProduto that = (VolumeProduto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
