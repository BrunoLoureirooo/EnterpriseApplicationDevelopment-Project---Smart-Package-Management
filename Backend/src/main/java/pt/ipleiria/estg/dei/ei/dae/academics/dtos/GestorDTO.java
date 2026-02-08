package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import pt.ipleiria.estg.dei.ei.dae.academics.entities.Gestor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class GestorDTO implements Serializable {

    private String username;
    private String nome;
    private String email;
    private String password;

    // Default constructor
    public GestorDTO() {}

    // Constructor with parameters
    public GestorDTO(String username, String nome, String email, String password) {
        this.username = username;
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
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

    //convert an entity to a DTO
    public static GestorDTO from(Gestor gestor) {
        return new GestorDTO(gestor.getUsername(), gestor.getNome(), gestor.getEmail(), gestor.getPassword());
    }

    //convert a list of entities to a list of DTOs
    public static List<GestorDTO> from(List<Gestor> gestores) {
        return gestores.stream().map(GestorDTO::from).collect(Collectors.toList());
    }
}
