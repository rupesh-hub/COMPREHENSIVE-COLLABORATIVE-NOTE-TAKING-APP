package com.ccnta.app.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import static com.ccnta.app.shared.CustomValidations.*;

@Getter
@Setter
@PasswordMatch(
        password = "password",
        confirmPassword = "confirmPassword",
        message = "Passwords must match"
)
public class RegistrationRequest {

    @ValidUsername(
            minLength = 4,
            maxLength = 20,
            message = "Invalid username!"
    )
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @StrongPassword(
            minLength = 8,
            maxLength = 64,
            message = "Password is too weak."
    )
    private String password;

    @NotBlank(message = "Confirm password is mandatory")
    private String confirmPassword;

}
