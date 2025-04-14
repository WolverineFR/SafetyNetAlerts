package com.openclassrooms.safetynetalerts.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.openclassrooms.safetynetalerts.exception.MedicalRecordException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.repository.MedicalRecordsRepository;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordsServiceTest {

	@Mock
	private MedicalRecordsRepository medicalRecordsRepository;
	@Mock
	private PersonService personService;

	@InjectMocks
	private MedicalRecordsService medicalRecordsService;

	@BeforeEach
	public void setUp() {
		medicalRecordsService = new MedicalRecordsService(medicalRecordsRepository, personService);
	}

	// Test pour getAllMedicalRecords
	@Test
	public void getAllMedicalRecordsTest() {
		List<MedicalRecords> medicalRecordsList = Arrays.asList(
				new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList(), Arrays.asList("pollen")),
				new MedicalRecords("Pierre", "Dupont", "02/02/1985", Arrays.asList("doliprane"),
						Arrays.asList("gluten")));

		when(medicalRecordsRepository.getAllMedicalRecords()).thenReturn(medicalRecordsList);

		List<MedicalRecords> result = medicalRecordsService.getAllMedicalRecords();

		assertNotNull(result);
		assertEquals(2, result.size());
	}

	// Test pour addMedicalRecord
	@Test
	public void addMedicalRecordTest() throws MedicalRecordException {
		MedicalRecords newMedicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList(),
				Arrays.asList("pollen"));

		when(medicalRecordsRepository.getAllMedicalRecords()).thenReturn(Arrays.asList());
		when(medicalRecordsRepository.addMedicalRecord(any(MedicalRecords.class))).thenReturn(newMedicalRecord);

		MedicalRecords result = medicalRecordsService.addMedicalRecord(newMedicalRecord);

		assertNotNull(result);
		assertEquals("Jean", result.getFirstName());
		assertEquals("Martin", result.getLastName());
	}

	// Test de l'exception : Dossier médical déjà existant
	@Test
	public void addMedicalRecordAlreadyExistsTest() throws MedicalRecordException {
		MedicalRecords newMedicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList(),
				Arrays.asList("pollen"));

		when(medicalRecordsRepository.getAllMedicalRecords()).thenReturn(Arrays
				.asList(new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList(), Arrays.asList("pollen"))));

		MedicalRecordException exception = assertThrows(MedicalRecordException.class, () -> {
			medicalRecordsService.addMedicalRecord(newMedicalRecord);
		});

		assertEquals("Un dossier médical existe déjà pour cette personne.", exception.getMessage());
	}

	// Test pour updateMedicalRecord
	@Test
	public void updateMedicalRecordTest() throws ResourceNotFoundException {
		MedicalRecords updatedMedicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList(),
				Arrays.asList("pollen"));

		when(medicalRecordsRepository.updateMedicalRecord(eq("Jean"), eq("Martin"), any(MedicalRecords.class)))
				.thenReturn(updatedMedicalRecord);

		MedicalRecords result = medicalRecordsService.updateMedicalRecord("Jean", "Martin", updatedMedicalRecord);

		assertNotNull(result);
		assertEquals("Jean", result.getFirstName());
		assertEquals("Martin", result.getLastName());
	}

	// Test pour l'exception lors de la mise à jour : noms ne correspondent pas
	@Test
	public void updateMedicalRecordNotFoundTest() {
		MedicalRecords updatedMedicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList(),
				Arrays.asList("pollen"));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			medicalRecordsService.updateMedicalRecord("Pierre", "Durand", updatedMedicalRecord);
		});

		assertEquals("Prénom et nom de l'URL ne correspondent pas à ceux du corps de la requête.",
				exception.getMessage());
	}

	// Test pour deleteMedicalRecord
	@Test
	public void deleteMedicalRecordTest() throws ResourceNotFoundException {
		String firstName = "Jean";
		String lastName = "Martin";

		MedicalRecords record = new MedicalRecords(firstName, lastName, "01/01/1990", Arrays.asList("doliprane"),
				Arrays.asList("pollen"));

		List<MedicalRecords> medicalRecordsList = new ArrayList<>();
		medicalRecordsList.add(record);

		when(medicalRecordsRepository.getAllMedicalRecords()).thenReturn(medicalRecordsList);

		doNothing().when(personService).deletePerson(firstName, lastName);
		doNothing().when(medicalRecordsRepository).saveMedicalRecordsToJson(any());

		medicalRecordsService.deleteMedicalRecord(firstName, lastName);

		verify(medicalRecordsRepository).saveMedicalRecordsToJson(any());
	}

	// Test pour l'exception lors de la suppression : aucun dossier médical trouvé
	@Test
	public void deleteMedicalRecordNotFoundTest() throws ResourceNotFoundException {
		when(medicalRecordsRepository.getAllMedicalRecords()).thenReturn(Arrays.asList());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			medicalRecordsService.deleteMedicalRecord("Jean", "Martin");
		});

		assertEquals("Aucun dossier médical trouvé pour cette personne.", exception.getMessage());
	}

	// Test pour calculateAge
	@Test
	public void calculateAgeTest() {
		MedicalRecords medicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", Arrays.asList(),
				Arrays.asList("pollen"));

		int age = medicalRecordsService.calculateAge(medicalRecord);

		int expectedAge = LocalDate.now().getYear() - 1990;
		assertEquals(expectedAge, age);
	}
}
