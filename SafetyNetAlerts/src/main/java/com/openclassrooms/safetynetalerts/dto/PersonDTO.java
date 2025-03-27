package com.openclassrooms.safetynetalerts.dto;

import java.util.List;

public record PersonDTO(String firstName, String lastName, String birthdate, String address,

		String city, String zip, String phone, String email, List<String> medications, List<String> allergies

) {

}
