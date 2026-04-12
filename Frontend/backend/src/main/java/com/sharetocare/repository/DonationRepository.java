package com.sharetocare.repository;

import com.sharetocare.model.Donation;
import com.sharetocare.model.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByStatus(DonationStatus status);
    List<Donation> findByDonorId(Long donorId);
}
