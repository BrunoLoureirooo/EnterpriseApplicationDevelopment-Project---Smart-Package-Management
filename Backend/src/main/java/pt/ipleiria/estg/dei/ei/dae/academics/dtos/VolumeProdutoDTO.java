package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import pt.ipleiria.estg.dei.ei.dae.academics.entities.VolumeProduto;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoProduto;

import java.util.List;
import java.util.stream.Collectors;

public class VolumeProdutoDTO {
    private long id;
    private long volumeId;
    private long produtoId;
    private int quantidade;
    private EstadoProduto estado;

    // Construtor padrão
    public VolumeProdutoDTO() {}

    // Construtor com parâmetros
    public VolumeProdutoDTO(long id, long volumeId, long produtoId, int quantidade, EstadoProduto estado) {
        this.id = id;
        this.volumeId = volumeId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.estado = estado;
    }

    // Getters e setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(long volumeId) {
        this.volumeId = volumeId;
    }

    public long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(long produtoId) {
        this.produtoId = produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public EstadoProduto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProduto estado) {
        this.estado = estado;
    }

    //from
    public static VolumeProdutoDTO from(VolumeProduto volumeProduto) {
        return new VolumeProdutoDTO(volumeProduto.getId(), volumeProduto.getVolume().getId(), volumeProduto.getProduto().getId(), volumeProduto.getQuantidade(), volumeProduto.getEstado());
    }

    //from(list)
    public static List<VolumeProdutoDTO> from(List<VolumeProduto> volumeProdutos) {
        return volumeProdutos.stream().map(VolumeProdutoDTO::from).collect(Collectors.toList());
    }
}
