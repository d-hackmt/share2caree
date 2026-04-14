package ngo_management_backend.model.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String email;
    private String password;
}