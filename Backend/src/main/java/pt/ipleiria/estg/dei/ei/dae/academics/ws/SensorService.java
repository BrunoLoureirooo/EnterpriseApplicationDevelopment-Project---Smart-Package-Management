package pt.ipleiria.estg.dei.ei.dae.academics.ws;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.MedicaoDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.SensorDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.VolumeDTO;
import jakarta.ejb.EJB;
import jakarta.ws.rs.core.MediaType;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.MedicaoBean;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.SensorAuthBean;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.SensorBean;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.VolumeBean;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Medicao;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Sensor;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Volume;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Authenticated;

import java.util.ArrayList;
import java.util.List;

@Path("sensor") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class SensorService {
    @EJB
    private SensorBean sensorBean;
    @EJB
    private MedicaoBean medicaoBean;
    @EJB
    private SensorAuthBean sensorAuthBean;

    @Context
    private SecurityContext securityContext;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/student/”
    @Authenticated
    @RolesAllowed({"Gestor", "Funcionario", "SensorAuth"})
    public Response getAllSensores() {
        List<Sensor> sensores = sensorBean.findAll(); // Busca todos os sensores
        List<SensorDTO> listaDTOs = new ArrayList<>();

        for (Sensor sensor : sensores) {
            if (sensor.getSensorAuth() != null) {
                sensor.setSensorAuth(null); // Remove o SensorAuth para evitar NullPointerException
            }
            listaDTOs.add(SensorDTO.from(sensor)); // Converte o Sensor para SensorDTO
        }

        return Response.ok(listaDTOs).build(); // Retorna a lista de SensorDTOs na resposta
    }


    @GET
    @Path("/{sensor_id}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor","Cliente","Funcionario","SensorAuth"})
    public Response getSensorById(@PathParam("sensor_id") Long sensorID) throws MyEntityNotFoundException {
        Sensor sensor = sensorBean.find(sensorID);
        if (sensor == null) {
            throw new MyEntityNotFoundException("Product with name " + sensorID + " not found");
        }
        return Response.status(Response.Status.OK).entity(SensorDTO.from(sensor)).build();
    }

    @GET
    @Path("/{sensor_id}/medicoes")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor","Cliente","Funcionario"})
    public Response getSensorMedicoes(@PathParam("sensor_id") Long sensorID) throws MyEntityNotFoundException {
        Sensor sensor = sensorBean.find(sensorID);
        if (sensor == null) {
            throw new MyEntityNotFoundException("Sensor with ID " + sensorID + " not found");
        }
        List<MedicaoDTO> medicoes = MedicaoDTO.from(sensorBean.getHistorico(sensor));
        return Response.status(Response.Status.OK).entity(medicoes).build();
    }

    // POST for medições
    @POST
    @Path("/{sensor_id}/medicoes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"SensorAuth"})
    public Response addMedicao(@PathParam("sensor_id") Long sensorID, MedicaoDTO medicaoDTO) throws MyEntityNotFoundException {
        var principal = securityContext.getUserPrincipal();

        if(!principal.getName().equals(sensorAuthBean.findBySensor(sensorID).getUsername())){
            throw new MyEntityNotFoundException("Sensor with ID " + sensorID + " not found");
        }

        try{
            Medicao medicao = medicaoBean.create(sensorID, medicaoDTO.getValor(), medicaoDTO.getData());
        }catch (MyEntityNotFoundException e){
            throw new MyEntityNotFoundException("Sensor with ID " + sensorID + " not found");
        } catch (MyValueIsIncorrectException e) {
            throw new RuntimeException(e);
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/")
    @Authenticated
    @RolesAllowed({"Gestor","Funcionario"})
    public Response createSensor(SensorDTO sensorDTO) throws MyEntityNotFoundException, MyEntityExistsException, MyValueIsIncorrectException {
        try{
            Sensor sensor = sensorBean.create(sensorDTO.isAtivo(), sensorDTO.getTipo(), sensorDTO.getVolumeId());
            return Response.status(Response.Status.CREATED).entity(SensorDTO.from(sensor)).build();
        } catch (MyEntityNotFoundException e){
            throw new MyEntityNotFoundException("Volume with ID " + sensorDTO.getVolumeId() + " not found");
        } catch (MyValueIsIncorrectException e) {
            throw new MyValueIsIncorrectException("Sensor type " + sensorDTO.getTipo() + " is invalid");
        }
    }

    @PATCH
    @Path("/{sensor_id}")
    @Authenticated
    @RolesAllowed({"Gestor","Funcionario"})
    public Response updateSensor(@PathParam("sensor_id") Long sensorID, SensorDTO sensorDTO) throws MyEntityNotFoundException, MyValueIsIncorrectException {
        try{
            Sensor sensor = sensorBean.update(sensorID, sensorDTO.isAtivo(), sensorDTO.getTipo(), sensorDTO.getVolumeId());
            return Response.status(Response.Status.OK).entity(SensorDTO.from(sensor)).build();
        } catch (MyEntityNotFoundException e){
            throw new MyEntityNotFoundException("Sensor with ID " + sensorID + " not found");
        } catch (MyValueIsIncorrectException e) {
            throw new MyValueIsIncorrectException("Sensor type " + sensorDTO.getTipo() + " is invalid");
        }
    }
}