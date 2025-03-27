package com.openclassrooms.safetynetalerts.dto;

import java.util.List;

public record PersonInfoLastNameDTO 
(
		String lastName, String address, int age, String email, List<String> medications, List<String> allergies
		){

}
