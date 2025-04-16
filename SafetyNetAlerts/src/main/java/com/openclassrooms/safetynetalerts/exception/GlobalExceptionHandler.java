package com.openclassrooms.safetynetalerts.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Classe centrale de gestion des exceptions pour l'application.
 * Utilise l'annotation @ControllerAdvice pour intercepter les erreurs globalement
 * et retourner des réponses HTTP cohérentes.
 */

@ControllerAdvice
public class GlobalExceptionHandler {
	
	 /**
     * Gère les exceptions IllegalArgumentException.
     * @param ex : Exception levée.
     * @return une réponse HTTP 400 avec message personnalisé.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les erreurs spécifiques aux dossiers médicaux.
     * @param ex : Exception personnalisée MedicalRecordException.
     * @return une réponse HTTP 400.
     */
    @ExceptionHandler(MedicalRecordException.class)
    public ResponseEntity<Object> handleMedicalRecordError(MedicalRecordException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Invalid medical record data");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Gère les erreurs liées aux personnes.
     * @param ex : Exception personnalisée PersonException.
     * @return une réponse HTTP 400.
     */
    @ExceptionHandler(PersonException.class)
    public ResponseEntity<Object> handlePersonError(PersonException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Invalid person data");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les erreurs liées aux casernes de pompiers.
     * @param ex : Exception personnalisée FireStationException.
     * @return une réponse HTTP 400.
     */
    @ExceptionHandler(FireStationException.class)
    public ResponseEntity<Object> handleFireStationError(FireStationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Invalid fire station data");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les erreurs génériques non spécifiquement capturées.
     * @param ex : Exception inattendue.
     * @return une réponse HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralError(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 500);
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Gère les cas où une ressource demandée est introuvable.
     * @param ex : Exception personnalisée ResourceNotFoundException.
     * @return une réponse HTTP 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 404);
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

}


