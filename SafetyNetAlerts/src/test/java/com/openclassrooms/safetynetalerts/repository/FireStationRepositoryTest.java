package com.openclassrooms.safetynetalerts.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.service.JsonService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FireStationRepositoryTest {

	@Mock
	private JsonService jsonService;

	@InjectMocks
	private FireStationRepository fireStationRepository;
	
	private FireStation fireStation;

	@BeforeEach
	void setUp() {
		fireStationRepository = new FireStationRepository(jsonService);
		fireStation = new FireStation("1 rue des fleurs", 1);
	}

	@SuppressWarnings("unchecked")
	@Test
	void getAllFireStationTest() {
		List<FireStation> fsList = List.of(fireStation);

		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("firestations"))).thenReturn(fsList);

		List<FireStation> result = fireStationRepository.getAllFireStation();

		assertEquals("1 rue des fleurs", result.get(0).getAddress());
		assertEquals(1, result.get(0).getStation());
	}

	@SuppressWarnings("unchecked")
	@Test
	void addFireStationTest() {
		List<FireStation> fsList = new ArrayList<>();
		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("firestations"))).thenReturn(fsList);

		FireStation result = fireStationRepository.addFireStation(fireStation);

		assertNotNull(result);
		assertEquals("1 rue des fleurs", result.getAddress());
		assertEquals(1, result.getStation());

		verify(jsonService, times(1)).writeJsonToFile(eq("firestations"), anyList());
	}

	@SuppressWarnings("unchecked")
	@Test
	void updateFireStationTest() {
		FireStation updateFireStation = new FireStation("1 rue des fleurs", 5);
		List<FireStation> fsList = new ArrayList<>(List.of(fireStation));
		
		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("firestations"))).thenReturn(fsList);
		
		FireStation result = fireStationRepository.updateFireStation("1 rue des fleurs", updateFireStation);

		assertEquals(5, result.getStation());
		assertEquals("1 rue des fleurs", result.getAddress());

		verify(jsonService, times(1)).writeJsonToFile(eq("firestations"), anyList());
	}

	@SuppressWarnings("unchecked")
	@Test
	void updateFireStationNotFoundTest() {
		List<FireStation> fsList = List.of(); // liste vide

		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("firestations"))).thenReturn(fsList);

		assertThrows(ResourceNotFoundException.class, () -> {
			fireStationRepository.updateFireStation("30 rue des pommes", fireStation);
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	void deleteFireStationTest() {
		List<FireStation> fsList = new ArrayList<>(List.of(fireStation));

		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("firestations"))).thenReturn(fsList);

		FireStation result = fireStationRepository.deleteFireStation(fireStation);

		assertEquals("1 rue des fleurs", result.getAddress());
		assertEquals(1, result.getStation());

		verify(jsonService, times(1)).writeJsonToFile(eq("firestations"), anyList());
	}

	@SuppressWarnings("unchecked")
	@Test
	void deleteFireStationNotFoundTest() {
		List<FireStation> fsList = List.of();
		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("firestations"))).thenReturn(fsList);

		assertThrows(ResourceNotFoundException.class, () -> {
			fireStationRepository.deleteFireStation(fireStation);
		});
	}
}
