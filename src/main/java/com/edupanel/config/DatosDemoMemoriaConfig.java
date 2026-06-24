package com.edupanel.config;

import com.edupanel.model.Alumno;
import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Calificacion;
import com.edupanel.repository.AlumnoRepository;
import com.edupanel.repository.AnuncioRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!firebase")
public class DatosDemoMemoriaConfig {

    /*
    NOTA JOACO: como dice el nombre es una clase temporal solo para probar la aplicacion sin base de datos. 
    Cuando los repositorios Firebase funcionen, esta clase no se usara.

    Manten esta clase y las demas hasta que firebase funcione 100% ya que permite iniciar la app y seguir 
    guardando en memoria a la vez para seguir ejecutando las pruebas. En caso que falle algo con tu config
    borralo nomas ya que la prioridad es integrar firebase
    */ 
     

    private final AlumnoRepository alumnoRepository;
    private final AnuncioRepository anuncioRepository;

    public DatosDemoMemoriaConfig(AlumnoRepository alumnoRepository, AnuncioRepository anuncioRepository) {
        this.alumnoRepository = alumnoRepository;
        this.anuncioRepository = anuncioRepository;
    }

    @PostConstruct
    public void cargarDatosDemo() {
        cargarAlumnoDemo();
        cargarAnuncioDemo();
    }

    private void cargarAlumnoDemo() {
        Alumno alumno = new Alumno();
        alumno.setUid("1");
        alumno.setNombre("Joaquin");
        alumno.setApellido("Astudillo");
        alumno.setRut("11.111.111-1");
        alumno.setEmail("joaquin@correo.com");
        alumno.setPassword("123456");

        Calificacion nota = new Calificacion();
        nota.setId("1");
        nota.setAlumnoId("1");
        nota.setAsignatura(Asignatura.MATEMATICAS);
        nota.setNota(6.0);
        nota.setDescripcion("Prueba 1");

        List<Calificacion> notas = new ArrayList<>();
        notas.add(nota);

        alumno.setNotas(notas);

        alumnoRepository.guardar(alumno);
    }

    private void cargarAnuncioDemo() {
        Anuncio anuncio = new Anuncio();
        anuncio.setId(UUID.randomUUID().toString());
        anuncio.setTitulo("Bienvenidos a EduPanel");
        anuncio.setMensaje("Este es el primer anuncio del sistema.");
        anuncio.setAsignatura(Asignatura.MATEMATICAS);
        anuncio.setProfesorId("profesor-demo");
        anuncio.setFechaPublicacion(LocalDateTime.now());

        anuncioRepository.guardar(anuncio);
    }
}
