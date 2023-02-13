package ru.kolomiec.taskspring.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import ru.kolomiec.taskspring.TestBase;
import ru.kolomiec.taskspring.dto.PersonRegistrationDTO;
import ru.kolomiec.taskspring.entity.Person;
import ru.kolomiec.taskspring.repository.PersonRepository;

import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthRestControllerTest extends ControllerTestBase {

    @Autowired
    private PersonRepository personRepository;

    // TODO это не юнит тесты, их лучше держать отдельно.
    @Test
    public void shouldRegistrationTestPerson() throws Exception {
        MvcResult result = registerPerson(testPerson);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertTrue(result.getResponse().getContentAsString().contains("token"));
        Assertions.assertTrue(personRepository.findByUsername(testPerson.getUsername()).isPresent());
    }


    @Test
    public void shouldLoginTestPerson() throws Exception {
        registerPerson(testPerson);

        MvcResult requestResult = mockMvc.perform(post(BASE_URL + "/auth/login")
                    .content(objectMapper.writeValueAsString(testPerson))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        Assertions.assertEquals(HttpStatus.OK.value(), requestResult.getResponse().getStatus());
        Assertions.assertTrue(requestResult.getResponse().getContentAsString().contains("token"));
    }

    @Test
    public void shouldNotLoginDefunctPerson() throws Exception {
        MvcResult requestResult = mockMvc.perform(post(BASE_URL + "/auth/login")
                    .content(objectMapper.writeValueAsString(testPerson))
                    .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        Assertions.assertTrue(personRepository.findByUsername(testPerson.getUsername()).isEmpty());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), requestResult.getResponse().getStatus());
    }
}
