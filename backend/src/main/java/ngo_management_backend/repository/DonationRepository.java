package ngo_management_backend.repository;

import ngo_management_backend.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByDonorId(Long donorId);
    List<Donation> findByNgoId(Long ngoId);
    List<Donation> findByStatus(String status);
}
