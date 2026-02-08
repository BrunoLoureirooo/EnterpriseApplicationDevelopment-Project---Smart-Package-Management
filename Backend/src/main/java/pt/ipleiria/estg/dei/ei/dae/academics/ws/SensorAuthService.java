package pt.ipleiria.estg.dei.ei.dae.academics.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.FuncionarioDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.dtos.SensorAuthDTO;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.FuncionarioBean;
import pt.ipleiria.estg.dei.ei.dae.academics.ejbs.SensorAuthBean;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.Funcionario;
import pt.ipleiria.estg.dei.ei.dae.academics.entities.SensorAuth;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.academics.exceptions.MyValueIsIncorrectException;
import pt.ipleiria.estg.dei.ei.dae.academics.security.Authenticated;

import java.util.List;

@Path("sensorAuth") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@Authenticated // this service requires authentication
@RolesAllowed({"SensorAuth","Gestor"})
public class SensorAuthService {
    @EJB
    private SensorAuthBean sensorAuthBean;

    @Context
    private SecurityContext securityContext;
    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/student/”
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response getAllSensoresAuth() {
        List<SensorAuth> sensorAuths = sensorAuthBean.findAll();
        return Response.ok(SensorAuthDTO.from(sensorAuths)).build();
    }
    @GET
    @Path("/{sensor_username}") // Relative path for this endpoint
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"SensorAuth","Gestor"})
    public Response getSensorAuthByUsername(@PathParam("sensor_username") String sensorUsername) throws MyEntityNotFoundException {
        var principal = securityContext.getUserPrincipal();
        if(!principal.getName().equals(sensorUsername) && !securityContext.isUserInRole("Gestor")){
            throw new MyEntityNotFoundException("Sensor with username " + sensorUsername + " not found");
        }

        SensorAuth sensorAuth = sensorAuthBean.find(sensorUsername);
        if (sensorAuth == null) {
            throw new MyEntityNotFoundException("Sensor with username " + sensorUsername + " not found");
        }
        return Response.ok(SensorAuthDTO.from(sensorAuth)).build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response createSensorAuth(SensorAuthDTO sensorAuthDTO) throws MyConstraintViolationException, MyValueIsIncorrectException, MyEntityExistsException {
        try{
            SensorAuth sensorAuth = sensorAuthBean.create(sensorAuthDTO.getUsername(), sensorAuthDTO.getPassword(), sensorAuthDTO.getNome(), sensorAuthDTO.getEmail(), sensorAuthDTO.getSensor_id());
            return Response.ok(SensorAuthDTO.from(sensorAuth)).build();
        } catch (MyEntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DELETE
    @Path("/{sensor_username}")
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response deleteSensorAuth(@PathParam("sensor_username") String sensorUsername) throws MyEntityNotFoundException {
        try {
            sensorAuthBean.delete(sensorUsername);
            return Response.ok().build();
        } catch (Exception e) {
            throw new MyEntityNotFoundException("Sensor with username " + sensorUsername + " not found");
        }
    }

    // update a Funcionario
    @PATCH
    @Path("/{sensor_username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @RolesAllowed({"Gestor"})
    public Response updateSensor(@PathParam("sensor_username") String sensorUsername, SensorAuthDTO sensorAuthDTO) throws MyEntityNotFoundException, MyValueIsIncorrectException, MyConstraintViolationException, MyEntityExistsException {
        try{
            sensorAuthBean.update(sensorUsername, sensorAuthDTO.getNome(), sensorAuthDTO.getEmail());
            SensorAuth sensorAuth = sensorAuthBean.find(sensorUsername);
            return Response.ok(SensorAuthDTO.from(sensorAuth)).build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


}
