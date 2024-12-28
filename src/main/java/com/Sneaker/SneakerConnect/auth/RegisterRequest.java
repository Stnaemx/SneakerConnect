package com.Sneaker.SneakerConnect.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "firstName is blank")
    private String firstName;

    @NotBlank(message = "lastName is blank")
    private String lastName;

    @NotBlank(message = "email is blank")
    private String email;

    @NotBlank(message = "password is blank")
    private String password;
}
