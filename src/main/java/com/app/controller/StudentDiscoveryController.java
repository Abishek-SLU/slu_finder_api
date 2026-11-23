package com.app.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.dto.StudentDiscoveryDTO;
import com.app.response.ResponseGenerator;
import com.app.response.TransactionContext;
import com.app.service.IStudentDiscoveryService;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentDiscoveryController {

    @Autowired
    private IStudentDiscoveryService discoveryService;

    @Autowired
    private ResponseGenerator responseGenerator;

    @GetMapping("/discover")
    public ResponseEntity<?> discoverStudents(
            Principal principal,
            @RequestHeader HttpHeaders httpHeaders) {

        TransactionContext context =
            responseGenerator.generateTransationContext(
                httpHeaders
            );

        if (principal == null) {
            return responseGenerator.errorResponse(
                context,
                "Authentication required",
                HttpStatus.UNAUTHORIZED
            );
        }

        try {
            List<StudentDiscoveryDTO> students =
                discoveryService.discoverStudents(
                    principal.getName()
                );

            return responseGenerator.successResponse(
                context,
                students,
                HttpStatus.OK
            );

        } catch (IllegalArgumentException ex) {
            return responseGenerator.errorResponse(
                context,
                ex.getMessage(),
                HttpStatus.NOT_FOUND
            );
        }
    }
}