package com.openclassrooms.safetynetalerts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynetalerts.controller.MedicalRecordsController;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.service.MedicalRecordsService;

@WebMvcTest(MedicalRecordsController.class)
public class MedicalRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MedicalRecordsService medicalRecordsService;

	@Test
	public void addNewMedicalRecordTest() throws Exception {
		List<String> medications = Arrays.asList("doliprane");
		List<String> allergies = Arrays.asList("pollen");

		MedicalRecords addNewMedicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", medications, allergies);

		doNothing().when(medicalRecordsService).addMedicalRecord(any(MedicalRecords.class));

		mockMvc.perform(post("/medicalrecord").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(addNewMedicalRecord))).andExpect(status().isCreated())
				.andExpect(content().string("Rapport medical enregistré avec succès !"));

	}

	@Test
	public void updateMedicalRecordTest() throws Exception {
		List<String> medications = Arrays.asList("toplexil");
		List<String> allergies = Arrays.asList("gluten");

		MedicalRecords updateMedicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", medications, allergies);

		when(medicalRecordsService.updateMedicalRecord(any(MedicalRecords.class))).thenReturn(updateMedicalRecord);

		mockMvc.perform(put("/medicalrecord/Jean/Martin").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateMedicalRecord))).andExpect(status().isOk());

	}

	@Test
	public void deleteMedicalRecordTest() throws Exception {
		List<String> medications = Arrays.asList("toplexil");
		List<String> allergies = Arrays.asList("gluten");
		String firstName = "Jean";
		String lastName = "Martin";

		MedicalRecords deleteMedicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", medications, allergies);
		;

		when(medicalRecordsService.deleteMedicalRecord(deleteMedicalRecord)).thenReturn(deleteMedicalRecord);

		mockMvc.perform(delete("/medicalrecord/Jean/Martin").param("firstName", firstName).param("lastName", lastName))
				.andExpect(status().isOk());
	}

}
