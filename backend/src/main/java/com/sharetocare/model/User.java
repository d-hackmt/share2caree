package com.sharetocare.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(unique = true)
    private String email;
    
    private String mobile;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // For NGOs
    private String registrationId;
    private boolean isVerified = false;
}
