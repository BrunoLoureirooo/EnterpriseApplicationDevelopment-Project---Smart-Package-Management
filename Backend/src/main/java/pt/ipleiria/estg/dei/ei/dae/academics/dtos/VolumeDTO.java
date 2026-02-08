package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import pt.ipleiria.estg.dei.ei.dae.academics.entities.*;
import pt.ipleiria.estg.dei.ei.dae.academics.enums.EstadoVolume;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VolumeDTO {

    private long id;
    private EstadoVolume estado;
    private List<Long> produtosIds;
    private List<Integer> produtosQuants;
    private List<Long> sensoresIds;

    public VolumeDTO() {
        sensoresIds = new ArrayList<>();
    }
    // Construtor
    public VolumeDTO(long id, EstadoVolume estado, List<Long> sensoresIds, List<Long> produtosIds, List<Integer> produtosQuants) {
        this.id = id;
        this.estado = estado;
        this.sensoresIds = sensoresIds;
        this.produtosIds = produtosIds;
        this.produtosQuants = produtosQuants;
    }

    // Getters e Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EstadoVolume getEstado() {
        return estado;
    }

    public void setEstado(EstadoVolume estado) {
        this.estado = estado;
    }


    public List<Long> getSensoresIds() {
        return sensoresIds;
    }

    public void setSensoresIds(List<Long> sensoresIds) {
        this.sensoresIds = sensoresIds;
    }


    public List<Long> getProdutosIds() {
        return produtosIds;
    }

    public void setProdutosIds(List<Long> produtosIds) {
        this.produtosIds = produtosIds;
    }

    public List<Integer> getProdutosQuants() {
        return produtosQuants;
    }

    public void setProdutosQuants(List<Integer> produtosQuants) {
        this.produtosQuants = produtosQuants;
    }

    public static VolumeDTO from(Volume volume) {
        List<Long> produtosIds = volume.getVolumeProdutos().stream()
                .map(volumeProduto -> volumeProduto.getProduto().getId())
                .toList();
        List<Integer> produtosQuants = volume.getVolumeProdutos().stream()
                .map(volumeProduto -> volumeProduto.getQuantidade())
                .toList();
        return new VolumeDTO(
                volume.getId(),
                volume.getEstado(),
                volume.getSensores().stream().map(Sensor::getId).collect(Collectors.toList()), // Use Sensor IDs
                produtosIds,
                produtosQuants
        );
    }
    // converts an entire list of entities into a list of DTOs
    public static List<VolumeDTO> from(List<Volume> volumes) {
        return volumes.stream().map(VolumeDTO::from).collect(Collectors.toList());
    }

}
