package ch.example.terminkalender;

import ch.example.terminkalender.model.Participant;
import ch.example.terminkalender.model.Reservation;
import ch.example.terminkalender.repository.ReservationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ReservationRepository reservationRepository;

    public DataInitializer(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) {
        // Testreservation 1
        Reservation res1 = new Reservation();
        res1.setDate(Date.valueOf(LocalDate.now().plusDays(1)));
        res1.setStartTime(Time.valueOf("10:00:00"));
        res1.setEndTime(Time.valueOf("11:00:00"));
        res1.setRoom(101);
        res1.setComment("Team Meeting");

        Participant p1 = new Participant();
        p1.setFirstName("Anna");
        p1.setLastName("Schmidt");
        p1.setReservation(res1);

        Participant p2 = new Participant();
        p2.setFirstName("Ben");
        p2.setLastName("Müller");
        p2.setReservation(res1);

        res1.getParticipants().add(p1);
        res1.getParticipants().add(p2);

        reservationRepository.save(res1);

        // Testreservation 2
        Reservation res2 = new Reservation();
        res2.setDate(Date.valueOf(LocalDate.now().plusDays(2)));
        res2.setStartTime(Time.valueOf("14:00:00"));
        res2.setEndTime(Time.valueOf("15:30:00"));
        res2.setRoom(102);
        res2.setComment("Projektbesprechung");

        Participant p3 = new Participant();
        p3.setFirstName("Clara");
        p3.setLastName("Weber");
        p3.setReservation(res2);

        res2.getParticipants().add(p3);

        reservationRepository.save(res2);
    }
}