package ch.example.terminkalender.repository;

import ch.example.terminkalender.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByPrivateKey(String privateKey);
    Optional<Reservation> findByPublicKey(String publicKey);
}