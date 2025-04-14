package com.openclassrooms.safetynetalerts.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynetalerts.dto.FireStationCoverageDTO;
import com.openclassrooms.safetynetalerts.dto.FireStationCoveragePhoneNumberDTO;
import com.openclassrooms.safetynetalerts.dto.FloodListOfStationNumberDTO;
import com.openclassrooms.safetynetalerts.dto.PersonByAddressDTO;
import com.openclassrooms.safetynetalerts.exception.FireStationException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.repository.FireStationRepository;
import com.openclassrooms.safetynetalerts.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class FireStationServiceTest {

	@Mock
	private FireStationRepository fireStationRepository;
	@Mock
	private PersonService personService;
	@Mock
	MedicalRecordsService medicalRecordsService;
	@Mock
	private PersonRepository personRepository;

	@InjectMocks
	private FireStationService fireStationService;

	@BeforeEach
	public void setUp() {
		fireStationService = new FireStationService(medicalRecordsService, personService, fireStationRepository);
	}

	@Test
	void getAllFireStationTest() {
		List<FireStation> fireStationList = List.of(new FireStation("1 rue des fleurs", 3));

		when(fireStationRepository.getAllFireStation()).thenReturn(fireStationList);

		List<FireStation> result = fireStationService.getAllFireStation();

		assertEquals(1, result.size());
		assertEquals("1 rue des fleurs", result.get(0).getAddress());
	}

	@Test
	void addFireStationTest() throws FireStationException {
		FireStation newFireStation = new FireStation("5 rue des marguerittes", 1);

		when(fireStationRepository.getAllFireStation()).thenReturn(List.of());

		when(fireStationRepository.addFireStation(newFireStation)).thenReturn(newFireStation);

		FireStation result = fireStationService.addFireStation(newFireStation);

		assertNotNull(result);
		assertEquals("5 rue des marguerittes", result.getAddress());
	}

	@Test
	void addFireStationAlreadyExistsTest() throws FireStationException {
		FireStation newFireStation = new FireStation("1 rue des fleurs", 1);

		when(fireStationRepository.getAllFireStation()).thenReturn(List.of(newFireStation));

		FireStationException exception = assertThrows(FireStationException.class, () -> {
			fireStationService.addFireStation(newFireStation);
		});

		assertEquals("Une caserne de pompier à cette adresse existe déjà.", exception.getMessage());
	}

	@Test
	void updateFireStationTest() throws ResourceNotFoundException {
		FireStation updateFireStation = new FireStation("1 rue des fleurs", 4);
		when(fireStationRepository.updateFireStation("1 rue des fleurs", updateFireStation)).thenReturn(updateFireStation);

		FireStation result = fireStationService.updateFireStation("1 rue des fleurs", updateFireStation);

		assertNotNull(result);
		assertEquals(4, result.getStation());
	}

	@Test
	void updateFireStationNotFoundTest() {
		FireStation updateFireStation = new FireStation("10 rue des pétales", 3);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			fireStationService.updateFireStation("1 rue des fleurs", updateFireStation);
		});

		assertEquals("L'adresse de la caserne dans l'url ne correspond pas au corps de la requete",
				exception.getMessage());
	}

	@Test
	void deleteFireStationTest() throws ResourceNotFoundException {
		FireStation station = new FireStation("1 rue des fleurs", 1);
		List<FireStation> fireStationList = new ArrayList<>();
		fireStationList.add(station);

		when(fireStationRepository.getAllFireStation()).thenReturn(fireStationList);

		doAnswer(invocation -> {
			List<FireStation> list = invocation.getArgument(0);
			assertEquals(0, list.size());
			return null;
		}).when(fireStationRepository).saveFireStationToJson(anyList());

		fireStationService.deleteFireStation("1 rue des fleurs", 1);

		
		
		
		// TESTS DES URLS
		
	}

	@Test
	void deleteFireStationNotFoundTest() throws ResourceNotFoundException {
		when(fireStationRepository.getAllFireStation()).thenReturn(new ArrayList<>());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			fireStationService.deleteFireStation("1 rue des fleurs", 10);
		});
		assertEquals("Aucune casernes trouvé pour ces informations", exception.getMessage());
	}

	@Test
	void getPersonsByStationNumberTest() throws Exception {
		// GIVEN
		int station = 1;

		FireStation fireStation = new FireStation("1 rue des fleurs", 1);
		when(fireStationRepository.getAllFireStation()).thenReturn(List.of(fireStation));

		Person person1 = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "750001", "0601020304",
				"jean.martin@email.com");
		when(personService.getAllPerson()).thenReturn(List.of(person1));

		MedicalRecords medicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", List.of(), List.of());
		when(medicalRecordsService.getAllMedicalRecords()).thenReturn(List.of(medicalRecord));
		when(medicalRecordsService.calculateAge(medicalRecord)).thenReturn(35);

		// WHEN
		FireStationCoverageDTO result = fireStationService.getPersonsByStationNumber(station);

		//THEN
		assertEquals(1, result.persons().size());
		assertEquals(0, result.numberOfChildren());
		assertEquals(1, result.numberOfAdults());
		assertEquals("Jean", result.persons().get(0).firstName());
	}

	@Test
	void getPhoneNumberByStationNumberTest() throws Exception {
		// GIVEN
		int station = 1;
		FireStation fireStation = new FireStation("1 rue des fleurs", 1);
		when(fireStationRepository.getAllFireStation()).thenReturn(List.of(fireStation));

		Person person1 = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean-martin@email.com");
		Person person2 = new Person("Georges", "Lefebvre", "1 rue des fleurs", "Paris", "75001", "0701020304",
				"gg@email.com"); // Same phone

		when(personService.getAllPerson()).thenReturn(List.of(person1, person2));

		// WHEN
		FireStationCoveragePhoneNumberDTO result = fireStationService.getPhoneNumberByStationNumber(station);

		// THEN
		assertEquals(2, result.phone().size());
		assertTrue(result.phone().contains("0601020304"));
		assertTrue(result.phone().contains("0701020304"));
	}

	@Test
	void getPersonByAddressTest() throws Exception {
		// GIVEN
		String address = "1 rue des fleurs";
		FireStation fireStation = new FireStation(address, 3);
		when(fireStationRepository.getAllFireStation()).thenReturn(List.of(fireStation));

		Person person = new Person("Jean", "Martin", address, "Paris", "75001", "0601020304", "jean-martin@email.com");
		when(personService.getAllPerson()).thenReturn(List.of(person));

		MedicalRecords medicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", List.of("doliprane"),
				List.of("pollen"));
		when(medicalRecordsService.getAllMedicalRecords()).thenReturn(List.of(medicalRecord));
		when(medicalRecordsService.calculateAge(medicalRecord)).thenReturn(35);

		// WHEN
		List<PersonByAddressDTO> result = fireStationService.getPersonByAddress(address);

		// THEN
		PersonByAddressDTO dto = result.get(0);

		assertEquals(3, dto.station());

		assertEquals("Jean", dto.firstName());
		assertEquals("Martin", dto.lastName());
		assertEquals("0601020304", dto.phone());
		assertEquals(35, dto.age());
		assertEquals(List.of("doliprane"), dto.medications());
		assertEquals(List.of("pollen"), dto.allergies());
	}

	@Test
	void getPersonByListOfStationNumberTest() throws Exception {
		// GIVEN
		int stationNumber = 1;
		String address = "1 rue des fleurs";

		FireStation fireStation = new FireStation(address, stationNumber);
		when(fireStationRepository.getAllFireStation()).thenReturn(List.of(fireStation));

		Person person = new Person("Jean", "Dupont", address, "Paris", "75001", "0611223344", "jdp@email.com");
		when(personService.getAllPerson()).thenReturn(List.of(person));

		MedicalRecords medicalRecord = new MedicalRecords("Jean", "Dupont", "01/01/1995", List.of("aspirin"),
				List.of("pollen"));
		when(medicalRecordsService.getAllMedicalRecords()).thenReturn(List.of(medicalRecord));
		when(medicalRecordsService.calculateAge(medicalRecord)).thenReturn(30);

		// WHEN
		List<FloodListOfStationNumberDTO> result = fireStationService.getPersonByListOfStationNumber(stationNumber);

		// THEN
		assertEquals(1, result.size());

		FloodListOfStationNumberDTO dto = result.get(0);
		assertEquals("Jean", dto.firstName());
		assertEquals("Dupont", dto.lastName());
		assertEquals(address, dto.address());
		assertEquals("0611223344", dto.phone());
		assertEquals(30, dto.age());
		assertTrue(dto.medications().contains("aspirin"));
		assertTrue(dto.allergies().contains("pollen"));
	}

}
