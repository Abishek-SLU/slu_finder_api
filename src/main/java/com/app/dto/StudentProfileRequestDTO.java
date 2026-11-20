
package com.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentProfileRequestDTO {

    private String bio;

    private String program;

    private String academicYear;

    private String major;

    private String profileImageUrl;
}
