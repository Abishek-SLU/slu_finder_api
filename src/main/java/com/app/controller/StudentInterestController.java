
package com.app.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.dto.InterestResponseDTO;
import com.app.dto.StudentInterestRequestDTO;
import com.app.response.ResponseGenerator;
import com.app.response.TransactionContext;
import com.app.service.IStudentInterestService;


@RestController
@RequestMapping("/api/student/my-interests")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentInterestController {

    @Autowired
    private IStudentInterestService studentInterestService;

    @Autowired
    private ResponseGenerator responseGenerator;

    @GetMapping
    public ResponseEntity<?> getMyInterests(
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

        List<InterestResponseDTO> response =
                studentInterestService.getMyInterests(
                        principal.getName());

        return responseGenerator.successResponse(
                context,
                response,
                HttpStatus.OK
        );
    }

    @PutMapping
    public ResponseEntity<?> saveMyInterests(
            @RequestBody StudentInterestRequestDTO request,
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

        try {

            List<InterestResponseDTO> response =
                    studentInterestService.saveMyInterests(
                            principal.getName(),
                            request
                    );

            return responseGenerator.successResponse(
                    context,
                    response,
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException e) {

            return responseGenerator.errorResponse(
                    context,
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
