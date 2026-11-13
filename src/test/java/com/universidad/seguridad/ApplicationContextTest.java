package com.universidad.seguridad;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Prueba minima para el pipeline: Spring construye el contexto completo con el
// perfil dev (H2 en memoria), sin necesitar una base de datos externa.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ApplicationContextTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Si algun bean (seguridad, JPA, JWT, Actuator) no se puede construir
        // con el perfil dev, esta prueba falla antes de llegar al job de Docker.
    }

    @Test
    void actuatorHealth_sinLogin_respondeUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void apiTareas_sinToken_responde401() throws Exception {
        mockMvc.perform(get("/api/tareas"))
                .andExpect(status().isUnauthorized());
    }
}
