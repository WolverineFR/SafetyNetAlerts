package com.openclassrooms.safetynetalerts.model;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class FireStation {
	private String address;
	private int station;
}
