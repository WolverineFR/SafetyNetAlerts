package com.openclassrooms.safetynetalerts.dto;

import java.util.List;

public record FireStationCoverageDTO (
		 List<PersonFireStationDTO> persons,
		 int numberOfAdults,
		 int numberOfChildren
) {
	
}
