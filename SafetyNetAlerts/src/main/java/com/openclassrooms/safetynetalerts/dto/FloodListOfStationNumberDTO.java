package com.openclassrooms.safetynetalerts.dto;

import java.util.List;

public record FloodListOfStationNumberDTO (String firstName, String lastName, String address, String phone, int age, List<String> medications, List<String> allergies) {

}
