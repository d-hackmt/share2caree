package com.sharetocare.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long donationId;
    private Long ngoId;
    
    private LocalDateTime claimDate = LocalDateTime.now();
    
    // Status of the claim (e.g., REQUESTED, ACCEPTED, COLLECTED)
    private String status = "REQUESTED";
}
