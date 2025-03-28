package com.openclassrooms.safetynetalerts.dto;

import java.util.List;

public record PersonByAddressDTO(int station, String firstName, String lastName, String phone, int age,
		List<String> medications, List<String> allergies) {

}
