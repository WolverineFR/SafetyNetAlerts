package com.openclassrooms.safetynetalerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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


}
