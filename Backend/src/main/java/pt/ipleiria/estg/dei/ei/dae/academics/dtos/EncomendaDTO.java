package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class EncomendaDTO implements Serializable {
    @Id
    private long codigo;
    private String estado;
    private List<Long> volumeIds;
    private String morada;
    private String clienteUsername;

    public EncomendaDTO() {
        volumeIds = new ArrayList<>();
    }

    public EncomendaDTO(long codigo, String estado, List<Long> volumeIds, String morada, String clienteUsername) {
        this.codigo = codigo;
        this.estado = estado;
        this.volumeIds = volumeIds;
        this.morada = morada;
        this.clienteUsername = clienteUsername;

    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Long> getVolumeIds() {
        return volumeIds;
    }

    public void setVolumeIds(List<Long> volumeIds) {
        this.volumeIds = volumeIds;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getClienteUsername() {
        return clienteUsername;
    }

    public void setClienteUsername(String clienteUsername) {
        this.clienteUsername = clienteUsername;
    }

    // Converts an entity Student to a DTO Student class
    public static EncomendaDTO from(Encomenda encomenda) {
        List<Long> volumeIds = encomenda.getVolumes()
                .stream()
                .map(Volume::getId)
                .toList();
        return new EncomendaDTO(
                encomenda.getCodigo(),
                encomenda.getEstado(),
                volumeIds,
                encomenda.getMorada(),
                encomenda.getCliente().getUsername() // Corrigido
        );
    }

    // converts an entire list of entities into a list of DTOs
    public static List<EncomendaDTO> from(List<Encomenda> encomendas) {
        return encomendas.stream().map(EncomendaDTO::from).collect(Collectors.toList());
    }


}


