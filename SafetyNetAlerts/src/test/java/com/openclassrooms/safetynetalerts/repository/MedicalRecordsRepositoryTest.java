package com.openclassrooms.safetynetalerts.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.service.JsonService;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordsRepositoryTest {

	@Mock
	private JsonService jsonService;

	@InjectMocks
	private MedicalRecordsRepository medicalRecordsRepository;

	private MedicalRecords medicalRecord;

	@BeforeEach
	void setUp() {
		medicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", List.of("doliprane"), List.of("pollen"));
	}

	@SuppressWarnings("unchecked")
	@Test
	void getAllMedicalRecordsTest() {
		List<MedicalRecords> mrList = List.of(medicalRecord);
		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("medicalrecords"))).thenReturn(mrList);

		List<MedicalRecords> result = medicalRecordsRepository.getAllMedicalRecords();

		assertEquals(1, result.size());
		assertEquals("Jean", result.get(0).getFirstName());
	}

	@SuppressWarnings("unchecked")
	@Test
	void addMedicalRecordTest() {
		List<MedicalRecords> mrList = new ArrayList<>();
		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("medicalrecords"))).thenReturn(mrList);

		MedicalRecords result = medicalRecordsRepository.addMedicalRecord(medicalRecord);

		assertEquals("Jean", result.getFirstName());
		verify(jsonService).writeJsonToFile(eq("medicalrecords"), eq(List.of(medicalRecord)));
	}

	@SuppressWarnings("unchecked")
	@Test
	void updateMedicalRecordTest() {
		MedicalRecords updateMr = new MedicalRecords("Jean", "Martin", "01/01/1990", List.of("aspirin"),
				List.of("gluten"));
		List<MedicalRecords> mrList = new ArrayList<>(List.of(medicalRecord));
		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("medicalrecords"))).thenReturn(mrList);

		MedicalRecords result = medicalRecordsRepository.updateMedicalRecord("Jean", "Martin", updateMr);

		assertEquals("aspirin", result.getMedications().get(0));
		verify(jsonService).writeJsonToFile(eq("medicalrecords"), eq(List.of(updateMr)));
	}

	@SuppressWarnings("unchecked")
	@Test
	void updateMedicalRecordNotFoundTest() {
		List<MedicalRecords> mrList = List.of(); // liste vide
		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("medicalrecords"))).thenReturn(mrList);

		assertThrows(ResourceNotFoundException.class, () -> {
			medicalRecordsRepository.updateMedicalRecord("Pierre", "Dupuis", medicalRecord);
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	void deleteMedicalRecordTest() {
		List<MedicalRecords> mrList = new ArrayList<>(List.of(medicalRecord));
		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("medicalrecords"))).thenReturn(mrList);

		MedicalRecords result = medicalRecordsRepository.deleteMedicalRecord(medicalRecord);

		assertEquals("Jean", result.getFirstName());
		verify(jsonService).writeJsonToFile(eq("medicalrecords"), eq(List.of()));
	}

	@SuppressWarnings("unchecked")
	@Test
	void deleteMedicalRecordNotFoundTest() {
		List<MedicalRecords> mrList = List.of(); // liste vide
		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("medicalrecords"))).thenReturn(mrList);

		assertThrows(ResourceNotFoundException.class, () -> {
			medicalRecordsRepository.deleteMedicalRecord(medicalRecord);
		});
	}

}
