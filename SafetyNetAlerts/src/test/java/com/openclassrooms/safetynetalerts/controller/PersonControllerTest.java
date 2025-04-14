package com.openclassrooms.safetynetalerts.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import com.openclassrooms.safetynetalerts.exception.PersonException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.service.PersonService;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PersonService personService;

	@Test
	public void getAllPersonTest() throws Exception {
		List<Person> personList = Arrays.asList(
				new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304", "jean@email.com"),
				new Person("Marie", "Dupont", "10 rue des roses", "Lyon", "69001", "0605060708", "marie@email.com"));

		when(personService.getAllPerson()).thenReturn(personList);

		mockMvc.perform(get("/person/all")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
	}

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

	@Test
	public void addNewPersonBadRequestExceptionTest() throws Exception {
		Person invalidPerson = new Person("", "Martin", "", "Paris", "", "0102030405", "bonjour@email.com");

		when(personService.addPerson(any(Person.class)))
				.thenThrow(new PersonException("Les champs ne doivent pas être vides."));

		mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(invalidPerson))).andExpect(status().isBadRequest());
	}

	@Test
	public void addNewPersonErrorExceptionTest() throws Exception {
		Person person = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean@email.com");

		when(personService.addPerson(any(Person.class))).thenThrow(new RuntimeException("Erreur inattendue"));

		mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(person))).andExpect(status().isInternalServerError());
	}

	@Test
	public void updatePersonNotFoundExceptionTest() throws Exception {
		Person updatedPerson = new Person("Jean", "Martin", "20 rue des plantes", "Paris", "75015", "0605040302",
				"jean@email.com");

		when(personService.updatePerson(eq("Jean"), eq("Martin"), any(Person.class)))
				.thenThrow(new ResourceNotFoundException("Aucune personne ne correspond"));

		mockMvc.perform(put("/person/Jean/Martin").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updatedPerson))).andExpect(status().isNotFound());
	}

	@Test
	public void updatePersonErrorExceptionTest() throws Exception {
		Person updatedPerson = new Person("Jean", "Martin", "20 rue des plantes", "Paris", "75015", "0605040302",
				"jean@email.com");

		when(personService.updatePerson(eq("Jean"), eq("Martin"), any(Person.class)))
				.thenThrow(new RuntimeException("Erreur inattendue"));

		mockMvc.perform(put("/person/Jean/Martin").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updatedPerson)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deletePersonNotFoundExceptionTest() throws Exception {
		String firstName = "Jean";
		String lastName = "Martin";

		doThrow(new ResourceNotFoundException("Personne non trouvée")).when(personService).deletePerson(eq(firstName),
				eq(lastName));

		mockMvc.perform(delete("/person/{firstName}/{lastName}", firstName, lastName)).andExpect(status().isNotFound());

		verify(personService, times(1)).deletePerson(eq(firstName), eq(lastName));
	}

	@Test
	public void deletePersonErrorExceptionTest() throws Exception {
		String firstName = "Jean";
		String lastName = "Martin";

		doThrow(new RuntimeException("Erreur inattendue")).when(personService).deletePerson(eq(firstName),
				eq(lastName));

		mockMvc.perform(delete("/person/{firstName}/{lastName}", firstName, lastName))
				.andExpect(status().isInternalServerError());
	}

}
