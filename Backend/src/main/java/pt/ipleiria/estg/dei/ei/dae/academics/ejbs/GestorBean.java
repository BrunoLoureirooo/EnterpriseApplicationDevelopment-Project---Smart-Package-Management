package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Gestor;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Hasher;

import java.util.List;

@Stateless
public class GestorBean {
    @PersistenceContext
    private EntityManager entityManager;

    private Hasher hasher = new Hasher();

    //create a new Gestor
    public Gestor create(String username, String password, String nome, String email) throws MyValueIsIncorrectException, MyEntityExistsException, MyConstraintViolationException {
        validateInput(username, password, nome, email);

        //if there exists a Gestor with the same username, throw an exception
        if (find(username) != null) {
            throw new MyEntityExistsException("Já existe um gestor com o username: " + username);
        }

        try{
            Gestor gestor = new Gestor(username, hasher.hash(password), nome, email);
            entityManager.persist(gestor);
            return gestor;
        } catch (ConstraintViolationException e){
            throw new MyConstraintViolationException(e);
        }
    }

    //update a Gestor
    public void update(String username, String nome, String email) throws MyValueIsIncorrectException, MyEntityNotFoundException, MyConstraintViolationException {
        validateInput(username, "password", nome, email);

        Gestor gestor = find(username);
        if (gestor != null) {
            try{
                gestor.setNome(nome);
                gestor.setEmail(email);
                entityManager.merge(gestor);
            } catch (ConstraintViolationException e){
                throw new MyConstraintViolationException(e);
            }
        }else{
            throw new MyEntityNotFoundException("Gestor não encontrado");
        }

    }

    //delete a Gestor
    public void delete(String username) throws MyEntityNotFoundException {
        Gestor gestor = find(username);
        if (gestor != null) {
            entityManager.remove(gestor);
        }else{
            throw new MyEntityNotFoundException("Gestor não encontrado");
        }
    }

    //validate the input
    private void validateInput(String username, String password, String nome, String email) throws MyValueIsIncorrectException {
        if (username == null || username.trim().length() == 0) {
            throw new MyValueIsIncorrectException("Username não pode ser vazio");
        }
        if (password == null || password.trim().length() == 0) {
            throw new MyValueIsIncorrectException("Password não pode ser vazia");
        }
        if (nome == null || nome.trim().length() == 0) {
            throw new MyValueIsIncorrectException("Nome não pode ser vazio");
        }
        if (email == null || email.trim().length() == 0) {
            throw new MyValueIsIncorrectException("Email não pode ser vazio");
        }
    }

    //find a Gestor by its username
    public Gestor find(String username) {
        return entityManager.find(Gestor.class, username);
    }

    //remove a Gestor by its username
    public void remove(String username) {
        Gestor gestor = find(username);
        if (gestor != null) {
            entityManager.remove(gestor);
        }
    }

    //find all Gestores
    public List<Gestor> findAll() {
        return entityManager.createNamedQuery("getAllGestores", Gestor.class).getResultList();
    }
}
