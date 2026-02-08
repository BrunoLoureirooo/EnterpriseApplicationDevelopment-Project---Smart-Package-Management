package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Cliente;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Encomenda;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Utilizador;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyArgumentsNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Hasher;

import java.util.List;

@Stateless
public class ClienteBean {

    @PersistenceContext
    private EntityManager entityManager;

    private Hasher hasher = new Hasher();

    // Create method (no change needed)
    public void create(String username, String password, String nome, String email) throws MyEntityExistsException, MyArgumentsNotFoundException, MyConstraintViolationException {
        validateInput(username, password, nome, email);

        // Now using Utilizador for the find query
        Utilizador utilizador = entityManager.createNamedQuery("getClienteByUsername", Utilizador.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (utilizador != null) {
            throw new MyEntityExistsException("Usuário com username: " + username + ", já existe");
        }

        try{
            utilizador = new Cliente(username, hasher.hash(password), nome, email);
            entityManager.persist(utilizador);
        }catch (ConstraintViolationException e){
            throw new MyConstraintViolationException(e);
        }
    }

    // Update method (no change needed)
    public void update(String username, String nome, String email) throws MyEntityNotFoundException, MyArgumentsNotFoundException, MyConstraintViolationException {
        // Ajustado: remove a validação da senha, pois não queremos atualizá-la aqui
        validateInput(username, nome, email);

        // Encontrar o cliente
        Cliente utilizador = (Cliente) find(username);  // Safely cast to Cliente

        // Bloqueio otimista para evitar conflitos de concorrência
        entityManager.lock(utilizador, LockModeType.OPTIMISTIC);

        try {
            // Atualizando apenas o nome e o email
            utilizador.setNome(nome);
            utilizador.setEmail(email);

            // Persistir as alterações
            entityManager.merge(utilizador);
        } catch (ConstraintViolationException e) {
            // Caso ocorra algum erro de violação de restrição, lançar exceção personalizada
            throw new MyConstraintViolationException(e);
        }
    }


    // Delete method (no change needed)
    public void delete(String username) throws MyEntityNotFoundException {
        Cliente utilizador = (Cliente) find(username);  // Safely cast to Cliente
        entityManager.remove(utilizador);
    }

    // Find method (refactored for generalization)
    public Cliente find(String username) throws MyEntityNotFoundException {
        Cliente utilizador = entityManager.find(Cliente.class, username);  // Using the superclass here
        if (utilizador == null) {
            throw new MyEntityNotFoundException("Cliente com username: " + username + ", não existe");
        }
        return utilizador;
    }

    // Find all clients (still working with Cliente, no change needed)
    public List<Cliente> findAll() {
        return entityManager.createNamedQuery("getAllClientes", Cliente.class).getResultList();
    }

    // Get Encomendas (refactored for generalization)
    public List<Encomenda> getEncomendas(String username) throws MyEntityNotFoundException {
        Cliente utilizador = find(username);
        // You can safely cast to Cliente here because it extends Utilizador
        return (utilizador).getEncomendas();
    }

    // Add Encomenda method (still works as is)
    public void addEncomenda(String username, long encomendaId) throws MyEntityNotFoundException {
        Cliente cliente = (Cliente) find(username);  // Safely cast back to Cliente
        Encomenda encomenda = entityManager.find(Encomenda.class, encomendaId);

        if (encomenda == null) {
            throw new MyEntityNotFoundException("Encomenda com id:" + encomendaId + ", não existe");
        }

        cliente.addEncomenda(encomenda);
        entityManager.merge(cliente);
    }

    // Remove Encomenda method (same as add)
    public void removeEncomenda(String username, long encomendaId) throws MyEntityNotFoundException {
        Cliente cliente = (Cliente) find(username);  // Cast to Cliente
        Encomenda encomenda = entityManager.find(Encomenda.class, encomendaId);

        if (encomenda == null) {
            throw new MyEntityNotFoundException("Encomenda com id:" + encomendaId + ", não existe");
        }

        cliente.removeEncomenda(encomenda);
        entityManager.merge(cliente);
    }

    // Validate input method (no change needed)
    private void validateInput(String username, String password, String nome, String email) throws MyArgumentsNotFoundException {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()
                || nome == null || nome.isEmpty() || email == null || email.isEmpty()) {
            throw new MyArgumentsNotFoundException("Todos os campos são obrigatórios");
        }
        // Aqui você pode adicionar validações adicionais de email ou outros campos, conforme necessário.
    }

    private void validateInput(String username, String nome, String email) throws MyArgumentsNotFoundException {
        if (username == null || username.isEmpty() || nome == null || nome.isEmpty() || email == null || email.isEmpty()) {
            throw new MyArgumentsNotFoundException("Todos os campos são obrigatórios");
        }
        // Aqui você pode adicionar validações adicionais de email ou outros campos, conforme necessário.
    }
}
