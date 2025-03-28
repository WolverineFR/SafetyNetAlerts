package com.openclassrooms.safetynetalerts.dto;

import java.util.List;

public record ChildByAddressDTO(String firstName, String lastName, int age, List<String> familyMembers) {

}
