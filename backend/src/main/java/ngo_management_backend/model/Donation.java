package ngo_management_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long donorId;

    private Long ngoId;

    private String title;

    private String category;

    private String quantity;

    private String itemCondition;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String pickupAddress;

    private Double latitude;

    private Double longitude;

    private String status = "AVAILABLE";

    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
