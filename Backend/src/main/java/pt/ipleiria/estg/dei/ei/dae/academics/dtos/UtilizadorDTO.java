package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Utilizador;

import java.util.List;
import java.util.stream.Collectors;

public class UtilizadorDTO {
    private String username;
    private String nome;
    private String email;
    private String role;

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

    public String getRole() {
        return role;
    }

    public UtilizadorDTO() {

    }

    public UtilizadorDTO(String username, String name, String email, String role) {
        this.username = username;
        this.nome = name;
        this.email = email;
        this.role = role;
    }
    public static UtilizadorDTO from(Utilizador user) {
        return new UtilizadorDTO(
                user.getUsername(),
                user.getNome(),
                user.getEmail(),
                Hibernate.getClass(user).getSimpleName()
        );
    }
    public static List<UtilizadorDTO> from(List<Utilizador> users) {
        return users.stream().map(UtilizadorDTO::from).collect(Collectors.toList());
    }
}
