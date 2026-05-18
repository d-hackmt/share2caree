package ngo_management_backend.controller;

import ngo_management_backend.model.Donation;
import ngo_management_backend.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @PostMapping
    public ResponseEntity<Donation> addDonation(@RequestBody Donation donation) {
        Donation savedDonation = donationService.processNewDonation(donation);
        return ResponseEntity.ok(savedDonation);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Donation>> getAvailableDonations() {
        return ResponseEntity.ok(donationService.getAvailableDonations());
    }

    @PutMapping("/{id}/claim")
    public ResponseEntity<?> claimDonation(@PathVariable Long id, @RequestParam Long ngoId) {
        try {
            Donation claimedDonation = donationService.claimDonation(id, ngoId);
            return ResponseEntity.ok(claimedDonation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<Donation>> getDonationsByDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(donationService.getDonationsByDonor(donorId));
    }

    @GetMapping("/ngo/{ngoId}")
    public ResponseEntity<List<Donation>> getClaimsByNgo(@PathVariable Long ngoId) {
        return ResponseEntity.ok(donationService.getClaimsByNgo(ngoId));
    }

    @GetMapping
    public ResponseEntity<List<Donation>> getAllDonations() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }
}
