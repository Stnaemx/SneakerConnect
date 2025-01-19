package com.Sneaker.SneakerConnect.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUser {

    // user info
    @NotBlank(message = "firstName is blank")
    private String firstName;

    @NotBlank(message = "lastName is blank")
    private String lastName;

    @NotBlank(message = "email is blank")
    private String email;

    @NotBlank(message = "password is blank")
    private String password;
}
