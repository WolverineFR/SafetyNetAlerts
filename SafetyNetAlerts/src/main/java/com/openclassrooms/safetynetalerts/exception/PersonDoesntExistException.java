package com.openclassrooms.safetynetalerts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersonDoesntExistException extends RuntimeException {
	public PersonDoesntExistException(String s) {
		super(s);
	}
}
