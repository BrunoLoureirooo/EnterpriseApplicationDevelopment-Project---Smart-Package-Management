package pt.ipleiria.estg.dei.ei.dae.academics.entities;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllGestores",
                query = "SELECT g FROM Gestor g ORDER BY g.nome"
        ),
        @NamedQuery(
                name = "getGestorByUsername",
                query = "SELECT g FROM Gestor g WHERE g.username = :username"
        )
})
public class Gestor extends Utilizador implements Serializable {

    // Default constructor
    public Gestor() {
    }

    // Constructor with parameters
    public Gestor(String username, String password, String nome, String email) {
        super(username, password, nome, email);
    }
}
