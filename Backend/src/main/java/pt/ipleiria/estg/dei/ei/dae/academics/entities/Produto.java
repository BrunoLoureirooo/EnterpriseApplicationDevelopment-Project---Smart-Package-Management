package pt.ipleiria.estg.dei.ei.dae.academics.entities;

import jakarta.persistence.*;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.TipoSensor;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produtos",
        uniqueConstraints = { @UniqueConstraint(columnNames = "nome") })
@NamedQueries({
        @NamedQuery(
                name = "getAllProdutos",
                query = "SELECT p FROM Produto p ORDER BY p.nome ASC" // JPQL
        ),
        @NamedQuery(name = "getProtutoByNome", query = "SELECT p FROM Produto p WHERE p.nome = :nome"),
        @NamedQuery(name = "getProdutoByCategoria", query = "SELECT p FROM Produto p WHERE p.categoria = :categoria"),
})
public class Produto extends Versionable  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String nome;
    private String descricao;
    private String categoria;
    @OneToMany(mappedBy = "produto")
    private List<VolumeProduto> volumeProdutos = new ArrayList<>();
    private float treshold;
    private TipoSensor treshold_tipo;

    public Produto() {
        this.volumeProdutos = new ArrayList<>();
    }

    public Produto(
            String nome,
            String descricao,
            String categoria,
            float treshold,
            TipoSensor treshold_tipo
    ) {
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.volumeProdutos = new ArrayList<>();
        this.treshold = treshold;
        this.treshold_tipo = treshold_tipo;
    }

    public float getTreshold() {
        return treshold;
    }

    public void setTreshold(float treshold) {
        this.treshold = treshold;
    }

    public TipoSensor getTresholdTipo() {
        return treshold_tipo;
    }

    public void setTresholdTipo(TipoSensor treshold_tipo) {
        this.treshold_tipo = treshold_tipo;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<VolumeProduto> getVolumeProdutos() {
        return volumeProdutos;
    }

    public void setVolumeProdutos(List<VolumeProduto> volumeProdutos) {
        this.volumeProdutos = volumeProdutos;
    }

    public void addVolumeProduto(VolumeProduto volumeProduto) throws MyValueIsIncorrectException {
        if (this.volumeProdutos.contains(volumeProduto)) {
            return;
        }
        if (volumeProduto == null) {
            throw new MyValueIsIncorrectException("VolumeProduto não pode ser nulo");
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
