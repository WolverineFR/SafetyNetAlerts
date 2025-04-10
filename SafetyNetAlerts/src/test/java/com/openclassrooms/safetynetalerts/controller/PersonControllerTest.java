package com.openclassrooms.safetynetalerts.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynetalerts.controller.PersonController;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.service.PersonService;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PersonService personService;

	@Test
	public void addNewPersonTest() throws Exception {
		String firstName = "Jean";
		String lastName = "Martin";
		String address = "1 rue des fleurs";
		String city = "Paris";
		String zip = "75001";
		String phone = "0601020304";
		String email = "jean-martin@email.com";

		Person addNewPerson = new Person(firstName, lastName, address, city, zip, phone, email);

		when(personService.addPerson(any(Person.class))).thenReturn(addNewPerson);

		mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(addNewPerson))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value("Jean")).andExpect(jsonPath("$.lastName").value("Martin"));
	}

	@Test
	public void updatePersonTest() throws Exception {
		String firstName = "Jean";
		String lastName = "Martin";
		String address = "15 rue des tulipes";
		String city = "Paris";
		String zip = "75012";
		String phone = "0701020304";
		String email = "jean-martin@email.com";

		Person updatePerson = new Person(firstName, lastName, address, city, zip, phone, email);

		when(personService.updatePerson(eq(firstName), eq(lastName), any(Person.class))).thenReturn(updatePerson);

		mockMvc.perform(put("/person/Jean/Martin").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updatePerson))).andExpect(status().isOk());
	}

	@Test
	public void deletePersonTest() throws Exception {
		String firstName = "Jean";
		String lastName = "Martin";

		mockMvc.perform(delete("/person/{firstName}/{lastName}", firstName, lastName))
				.andExpect(status().isNoContent());

		verify(personService, times(1)).deletePerson(firstName, lastName);
	}
}
