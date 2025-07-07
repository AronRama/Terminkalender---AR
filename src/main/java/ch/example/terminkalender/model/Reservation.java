package ch.example.terminkalender.model;

import jakarta.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date date;

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "end_time", nullable = false)
    private Time endTime;

    @Column(nullable = false)
    private Integer room;

    @Column(nullable = false, length = 200)
    private String comment;

    @Column(name = "private_key", nullable = false, unique = true)
    private String privateKey;

    @Column(name = "public_key", nullable = false, unique = true)
    private String publicKey;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    // Konstruktor für Testdaten
    public Reservation() {
        this.privateKey = UUID.randomUUID().toString();
        this.publicKey = UUID.randomUUID().toString();
    }

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public Integer getRoom() { return room; }
    public void setRoom(Integer room) { this.room = room; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    public List<Participant> getParticipants() { return participants; }
    public void setParticipants(List<Participant> participants) { this.participants = participants; }
}