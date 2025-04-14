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

import com.openclassrooms.safetynetalerts.exception.FireStationException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.repository.FireStationRepository;

@ExtendWith(MockitoExtension.class)
public class FireStationServiceTest {

	@Mock
	private FireStationRepository fireStationRepository;
	@Mock
	private PersonService personService;
	@Mock
	MedicalRecordsService medicalRecordsService;

	@InjectMocks
	private FireStationService fireStationService;

	@BeforeEach
	public void setUp() {
		fireStationService = new FireStationService(medicalRecordsService, personService, fireStationRepository);
	}

	@Test
	void getAllFireStationTest() {
		List<FireStation> fireStationList = List.of(new FireStation("1509 Culver St", 3));

		when(fireStationRepository.getAllFireStation()).thenReturn(fireStationList);

		List<FireStation> result = fireStationService.getAllFireStation();

		assertEquals(1, result.size());
		assertEquals("1509 Culver St", result.get(0).getAddress());
	}

	@Test
	void addFireStationTest() throws FireStationException {
		FireStation newFireStation = new FireStation("123 Main St", 1);

		when(fireStationRepository.getAllFireStation()).thenReturn(List.of());

		when(fireStationRepository.addFireStation(newFireStation)).thenReturn(newFireStation);

		FireStation result = fireStationService.addFireStation(newFireStation);

		assertNotNull(result);
		assertEquals("123 Main St", result.getAddress());
	}

	@Test
	void addFireStationAlreadyExistsTest() throws FireStationException {
		FireStation newFireStation = new FireStation("123 Main St", 1);

		when(fireStationRepository.getAllFireStation()).thenReturn(List.of(newFireStation));

		FireStationException exception = assertThrows(FireStationException.class, () -> {
			fireStationService.addFireStation(newFireStation);
		});

		assertEquals("Une caserne de pompier à cette adresse existe déjà.", exception.getMessage());
	}

	@Test
	void updateFireStationTest() throws ResourceNotFoundException {
		FireStation updateFireStation = new FireStation("123 Main St", 4);
		when(fireStationRepository.updateFireStation("123 Main St", updateFireStation)).thenReturn(updateFireStation);

		FireStation result = fireStationService.updateFireStation("123 Main St", updateFireStation);

		assertNotNull(result);
		assertEquals(4, result.getStation());
	}

	@Test
	void updateFireStationNotFoundTest() {
		FireStation updateFireStation = new FireStation("Another St", 3);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			fireStationService.updateFireStation("123 Main St", updateFireStation);
		});

		assertEquals("L'adresse de la caserne dans l'url ne correspond pas au corps de la requete",
				exception.getMessage());
	}

	@Test
	void deleteFireStationTest() throws ResourceNotFoundException {
		FireStation station = new FireStation("123 Main St", 1);
		List<FireStation> fireStationList = new ArrayList<>();
		fireStationList.add(station);

		when(fireStationRepository.getAllFireStation()).thenReturn(fireStationList);

		doAnswer(invocation -> {
			List<FireStation> list = invocation.getArgument(0);
			assertEquals(0, list.size());
			return null;
		}).when(fireStationRepository).saveFireStationToJson(anyList());

		fireStationService.deleteFireStation("123 Main St", 1);
		
	}

	@Test
	void deleteFireStationNotFoundTest() throws ResourceNotFoundException {
		when(fireStationRepository.getAllFireStation()).thenReturn(new ArrayList<>());
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> {
					fireStationService.deleteFireStation("1 rue des fleurs", 10);
				});
		assertEquals("Aucune casernes trouvé pour ces informations", exception.getMessage());
	}

}
