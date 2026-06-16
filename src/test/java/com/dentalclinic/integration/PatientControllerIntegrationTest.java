package com.dentalclinic.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String getAuthToken() throws Exception {
        String loginJson = """
                {
                    "username": "admin",
                    "password": "admin123"
                }
                """;
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        int start = responseBody.indexOf("\"accessToken\":\"") + 15;
        int end = responseBody.indexOf("\"", start);
        return responseBody.substring(start, end);
    }

    @Test
    void create_patient_integration() throws Exception {
        String token = getAuthToken();

        String patientJson = """
                {
                    "firstName": "Integration",
                    "lastName": "Test",
                    "phone": "+9999999999",
                    "email": "integration@test.com",
                    "dateOfBirth": "1995-01-01",
                    "gender": "MALE"
                }
                """;

        mockMvc.perform(post("/patients")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("Integration"));
    }

    @Test
    void get_patient_by_id_integration() throws Exception {
        String token = getAuthToken();

        String patientJson = """
                {
                    "firstName": "GetTest",
                    "lastName": "Patient",
                    "phone": "+8888888888",
                    "email": "gettest@test.com",
                    "dateOfBirth": "1990-05-15",
                    "gender": "FEMALE"
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/patients")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String idStr = "\"id\":";
        int start = responseBody.indexOf(idStr) + idStr.length();
        int end = responseBody.indexOf(",", start);
        Long id = Long.parseLong(responseBody.substring(start, end).trim());

        mockMvc.perform(get("/patients/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("GetTest"));
    }

    @Test
    void search_patients_integration() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/patients/search")
                        .header("Authorization", "Bearer " + token)
                        .param("query", "Integration")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void create_patient_invalid_data() throws Exception {
        String token = getAuthToken();

        String invalidJson = """
                {
                    "firstName": "",
                    "lastName": "",
                    "phone": "invalid",
                    "dateOfBirth": "2030-01-01"
                }
                """;

        mockMvc.perform(post("/patients")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_patient_not_found() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/patients/99999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
