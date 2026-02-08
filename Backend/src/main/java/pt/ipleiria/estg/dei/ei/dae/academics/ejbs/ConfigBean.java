package pt.ipleiria.estg.dei.ei.dae.academics.ejbs;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Encomenda;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Medicao;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Volume;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Startup  // Classe será instanciada automaticamente no momento da inicialização da aplicação
@Singleton // Apenas existirá uma instância desta classe em toda a aplicação
public class ConfigBean {

    Logger logger = Logger.getLogger(ConfigBean.class.getName());

    @PersistenceContext
    private EntityManager entityManager;
    @EJB
    private SensorBean sensorBean;
    @EJB
    private ClienteBean clienteBean;
    @EJB
    private MedicaoBean medicaoBean;
    @EJB
    private VolumeBean volumeBean;
    @EJB
    private EncomendaBean encomendaBean;
    @EJB
    private ProdutoBean produtoBean;
    @EJB
    private FuncionarioBean funcionarioBean;
    @EJB
    private GestorBean gestorBean;
    @EJB
    private SensorAuthBean sensorAuthBean;



    @PostConstruct
    public void populateDB() {
        try {
            //criar produtos
            produtoBean.create("Televisao 55'", "DESCRICAO", "ELETRODOMESTICOS",45f,"TEMPERATURA");
            produtoBean.create("Televisao 70'", "DESCRICAO", "ELETRODOMESTICOS",45f,"TEMPERATURA");
            produtoBean.create("Computador escritorio", "DESCRICAO", "Computadores",45f,"TEMPERATURA");
            produtoBean.create("Computador gaming", "DESCRICAO", "Computadores",45f,"TEMPERATURA");
            produtoBean.create("Iphone 13", "DESCRICAO", "ELETRODOMESTICOS",45f,"TEMPERATURA");
            produtoBean.create("Samsung Galaxy S24", "DESCRICAO", "ELETRODOMESTICOS",45f,"TEMPERATURA");

            //criar dois clientes
            clienteBean.create("QuimDelas", "123", "Joaquim", "quimdelas6969@mail.pt");
            clienteBean.create("Inacio69", "123", "Inacio", "inacio6969@email.pt");

            //criar dois funcionarios
            funcionarioBean.create("Func1", "123", "Funcionario1", "func1@mail.pt");
            funcionarioBean.create("Func2", "123", "Funcionario2", "func2@mail.pt");

            //criar um gestor
            gestorBean.create("Gestor1", "123", "Gestor1", "georginobambino@mail.pt");



            //criar um array no estilo
            // [[Produto1,1], [Produto2,2], [Produto3,2]]

            List<Object[]> produtos = new ArrayList<>();
            produtos.add(new Object[]{1L, 2});
            produtos.add(new Object[]{2L, 1});
            produtos.add(new Object[]{3L, 2});
            produtos.add(new Object[]{4L, 1});
            produtos.add(new Object[]{6L, 1});

            List<Volume> volumes = volumeBean.create(produtos);

            Encomenda encomenda  = encomendaBean.create(volumes, "Morada", "QuimDelas");
            encomendaBean.create(volumes, "Morada1", "QuimDelas");
            encomendaBean.create(volumes, "Morada2", "Inacio69");

            sensorBean.create( true, "TEMPERATURA", volumes.get(0).getId());
            sensorBean.create( true, "temperatura", volumes.get(1).getId());

            medicaoBean.create(1, 10.77f, 10012025);
            medicaoBean.create(1, 10.27f, 11012025);

            sensorAuthBean.create("Sensor", "123", "Sensor", "Sensor@mail.pt", 1);

        } catch (Exception e) {
            // Log the exception with a message to help debugging
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }

}