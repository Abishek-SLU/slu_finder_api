
package com.app.dto;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentInterestRequestDTO {

    private List<UUID> interestIds;
}
