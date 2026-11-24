package com.cesul.barber;

import com.cesul.barber.repositories.UsersRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://127.0.0.1:${BARBER_TEST_DB_PORT:5433}/barber_tests",
        "spring.datasource.username=barber_test",
        "spring.datasource.password=${BARBER_TEST_DB_PASSWORD}",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class BarberApplicationTests {

    private static final String USER_REQUEST = """
            {
              "id": "00000000-0000-0000-0000-000000000001",
              "name": "Ana",
              "email": "ana@example.test",
              "avatarUrl": "https://example.test/ana.png"
            }
            """;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    void managesUsersThroughHttp() throws Exception {
        MvcResult result = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ana"))
                .andReturn();

        UUID id = UUID.fromString(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
        assertNotEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), id);
        assertEquals("/users/" + id, URI.create(result.getResponse().getHeader("Location")).getPath());
        assertTrue(usersRepository.existsById(id));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id.toString()));

        mvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@example.test"));

        mvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ana Silva", "email": "ana.silva@example.test"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Ana Silva"))
                .andExpect(jsonPath("$.avatarUrl", nullValue()));

        String avatarUrl = "https://example.test/avatar?signature=" + "x".repeat(300);
        mvc.perform(patch("/users/{id}/avatar", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"avatarUrl": "%s", "name": "Outro nome"}
                                """.formatted(avatarUrl)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana Silva"))
                .andExpect(jsonPath("$.email").value("ana.silva@example.test"))
                .andExpect(jsonPath("$.avatarUrl").value(avatarUrl));

        mvc.perform(patch("/users/{id}/avatar", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl").value(avatarUrl));

        mvc.perform(patch("/users/{id}/avatar", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"avatarUrl": null}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl", nullValue()));

        mvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana Silva"))
                .andExpect(jsonPath("$.avatarUrl", nullValue()));

        mvc.perform(delete("/users/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/users/{id}", id)).andExpect(status().isNotFound());
        mvc.perform(delete("/users/{id}", id)).andExpect(status().isNotFound());
        mvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_REQUEST))
                .andExpect(status().isNotFound());
        mvc.perform(patch("/users/{id}/avatar", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"avatarUrl": "https://example.test/novo.png"}
                                """))
                .andExpect(status().isNotFound());

        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(USER_REQUEST))
                .andExpect(status().isCreated());
        mvc.perform(delete("/users/clear")).andExpect(status().isNoContent());
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void documentsUserEndpoints() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/users'].get").exists())
                .andExpect(jsonPath("$.paths['/users'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/users/{id}'].get.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/users/{id}'].put").exists())
                .andExpect(jsonPath("$.paths['/users/{id}'].delete.responses['204']").exists())
                .andExpect(jsonPath("$.paths['/users/{id}/avatar'].patch").exists())
                .andExpect(jsonPath("$.paths['/users/clear'].delete.responses['204']").exists());

        mvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
