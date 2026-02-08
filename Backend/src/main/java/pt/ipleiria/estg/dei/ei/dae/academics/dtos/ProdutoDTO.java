package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import jakarta.persistence.Id;
import  pt.ipleiria.estg.dei.ei.dae.academics.entities.Produto;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


public class ProdutoDTO implements Serializable {
    @Id
    private long id;
    private String nome;
    private String descricao;
    private String categoria;
    private float treshold;
    private String treshold_tipo;

    public ProdutoDTO() {

    }

    public ProdutoDTO(long id, String nome, String descricao, String categoria, float treshold, String treshold_tipo) {
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.id = id;
        this.treshold = treshold;
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

    public float getTreshold() {
        return treshold;
    }

    public void setTreshold(float treshold) {
        this.treshold = treshold;
    }

    public String getTresholdTipo() {
        return treshold_tipo;
    }

    public void setTresholdTipo(String treshold_tipo) {
        this.treshold_tipo = treshold_tipo;
    }

    // Converts an entity Student to a DTO Student class
    public static ProdutoDTO from(Produto produto) {
        return new ProdutoDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getCategoria(),
                produto.getTreshold(),
                produto.getTresholdTipo().toString()
        );
    }
    // converts an entire list of entities into a list of DTOs
    public static List<ProdutoDTO> from(List<Produto> produtos) {
        return produtos.stream().map(ProdutoDTO::from).collect(Collectors.toList());
    }


}


