package com.universidad.seguridad.config;

import com.universidad.seguridad.model.Tarea;
import com.universidad.seguridad.model.Usuario;
import com.universidad.seguridad.repository.TareaRepository;
import com.universidad.seguridad.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

// Solo en el perfil dev: como H2 arranca vacia en cada ejecucion, se crean un
// ADMIN y un USER de prueba para poder usar las vistas y la API sin pasos extra.
@Configuration
@Profile("dev")
public class DatosDemoDev {

    @Bean
    CommandLineRunner cargarDatosDemo(UsuarioRepository usuarios, TareaRepository tareas,
                                      PasswordEncoder encoder) {
        return args -> {
            if (usuarios.count() > 0) return;
            Usuario admin = crear("Administrador", "admin@universidad.edu", encoder.encode("admin123"), "ROLE_ADMIN");
            Usuario laura = crear("Laura Gomez", "laura@correo.com", encoder.encode("laura123"), "ROLE_USER");
            usuarios.save(admin);
            usuarios.save(laura);

            Tarea t = new Tarea();
            t.setTitulo("Dockerizar la aplicacion");
            t.setDescripcion("Dockerfile multi-stage y perfiles dev/prod");
            t.setPropietario(laura);
            tareas.save(t);
        };
    }

    private Usuario crear(String nombre, String email, String hash, String rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setContrasenia(hash);
        u.setRol(rol);
        return u;
    }
}
