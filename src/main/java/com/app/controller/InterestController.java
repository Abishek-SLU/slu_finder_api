
package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.dto.InterestResponseDTO;
import com.app.response.ResponseGenerator;
import com.app.response.TransactionContext;
import com.app.service.IInterestService;

@RestController
@RequestMapping("/api/student/interests")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InterestController {

    @Autowired
    private IInterestService interestService;

    @Autowired
    private ResponseGenerator responseGenerator;

    @GetMapping
    public ResponseEntity<?> getAllInterests(
            @RequestHeader HttpHeaders headers) {

        TransactionContext context =
                responseGenerator.generateTransationContext(headers);

        List<InterestResponseDTO> interests =
                interestService.getAllInterests();

        return responseGenerator.successResponse(
                context,
                interests,
                HttpStatus.OK
        );
    }
}
