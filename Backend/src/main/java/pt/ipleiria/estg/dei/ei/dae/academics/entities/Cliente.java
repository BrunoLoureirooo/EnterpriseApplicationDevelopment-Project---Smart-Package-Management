package pt.ipleiria.estg.dei.ei.dae.academics.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "getAllClientes", query = "SELECT c FROM Cliente c ORDER BY c.username"),
        @NamedQuery(name = "getClienteByUsername", query = "SELECT c FROM Cliente c WHERE c.username = :username")
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Cliente extends Utilizador implements Serializable {

    @OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Encomenda> encomendas = new ArrayList<>();

    // Default constructor
    public Cliente() {}

    // Constructor with parameters
    public Cliente(String username, String password, String nome, String email) {
        super(username, password, nome, email); // Call superclass constructor
    }

    // Getters and Setters for encomendas
    public List<Encomenda> getEncomendas() {
        return encomendas;
    }

    public void setEncomendas(List<Encomenda> encomendas) {
        this.encomendas.clear();
        if (encomendas != null) {
            this.encomendas.addAll(encomendas);
            for (Encomenda encomenda : encomendas) {
                encomenda.setCliente(this); // Sincroniza a relação bidirecional
            }
        }
    }

    public void addEncomenda(Encomenda encomenda) {
        if (encomenda != null && !this.encomendas.contains(encomenda)) {
            this.encomendas.add(encomenda);
            encomenda.setCliente(this);
        }
    }

    public void removeEncomenda(Encomenda encomenda) {
        if (this.encomendas.contains(encomenda)) {
            this.encomendas.remove(encomenda);
            encomenda.setCliente(null);
        }
    }
}
