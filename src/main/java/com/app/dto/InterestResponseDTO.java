
package com.app.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterestResponseDTO {

    private UUID id;

    private String name;

    private String category;
}
