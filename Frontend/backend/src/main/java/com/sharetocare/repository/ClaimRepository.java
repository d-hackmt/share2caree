package com.sharetocare.repository;

import com.sharetocare.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByNgoId(Long ngoId);
    List<Claim> findByDonationId(Long donationId);
}
