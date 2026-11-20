
package com.app.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.dto.StudentProfileRequestDTO;
import com.app.dto.StudentProfileResponseDTO;
import com.app.response.ResponseGenerator;
import com.app.response.TransactionContext;
import com.app.service.IStudentProfileService;

@RestController
@RequestMapping("/api/student/profile")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentProfileController {

    @Autowired
    private IStudentProfileService studentProfileService;

    @Autowired
    private ResponseGenerator responseGenerator;

    @GetMapping
    public ResponseEntity<?> getProfile(
            Principal principal,
            @RequestHeader HttpHeaders headers) {

        TransactionContext context =
                responseGenerator.generateTransationContext(headers);

        if (principal == null) {
            return responseGenerator.errorResponse(
                    context,
                    "Unauthorized",
                    HttpStatus.UNAUTHORIZED
            );
        }

        StudentProfileResponseDTO response =
                studentProfileService.getProfile(
                        principal.getName());

        return responseGenerator.successResponse(
                context,
                response,
                HttpStatus.OK
        );
    }

    @PutMapping
    public ResponseEntity<?> saveProfile(
            @RequestBody StudentProfileRequestDTO request,
            Principal principal,
            @RequestHeader HttpHeaders headers) {

        TransactionContext context =
                responseGenerator.generateTransationContext(headers);

        if (principal == null) {
            return responseGenerator.errorResponse(
                    context,
                    "Unauthorized",
                    HttpStatus.UNAUTHORIZED
            );
        }

        StudentProfileResponseDTO response =
                studentProfileService.saveProfile(
                        principal.getName(),
                        request
                );

        return responseGenerator.successResponse(
                context,
                response,
                HttpStatus.OK
        );
    }
}
