package com.openclassrooms.safetynetalerts.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class MedicalRecords {
	private String firstName;
	private String lastName;
	private LocalDate birthdate;
	private String[] medications;
	private String[] allergies;

}
