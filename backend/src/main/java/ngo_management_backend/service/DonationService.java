package ngo_management_backend.service;

import ngo_management_backend.model.Donation;
import ngo_management_backend.model.NGOStatus;
import ngo_management_backend.model.User;
import ngo_management_backend.model.Role;
import ngo_management_backend.repository.DonationRepository;
import ngo_management_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public Donation processNewDonation(Donation donation) {
        Donation savedDonation = donationRepository.save(donation);

        List<User> ngosToNotify;
        boolean hasLocation = savedDonation.getLatitude() != null && savedDonation.getLongitude() != null;

        if (hasLocation) {
            ngosToNotify = userRepository.findNearbyNGOs(
                savedDonation.getLatitude(), savedDonation.getLongitude(), 5.0);
            System.out.println("Found " + ngosToNotify.size() + " approved NGOs within 5km.");
        } else {
            // No location provided — notify all approved NGOs
            ngosToNotify = userRepository.findByRoleAndNgoStatus(Role.NGO, NGOStatus.APPROVED);
            System.out.println("No location on donation — notifying all " + ngosToNotify.size() + " approved NGOs.");
        }

        for (User ngo : ngosToNotify) {
            String locationLine = hasLocation ? "" : "Location: Not specified (check pickup address)\n";
            String subject = "New Donation Available" + (hasLocation ? " Near You" : "") + " - Share2Care";
            String body = "Hello " + ngo.getName() + ",\n\n" +
                          "A new donation is available" + (hasLocation ? " near your location" : "") + "!\n\n" +
                          "Donation Details:\n" +
                          "Title: " + savedDonation.getTitle() + "\n" +
                          "Category: " + savedDonation.getCategory() + "\n" +
                          "Quantity: " + savedDonation.getQuantity() + "\n" +
                          "Condition: " + savedDonation.getItemCondition() + "\n" +
                          "Description: " + savedDonation.getDescription() + "\n" +
                          "Pickup Address: " + savedDonation.getPickupAddress() + "\n" +
                          locationLine +
                          "\nLogin to Share2Care to claim this donation.\n\n" +
                          "Thank you,\nShare2Care Team";
            emailService.sendEmail(ngo.getEmail(), subject, body);
        }

        return savedDonation;
    }

    public Donation claimDonation(Long donationId, Long ngoId) {
        Optional<Donation> optionalDonation = donationRepository.findById(donationId);
        Optional<User> optionalNgo = userRepository.findById(ngoId);

        if (optionalDonation.isEmpty() || optionalNgo.isEmpty()) {
            throw new RuntimeException("Donation or NGO not found");
        }

        Donation donation = optionalDonation.get();
        User ngo = optionalNgo.get();

        if (!"AVAILABLE".equals(donation.getStatus())) {
            throw new RuntimeException("This donation has already been claimed");
        }

        donation.setStatus("CLAIMED");
        donation.setNgoId(ngo.getId());
        Donation updatedDonation = donationRepository.save(donation);

        Optional<User> optionalDonor = userRepository.findById(donation.getDonorId());

        if (optionalDonor.isPresent()) {
            User donor = optionalDonor.get();

            String donationInfo = "\n\nDonation Details:\n" +
                                  "Title: " + donation.getTitle() + "\n" +
                                  "Category: " + donation.getCategory() + "\n" +
                                  "Quantity: " + donation.getQuantity() + "\n" +
                                  "Pickup Address: " + donation.getPickupAddress() + "\n\n" +
                                  "NGO Details:\n" +
                                  "Name: " + ngo.getName() + "\n" +
                                  "Email: " + ngo.getEmail() + "\n" +
                                  "Phone: " + (ngo.getPhone() != null ? ngo.getPhone() : "N/A") + "\n\n" +
                                  "Donor Details:\n" +
                                  "Name: " + donor.getName() + "\n" +
                                  "Email: " + donor.getEmail() + "\n" +
                                  "Phone: " + (donor.getPhone() != null ? donor.getPhone() : "N/A") + "\n\n" +
                                  "Thank you for being part of Share2Care!\nShare2Care Team";

            emailService.sendEmail(ngo.getEmail(),
                "Donation Claimed Successfully - Share2Care",
                "Hello " + ngo.getName() + ",\n\nYou have successfully claimed a donation." + donationInfo);

            emailService.sendEmail(donor.getEmail(),
                "Your Donation Has Been Claimed - Share2Care",
                "Hello " + donor.getName() + ",\n\nGreat news! Your donation has been claimed by an NGO." + donationInfo);

            List<User> admins = userRepository.findByRole(Role.ADMIN);
            for (User admin : admins) {
                emailService.sendEmail(admin.getEmail(),
                    "Donation Claim Alert - Share2Care",
                    "Hello Admin,\n\nA donation has been claimed on Share2Care." + donationInfo);
            }
        }

        return updatedDonation;
    }

    public List<Donation> getAvailableDonations() {
        return donationRepository.findByStatus("AVAILABLE");
    }

    public List<Donation> getDonationsByDonor(Long donorId) {
        return donationRepository.findByDonorId(donorId);
    }

    public List<Donation> getClaimsByNgo(Long ngoId) {
        return donationRepository.findByNgoId(ngoId);
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }
}
