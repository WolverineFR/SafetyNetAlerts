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

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.service.FireStationService;

@WebMvcTest(FireStationController.class)
public class FireStationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FireStationService fireStationService;

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
}
