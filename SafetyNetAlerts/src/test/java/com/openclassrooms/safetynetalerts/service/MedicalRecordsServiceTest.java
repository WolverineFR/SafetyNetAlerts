package com.openclassrooms.safetynetalerts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.repository.MedicalRecordsRepository;

@WebMvcTest
public class MedicalRecordsServiceTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
    private JsonService jsonService;
	
	@InjectMocks
    private MedicalRecordsService medicalRecordsService;
	
	@Test
	public void addNewMedicalRecordTest() throws Exception {
		 MedicalRecords newMedicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", List.of("Aspirin"), List.of("Pollen"));
	        
	        doNothing().when(jsonService).writeJsonToFile(eq("medicalRecords"), any());
	        
	        medicalRecordsService.addMedicalRecord(newMedicalRecord);
	        

	    
	}
}
