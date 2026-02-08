package pt.ipleiria.estg.dei.ei.dae.academics.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllFuncionarios",
                query = "SELECT f FROM Funcionario f ORDER BY f.nome"
        ),
        @NamedQuery(
                name = "getFuncionarioByUsername",
                query = "SELECT f FROM Funcionario f WHERE f.username = :username"
        )
})
public class Funcionario extends Utilizador implements Serializable {
    public Funcionario() {
    }

    public Funcionario(String username, String password, String nome, String email) {
        super(username, password, nome, email);
    }
}
