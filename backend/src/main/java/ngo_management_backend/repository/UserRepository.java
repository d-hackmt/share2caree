package ngo_management_backend.repository;

import ngo_management_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ngo_management_backend.model.NGOStatus;
import ngo_management_backend.model.Role;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByRoleAndNgoStatus(Role role, NGOStatus ngoStatus);

    @Query(value = "SELECT * FROM users u WHERE u.role = 'NGO' AND u.ngo_status = 'APPROVED' " +
            "AND u.latitude IS NOT NULL AND u.longitude IS NOT NULL AND " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(u.latitude)) * " +
            "cos(radians(u.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(u.latitude)))) <= :distance",
            nativeQuery = true)
    List<User> findNearbyNGOs(@Param("latitude") Double latitude,
                              @Param("longitude") Double longitude,
                              @Param("distance") Double distance);
}