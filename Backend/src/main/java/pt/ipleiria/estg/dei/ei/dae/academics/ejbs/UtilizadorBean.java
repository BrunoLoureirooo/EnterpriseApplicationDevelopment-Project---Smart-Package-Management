package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Utilizador;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Hasher;

@Stateless
public class UtilizadorBean {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private Hasher hasher;

    public Utilizador findOrFail(String username){
        var utilizador = entityManager.find(Utilizador.class, username);
        Hibernate.initialize(utilizador);
        return utilizador;
    }

    public boolean canLogin(String username, String password){
        var utilizador = findOrFail(username);
        return utilizador != null && utilizador.getPassword().equals(hasher.hash(password));
    }

    public boolean isPasswordCorrect(String username, String oldPassword) {
        var utilizador = findOrFail(username);
        try{
            var res = utilizador != null && utilizador.getPassword().equals(hasher.hash(oldPassword));
            return res;
        } catch (Exception e){
            throw new RuntimeException("Error checking password");
        }
    }

    public void updatePassword(String username, String newPassword) {
        var utilizador = findOrFail(username);
        try {
            utilizador.setPassword(hasher.hash(newPassword));
        } catch (Exception e){
            throw new RuntimeException("Error updating password");
        }
    }
}
