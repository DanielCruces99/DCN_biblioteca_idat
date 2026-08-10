package com.biblioteca_idat_api.biblioteca_idat_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crearLibroSinAutenticacionDebeResponder401() throws Exception {
        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(libroValido()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuarioNormalNoPuedeCrearLibros() throws Exception {
        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(libroValido()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminConLibroInvalidoDebeRecibir400() throws Exception {
        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\",\"autor\":\"\",\"isbn\":\"\",\"stock\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginSinCredencialesDebeRecibir400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void preflightDesdeOrigenPermitidoDebeIncluirCabecerasCors() throws Exception {
        mockMvc.perform(options("/api/libros")
                        .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        org.hamcrest.Matchers.containsString("GET")));
    }

    @Test
    @WithMockUser(username = "auditor.admin", roles = "ADMIN")
    void creacionDebeRegistrarYExponerAuditoria() throws Exception {
        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Domain-Driven Design\",\"autor\":\"Eric Evans\"," +
                                "\"isbn\":\"9780321125217\",\"stock\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.auditoria.creadoPor").value("auditor.admin"))
                .andExpect(jsonPath("$.auditoria.modificadoPor").value("auditor.admin"))
                .andExpect(jsonPath("$.auditoria.fechaCreacion").isNotEmpty())
                .andExpect(jsonPath("$.auditoria.fechaModificacion").isNotEmpty());
    }

    private String libroValido() {
        return "{\"titulo\":\"Clean Code\",\"autor\":\"Robert C. Martin\","
                + "\"isbn\":\"9780132350884\",\"stock\":3}";
    }
}
