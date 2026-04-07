package com.sharetocare.controller;

import com.sharetocare.model.Claim;
import com.sharetocare.model.Donation;
import com.sharetocare.model.DonationStatus;
import com.sharetocare.repository.ClaimRepository;
import com.sharetocare.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
public class DonationController {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @PostMapping
    public ResponseEntity<Donation> createDonation(@RequestBody Donation donation) {
        return ResponseEntity.ok(donationRepository.save(donation));
    }

    @GetMapping
    public ResponseEntity<List<Donation>> getAllDonations() {
        return ResponseEntity.ok(donationRepository.findAll());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Donation>> getAvailableDonations() {
        return ResponseEntity.ok(donationRepository.findByStatus(DonationStatus.AVAILABLE));
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<Donation>> getDonationsByDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(donationRepository.findByDonorId(donorId));
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<?> claimDonation(@PathVariable Long id, @RequestParam Long ngoId) {
        Donation donation = donationRepository.findById(id).orElse(null);
        if (donation == null) return ResponseEntity.notFound().build();
        
        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            return ResponseEntity.badRequest().body("Donation not available");
        }

        donation.setStatus(DonationStatus.CLAIMED);
        donationRepository.save(donation);

        Claim claim = new Claim();
        claim.setDonationId(id);
        claim.setNgoId(ngoId);
        claimRepository.save(claim);

        return ResponseEntity.ok(claim);
    }
}
