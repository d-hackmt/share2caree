package com.sharetocare.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Enumerated(EnumType.STRING)
    private DonationCategory category;
    
    private String quantity;
    private String itemCondition; // Using String to match "New", "Like New", etc.
    private String description;
    private String pickupAddress;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private DonationStatus status = DonationStatus.AVAILABLE;

    private Long donorId;
    
    private LocalDateTime createdAt = LocalDateTime.now();
}
