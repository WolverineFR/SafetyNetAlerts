package com.openclassrooms.safetynetalerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynetalerts.dto.FireStationCoverageDTO;
import com.openclassrooms.safetynetalerts.dto.FireStationCoveragePhoneNumberDTO;
import com.openclassrooms.safetynetalerts.dto.FloodListOfStationNumberDTO;
import com.openclassrooms.safetynetalerts.dto.PersonByAddressDTO;
import com.openclassrooms.safetynetalerts.dto.PersonFireStationDTO;
import com.openclassrooms.safetynetalerts.exception.FireStationException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.service.FireStationService;

@WebMvcTest(FireStationController.class)
public class FireStationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FireStationService fireStationService;

	@Test
	public void getAllFireStationsTest() throws Exception {
		List<FireStation> fireStations = List.of(new FireStation("1 rue des Lilas", 1),
				new FireStation("2 rue des Roses", 2));

		when(fireStationService.getAllFireStation()).thenReturn(fireStations);

		mockMvc.perform(get("/firestation/all")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	public void addNewFireStationTest() throws Exception {
		String address = "1 rue des fleurs";
		int station = 1;

		FireStation addNewFireStation = new FireStation(address, station);

		when(fireStationService.addFireStation(any(FireStation.class))).thenReturn(addNewFireStation);

		mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(addNewFireStation))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.address").value("1 rue des fleurs")).andExpect(jsonPath("$.station").value(1));

	}

	@Test
	public void updateFireStationTest() throws Exception {
		String address = "1 rue des fleurs";
		int station = 5;

		FireStation updateFireStation = new FireStation(address, station);

		when(fireStationService.updateFireStation(eq(address), any(FireStation.class))).thenReturn(updateFireStation);

		mockMvc.perform(put("/firestation/{address}", address).contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateFireStation))).andExpect(status().isOk());
	}

	@Test
	public void deleteFireStationTest() throws Exception {
		String address = "1 rue des fleurs";
		int station = 5;

		mockMvc.perform(delete("/firestation/{address}/{station}", address, station)).andExpect(status().isNoContent());

		verify(fireStationService, times(1)).deleteFireStation(eq(address), eq(station));
	}

	@Test
	public void addNewFireStationAlreadyExistExceptionTest() throws Exception {
		FireStation newFS = new FireStation("10 rue des Lilas", 3);
		when(fireStationService.addFireStation(any(FireStation.class)))
				.thenThrow(new FireStationException("Adresse déjà existante"));

		mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(newFS))).andExpect(status().isBadRequest());
	}

	@Test
	public void addNewFireStationErrorExceptionTest() throws Exception {
		FireStation newFS = new FireStation("10 rue des Lilas", 3);
		when(fireStationService.addFireStation(any(FireStation.class)))
				.thenThrow(new RuntimeException("Erreur serveur"));

		mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(newFS))).andExpect(status().isInternalServerError());
	}

	@Test
	public void updateFireStationNotFoundExceptionTest() throws Exception {
		FireStation updateFS = new FireStation("1 rue des fleurs", 8);
		when(fireStationService.updateFireStation(eq("1 rue des fleurs"), any(FireStation.class)))
				.thenThrow(new ResourceNotFoundException("Caserne de pompier non trouvée"));

		mockMvc.perform(put("/firestation/1 rue des fleurs").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateFS))).andExpect(status().isNotFound());
	}

	@Test
	public void updateFireStationErrorExceptionTest() throws Exception {
		FireStation updateFS = new FireStation("1 rue des fleurs", 8);
		when(fireStationService.updateFireStation(eq("1 rue des fleurs"), any(FireStation.class)))
				.thenThrow(new RuntimeException("Erreur serveur"));

		mockMvc.perform(put("/firestation/1 rue des fleurs").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateFS))).andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteFireStationNotFoundExceptionTest() throws Exception {
		String address = "1 rue des fleurs";
		int station = 5;

		doThrow(new ResourceNotFoundException("Caserne de pompier introuvable")).when(fireStationService)
				.deleteFireStation(eq(address), eq(station));

		mockMvc.perform(delete("/firestation/{address}/{station}", address, station)).andExpect(status().isNotFound());
	}

	@Test
	public void deleteFireStationErrorExceptionTest() throws Exception {
		String address = "1 rue des fleurs";
		int station = 5;

		doThrow(new RuntimeException("Erreur interne")).when(fireStationService).deleteFireStation(eq(address),
				eq(station));

		mockMvc.perform(delete("/firestation/{address}/{station}", address, station))
				.andExpect(status().isInternalServerError());
	}

	// URLs

	@Test
	public void getPersonsByStationNumberTest() throws Exception {
		int stationNumber = 1;

		PersonFireStationDTO person1 = new PersonFireStationDTO("Jean", "Martin", "1 rue des fleurs", "0601020304");
		PersonFireStationDTO person2 = new PersonFireStationDTO("Pierre", "Durant", "1 rue des roses", "0703020104");

		List<PersonFireStationDTO> persons = List.of(person1, person2);

		FireStationCoverageDTO coverageDTO = new FireStationCoverageDTO(persons, 1, 1); // 1 adulte, 1 enfant

		when(fireStationService.getPersonsByStationNumber(stationNumber)).thenReturn(coverageDTO);

		mockMvc.perform(get("/firestation").param("stationNumber", String.valueOf(stationNumber)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.numberOfAdults").value(1))
				.andExpect(jsonPath("$.numberOfChildren").value(1))
				.andExpect(jsonPath("$.persons[0].firstName").value("Jean"))
				.andExpect(jsonPath("$.persons[0].lastName").value("Martin"))
				.andExpect(jsonPath("$.persons[0].address").value("1 rue des fleurs"))
				.andExpect(jsonPath("$.persons[0].phone").value("0601020304"))
				.andExpect(jsonPath("$.persons[1].firstName").value("Pierre"))
				.andExpect(jsonPath("$.persons[1].lastName").value("Durant"))
				.andExpect(jsonPath("$.persons[1].address").value("1 rue des roses"))
				.andExpect(jsonPath("$.persons[1].phone").value("0703020104"));
	}

	@Test
	public void getPersonsByStationNumberNotFoundTest() throws Exception {
		int stationNumber = 1;

		when(fireStationService.getPersonsByStationNumber(stationNumber))
				.thenThrow(new ResourceNotFoundException("Station non trouvée"));

		mockMvc.perform(get("/firestation").param("stationNumber", String.valueOf(stationNumber)))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getPersonsByStationNumberErrorTest() throws Exception {
		int stationNumber = 1;

		when(fireStationService.getPersonsByStationNumber(stationNumber))
				.thenThrow(new RuntimeException("Erreur serveur"));

		mockMvc.perform(get("/firestation").param("stationNumber", String.valueOf(stationNumber)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getPhoneNumberByStationNumberTest() throws Exception {
		int stationNumber = 3;
		List<String> phoneNumbers = List.of("0601020304", "0704050607");

		FireStationCoveragePhoneNumberDTO dto = new FireStationCoveragePhoneNumberDTO(phoneNumbers);

		when(fireStationService.getPhoneNumberByStationNumber(stationNumber)).thenReturn(dto);

		mockMvc.perform(get("/phoneAlert").param("firestation", String.valueOf(stationNumber)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.phone.length()").value(2))
				.andExpect(jsonPath("$.phone[0]").value("0601020304"))
				.andExpect(jsonPath("$.phone[1]").value("0704050607"));
	}

	@Test
	public void getPhoneNumberByStationNumberNotFoundTest() throws Exception {
		int stationNumber = 5;

		when(fireStationService.getPhoneNumberByStationNumber(stationNumber))
				.thenThrow(new ResourceNotFoundException("Station non trouvée"));

		mockMvc.perform(get("/phoneAlert").param("firestation", String.valueOf(stationNumber)))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getPhoneNumberByStationNumberErrorTest() throws Exception {
		int stationNumber = 5;

		when(fireStationService.getPhoneNumberByStationNumber(stationNumber))
				.thenThrow(new RuntimeException("Erreur serveur"));

		mockMvc.perform(get("/phoneAlert").param("firestation", String.valueOf(stationNumber)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getPersonByAddressTest() throws Exception {
		String address = "1509 Culver St";

		PersonByAddressDTO person1 = new PersonByAddressDTO(3, "John", "Boyd", "841-874-6512", 36,
				List.of("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));

		PersonByAddressDTO person2 = new PersonByAddressDTO(3, "Jacob", "Boyd", "841-874-6513", 31, List.of(),
				List.of());

		List<PersonByAddressDTO> people = List.of(person1, person2);

		when(fireStationService.getPersonByAddress(address)).thenReturn(people);

		mockMvc.perform(get("/fire").param("address", address)).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].medications[0]").value("aznol:350mg"))
				.andExpect(jsonPath("$[1].lastName").value("Boyd"));
	}

	@Test
	public void getPersonByAddressNotFoundTest() throws Exception {
		String address = "Adresse inconnue";

		when(fireStationService.getPersonByAddress(address))
				.thenThrow(new ResourceNotFoundException("Adresse introuvable"));

		mockMvc.perform(get("/fire").param("address", address)).andExpect(status().isNotFound());
	}

	@Test
	public void getPersonByAddressErrorTest() throws Exception {
		String address = "Erreur";

		when(fireStationService.getPersonByAddress(address)).thenThrow(new RuntimeException("Erreur serveur"));

		mockMvc.perform(get("/fire").param("address", address)).andExpect(status().isInternalServerError());
	}

	@Test
	public void getPersonByListOfStationNumberTest() throws Exception {
		int station = 1;

		FloodListOfStationNumberDTO person1 = new FloodListOfStationNumberDTO("John", "Doe", "1509 Culver St",
				"841-874-6512", 36, List.of("aznol:350mg"), List.of("nillacilan"));

		FloodListOfStationNumberDTO person2 = new FloodListOfStationNumberDTO("Jane", "Doe", "1509 Culver St",
				"841-874-6513", 34, List.of("hydrapermazol:100mg"), List.of());

		List<FloodListOfStationNumberDTO> persons = List.of(person1, person2);

		when(fireStationService.getPersonByListOfStationNumber(station)).thenReturn(persons);

		mockMvc.perform(get("/flood/station").param("firestation", String.valueOf(station))).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[1].medications[0]").value("hydrapermazol:100mg"))
				.andExpect(jsonPath("$[0].allergies[0]").value("nillacilan"));
	}

	@Test
	public void getPersonByListOfStationNumberNotFoundTest() throws Exception {
		int station = 10;

		when(fireStationService.getPersonByListOfStationNumber(station))
				.thenThrow(new ResourceNotFoundException("Station introuvable"));

		mockMvc.perform(get("/flood/station").param("firestation", String.valueOf(station)))
				.andExpect(status().isNotFound());
	}

}
