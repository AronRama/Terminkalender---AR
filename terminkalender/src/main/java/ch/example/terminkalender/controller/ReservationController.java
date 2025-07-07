package ch.example.terminkalender.controller;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.example.terminkalender.model.Participant;
import ch.example.terminkalender.model.Reservation;
import ch.example.terminkalender.repository.ReservationRepository;
import jakarta.transaction.Transactional;

@Controller
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    private final List<Integer> rooms = Arrays.asList(101, 102, 201, 202);

    // Startseite
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("key", "");
        return "index";
    }

    // Formular anzeigen
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("rooms", rooms);
        return "create";
    }

    // Formular absenden und Reservation speichern
    @PostMapping("/create")
    @Transactional
    public String createReservation(@RequestParam("date") String dateStr,
                                    @RequestParam("startTime") String startTimeStr,
                                    @RequestParam("endTime") String endTimeStr,
                                    @RequestParam("room") Integer room,
                                    @RequestParam("comment") String comment,
                                    @RequestParam("participants") String participantsStr,
                                    Model model) {
        try {
            Reservation reservation = new Reservation();

            // Validierung Datum in Zukunft
            Date date = Date.valueOf(dateStr);
            if (date.before(Date.valueOf(LocalDate.now()))) {
                model.addAttribute("error", "Datum muss in der Zukunft liegen.");
                model.addAttribute("rooms", rooms);
                return "create";
            }
            reservation.setDate(date);
            reservation.setStartTime(Time.valueOf(startTimeStr + ":00"));
            reservation.setEndTime(Time.valueOf(endTimeStr + ":00"));
            reservation.setRoom(room);
            reservation.setComment(comment);

            // Validierung Zeit
            if (reservation.getStartTime().after(reservation.getEndTime())) {
                model.addAttribute("error", "Endzeit muss nach Startzeit liegen.");
                model.addAttribute("rooms", rooms);
                return "create";
            }

            // Validierung Kommentarlänge
            if (comment.length() < 10 || comment.length() > 200) {
                model.addAttribute("error", "Bemerkung muss zwischen 10 und 200 Zeichen lang sein.");
                model.addAttribute("rooms", rooms);
                return "create";
            }

            // Raumverfügbarkeit prüfen
            List<Reservation> conflictingReservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getRoom().equals(room)
                            && r.getDate().equals(date)
                            && !(reservation.getEndTime().before(r.getStartTime()) || reservation.getStartTime().after(r.getEndTime())))
                    .toList();

            if (!conflictingReservations.isEmpty()) {
                model.addAttribute("error", "Raum ist bereits belegt.");
                model.addAttribute("rooms", rooms);
                return "create";
            }

            // Teilnehmer parsen und hinzufügen
            List<Participant> participants = new ArrayList<>();
            String[] parts = participantsStr.split(",");
            for (String part : parts) {
                String[] nameParts = part.trim().split(" ");
                String firstName = nameParts.length > 0 ? nameParts[0] : "";
                String lastName = nameParts.length > 1 ? nameParts[1] : "";
                Participant p = new Participant();
                p.setFirstName(firstName);
                p.setLastName(lastName);
                p.setReservation(reservation);
                participants.add(p);
            }
            reservation.setParticipants(participants);

            // Speichern
            System.out.println("Teilnehmer vor dem Speichern:");
for (Participant p : participants) {
    System.out.println("- " + p.getFirstName() + " " + p.getLastName());
}
System.out.println("Reservation wird gespeichert: " + reservation);

            reservationRepository.save(reservation);
            System.out.println("Reservation gespeichert: " + reservation.getId());

            return "redirect:/";  // Zur Startseite nach Speichern

        } catch (Exception e) {
            model.addAttribute("error", "Fehler beim Speichern der Reservation: " + e.getMessage());
            model.addAttribute("rooms", rooms);
            return "create";
        }
    }

    // Schlüssel prüfen
    @PostMapping("/check-key")
    public String checkKey(@RequestParam("key") String key, Model model) {
        var reservation = reservationRepository.findByPrivateKey(key);
        if (reservation.isPresent()) {
            model.addAttribute("reservation", reservation.get());
            model.addAttribute("rooms", rooms);
            return "edit";
        }
        reservation = reservationRepository.findByPublicKey(key);
        if (reservation.isPresent()) {
            model.addAttribute("reservation", reservation.get());
            return "view";
        }
        model.addAttribute("error", "Ungültiger Schlüssel.");
        model.addAttribute("key", key);
        return "index";
    }

    // Termin bearbeiten
    @PostMapping("/edit/{id}")
    public String updateReservation(@PathVariable Long id,
                                    @ModelAttribute Reservation updatedReservation,
                                    @RequestParam("participants") String participants,
                                    Model model) {
        var existingReservation = reservationRepository.findById(id);
        if (existingReservation.isEmpty()) {
            model.addAttribute("error", "Reservation nicht gefunden.");
            return "index";
        }

        Reservation reservation = existingReservation.get();
        reservation.setDate(updatedReservation.getDate());
        reservation.setStartTime(updatedReservation.getStartTime());
        reservation.setEndTime(updatedReservation.getEndTime());
        reservation.setRoom(updatedReservation.getRoom());
        reservation.setComment(updatedReservation.getComment());

        reservation.getParticipants().clear();
        String[] participantArray = participants.split(",");
        for (String participant : participantArray) {
            String[] names = participant.trim().split(" ");
            if (names.length == 2) {
                Participant p = new Participant();
                p.setFirstName(names[0]);
                p.setLastName(names[1]);
                p.setReservation(reservation);
                reservation.getParticipants().add(p);
            }
        }

        reservationRepository.save(reservation);
        model.addAttribute("reservation", reservation);
        return "confirmation";
    }

    // Termin löschen
    @PostMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id, Model model) {
        reservationRepository.deleteById(id);
        model.addAttribute("message", "Reservation gelöscht.");
        return "index";
    }

    // Alle Termine anzeigen
    @GetMapping("/list")
    public String listReservations(Model model) {
        List<Reservation> reservations = reservationRepository.findAll();
        model.addAttribute("reservations", reservations);
        return "list";
    }
}
