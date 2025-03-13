package com.openclassrooms.safetynetalerts.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MedicalRecords {
	private String firstName;
	private String lastName;
	private LocalDate birthdate;
	private String[] medications;
	private String[] allergies;

}
