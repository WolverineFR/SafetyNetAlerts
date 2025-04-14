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

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynetalerts.exception.MedicalRecordException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.service.MedicalRecordsService;

@WebMvcTest(MedicalRecordsController.class)
public class MedicalRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MedicalRecordsService medicalRecordsService;

	@Test
	public void getAllMedicalRecordTest() throws Exception {
		List<MedicalRecords> mrList = Arrays.asList(
				new MedicalRecords("Jean", "Martin", "02/03/1990", Arrays.asList("doliprane"), Arrays.asList("pollen")),
				new MedicalRecords("Pierre", "Dupont", "10/25/1975", Arrays.asList(), Arrays.asList("gluten")));

		when(medicalRecordsService.getAllMedicalRecords()).thenReturn(mrList);

		mockMvc.perform(get("/medicalrecord/all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	public void addNewMedicalRecordTest() throws Exception {
		List<String> medications = Arrays.asList("doliprane");
		List<String> allergies = Arrays.asList("pollen");
		String birthdate = "01/01/1990";
		String firstName = "Jean";
		String lastName = "Martin";

		MedicalRecords addNewMedicalRecord = new MedicalRecords(firstName, lastName, birthdate, medications, allergies);

		when(medicalRecordsService.addMedicalRecord(any(MedicalRecords.class))).thenReturn(addNewMedicalRecord);

		mockMvc.perform(post("/medicalrecord").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(addNewMedicalRecord))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value("Jean")).andExpect(jsonPath("$.lastName").value("Martin"));

	}

	@Test
	public void updateMedicalRecordTest() throws Exception {
		List<String> medications = Arrays.asList("toplexil");
		List<String> allergies = Arrays.asList("gluten");
		String birthdate = "01/01/1990";
		String firstName = "Jean";
		String lastName = "Martin";

		MedicalRecords updateMedicalRecord = new MedicalRecords(firstName, lastName, birthdate, medications, allergies);

		when(medicalRecordsService.updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecords.class)))
				.thenReturn(updateMedicalRecord);

		mockMvc.perform(put("/medicalrecord/Jean/Martin").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateMedicalRecord))).andExpect(status().isOk());

	}

	@Test
	public void deleteMedicalRecordTest() throws Exception {
		String firstName = "Jean";
		String lastName = "Martin";

		mockMvc.perform(delete("/medicalrecord/{firstName}/{lastName}", firstName, lastName))
				.andExpect(status().isNoContent());

		verify(medicalRecordsService, times(1)).deleteMedicalRecord(eq(firstName), eq(lastName));
	}

	@Test
	public void addNewMedicalRecordAlreadyExistExceptionTest() throws Exception {
		MedicalRecords mr = new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList("doliprane"),
				Arrays.asList("pollen"));

		when(medicalRecordsService.addMedicalRecord(any(MedicalRecords.class)))
				.thenThrow(new MedicalRecordException("Ce dossier médical existe déjà."));

		mockMvc.perform(post("/medicalrecord").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(mr))).andExpect(status().isBadRequest());
	}

	@Test
	public void addNewMedicalRecordErrorExceptionTest() throws Exception {
		MedicalRecords mr = new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList("doliprane"),
				Arrays.asList("pollen"));

		when(medicalRecordsService.addMedicalRecord(any(MedicalRecords.class)))
				.thenThrow(new RuntimeException("Erreur inconnue"));

		mockMvc.perform(post("/medicalrecord").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(mr))).andExpect(status().isInternalServerError());
	}

	@Test
	public void updateMedicalRecordNotFoundExceptionTest() throws Exception {
		MedicalRecords mrUpdate = new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList("doliprane"),
				Arrays.asList("pollen"));

		when(medicalRecordsService.updateMedicalRecord(eq("Jean"), eq("Martin"), any(MedicalRecords.class)))
				.thenThrow(new ResourceNotFoundException("Dossier médical non trouvé"));

		mockMvc.perform(put("/medicalrecord/Jean/Martin").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(mrUpdate))).andExpect(status().isNotFound());
	}

	@Test
	public void updateMedicalRecordIllegalArgumentExceptionTest() throws Exception {
		MedicalRecords mrUpdate = new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList("doliprane"),
				Arrays.asList("pollen"));

		when(medicalRecordsService.updateMedicalRecord(eq("Jean"), eq("Martin"), any(MedicalRecords.class)))
				.thenThrow(new IllegalArgumentException("Les noms ou prénoms ne correspondent pas"));

		mockMvc.perform(put("/medicalrecord/Jean/Martin").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(mrUpdate))).andExpect(status().isBadRequest());
	}

	@Test
	public void deleteMedicalRecordNotFoundExceptionTest() throws Exception {
		String firstName = "Jean";
		String lastName = "Martin";

		doThrow(new ResourceNotFoundException("Dossier médical introuvable")).when(medicalRecordsService)
				.deleteMedicalRecord(eq(firstName), eq(lastName));

		mockMvc.perform(delete("/medicalrecord/{firstName}/{lastName}", firstName, lastName))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteMedicalRecordErrorExceptionTest() throws Exception {
		String firstName = "Jean";
		String lastName = "Martin";

		doThrow(new RuntimeException("Erreur interne")).when(medicalRecordsService)
				.deleteMedicalRecord(eq(firstName), eq(lastName));

		mockMvc.perform(delete("/medicalrecord/{firstName}/{lastName}", firstName, lastName))
				.andExpect(status().isInternalServerError());
	}

}
