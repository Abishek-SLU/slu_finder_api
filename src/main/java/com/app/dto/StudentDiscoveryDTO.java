package com.app.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class StudentDiscoveryDTO {

    private String userId;
    private String userName;
    private String fullName;
    private String program;
    private String major;
    private String academicYear;
    private String bio;
    private String profileImageUrl;

    private int sharedInterestCount;

    private List<String> sharedInterests =
            new ArrayList<>();
}