package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Funcionario;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Hasher;

import java.util.List;

@Stateless
public class FuncionarioBean {
    // Injeção do EntityManager
    @PersistenceContext
    private EntityManager entityManager;

    private Hasher hasher = new Hasher();

    // Criar um novo Funcionario
    public Funcionario create(String username, String password, String nome, String email) throws MyValueIsIncorrectException, MyEntityExistsException, MyConstraintViolationException {
        validateInput(username, password, nome, email);

        // Verificar se já existe um Funcionario com o mesmo username
        if (find(username) != null) {
            throw new MyEntityExistsException("Já existe um funcionário com o username: " + username);
        }

        // Criar um novo Funcionario
        try{
            Funcionario funcionario = new Funcionario(username, hasher.hash(password), nome, email);
            entityManager.persist(funcionario);
            return funcionario;
        } catch (ConstraintViolationException e){
            throw new MyConstraintViolationException(e);
        }

    }

    // Atualizar um Funcionario
    public void update(String username, String nome, String email) throws MyValueIsIncorrectException, MyEntityExistsException, MyConstraintViolationException, MyEntityNotFoundException {
        validateInput(username, nome, email);

        Funcionario funcionario = find(username);
        if (funcionario != null) {
            try{
                funcionario.setNome(nome);
                funcionario.setEmail(email);
                entityManager.merge(funcionario);
            } catch (ConstraintViolationException e){
                throw new MyConstraintViolationException(e);
            }
        }else{
            throw new MyEntityNotFoundException("Funcionario não encontrado");
        }

    }

    // Remover um Funcionario
    public void delete(String username) throws MyValueIsIncorrectException {
        Funcionario funcionario = find(username);
        if (funcionario != null) {
            entityManager.remove(funcionario);
        }else{
            throw new MyValueIsIncorrectException("Funcionario não encontrado");
        }

    }

    // Validação de entrada
    private void validateInput(String username, String password, String nome, String email) throws MyValueIsIncorrectException {
        if (username == null || username.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Username não pode ser vazio");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Password não pode ser vazia");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Nome não pode ser vazio");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Email não pode ser vazio");
        }
    }

    private void validateInput(String username, String nome, String email) throws MyValueIsIncorrectException {
        if (username == null || username.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Username não pode ser vazio");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Nome não pode ser vazio");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new MyValueIsIncorrectException("Email não pode ser vazio");
        }
    }

    // Encontrar um Funcionario pelo username
    public Funcionario find(String username) {
        return entityManager.find(Funcionario.class, username);
    }

    // Remover um Funcionario pelo username
    public void remove(String username) throws MyValueIsIncorrectException {
        Funcionario funcionario = find(username);
        if (funcionario == null) {
            throw new MyValueIsIncorrectException("Funcionario não encontrado para o username: " + username);
        }
        entityManager.remove(funcionario);
    }

    // Encontrar todos os Funcionarios
    public List<Funcionario> findAll() {
        return entityManager.createNamedQuery("getAllFuncionarios", Funcionario.class).getResultList();
    }
}
