package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.SensorBean;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Medicao;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Sensor;

import java.util.List;
import java.util.stream.Collectors;

public class MedicaoDTO {
    private String id;
    private long data;
    private float valor;
    private long sensor_id;

    public MedicaoDTO() {
    }

    public MedicaoDTO(String id, long data, float valor, long sensor_id) {
        this.id = id;
        this.data = data;
        this.valor = valor;
        this.sensor_id = sensor_id;
    }

    public String getId() {
        return id;
    }

    public long getData() {
        return data;
    }

    public float getValor() {
        return valor;
    }

    public long getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(long sensor_id) {
        this.sensor_id = sensor_id;
    }

    public static MedicaoDTO from(Medicao medicao) {
        return new MedicaoDTO(medicao.getId(), medicao.getData_hora(), medicao.getValor(), medicao.getSensor().getId());
    }

    public static List<MedicaoDTO> from(List<Medicao> medicoes) {
        return medicoes.stream().map(MedicaoDTO::from).collect(Collectors.toList());
    }
}
