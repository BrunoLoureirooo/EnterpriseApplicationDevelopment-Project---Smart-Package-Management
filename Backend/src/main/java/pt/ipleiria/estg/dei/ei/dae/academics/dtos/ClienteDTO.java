package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import jakarta.persistence.Id;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Cliente;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Encomenda;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Medicao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteDTO implements Serializable {
    @Id
    private String username;
    private String nome;
    private String email;
    private String password;
    private List<Long> encomendasId;

    public ClienteDTO() {
        this.encomendasId = new ArrayList<>();
    }

    public ClienteDTO(String username, String nome, String email, String password, List<Long> encomendasId) {
        this.username = username;
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.encomendasId = encomendasId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Long> getEncomendasId() {
        return encomendasId;
    }

    public void setEncomendasId(List<Long> encomendasId) {
        this.encomendasId = encomendasId;
    }

    // Converts an entity Student to a DTO Student class
    public static ClienteDTO from(Cliente cliente) {
        List<Long> encomendasId = cliente.getEncomendas()
                .stream()
                .map(Encomenda::getCodigo) // Assuming Encomenda has a getCodigo() method
                .collect(Collectors.toList());  // Usar collect em vez de toList() para maior compatibilidade
        return new ClienteDTO(
                cliente.getUsername(),
                cliente.getNome(),  // Corrigir a ordem
                cliente.getEmail(), // Corrigir a ordem
                cliente.getPassword(), // Corrigir a ordem
                encomendasId
        );
    }
    // converts an entire list of entities into a list of DTOs
    public static List<ClienteDTO> from(List<Cliente> clientes) {
        return clientes.stream().map(ClienteDTO::from).collect(Collectors.toList());
    }


}

