
package com.app.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentProfileResponseDTO {

    private UUID userId;

    private String userName;

    private String fullName;

    private String email;

    private String bio;

    private String program;

    private String academicYear;

    private String major;

    private String profileImageUrl;
}
