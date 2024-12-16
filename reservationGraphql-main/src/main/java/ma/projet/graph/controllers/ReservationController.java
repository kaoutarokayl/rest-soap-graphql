package ma.projet.graph.controllers;

import ma.projet.graph.entities.Reservation;
import ma.projet.graph.entities.ReservationRequest;
import ma.projet.graph.repositories.ReservationRepository;
import ma.projet.graph.entities.Client;
import ma.projet.graph.entities.Chambre;
import ma.projet.graph.repositories.ClientRepository;
import ma.projet.graph.repositories.ChambreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    // Récupérer toutes les réservations
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Récupérer une réservation par ID
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        return reservation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Créer une réservation
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest reservationRequest) {
        Optional<Client> clientOpt = clientRepository.findById(reservationRequest.getClientId());
        Optional<Chambre> chambreOpt = chambreRepository.findById(reservationRequest.getChambreId());

        if (!clientOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Client with id " + reservationRequest.getClientId() + " not found.");
        }

        if (!chambreOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chambre with id " + reservationRequest.getChambreId() + " not found.");
        }

        Reservation reservation = new Reservation();
        reservation.setDateDebut(reservationRequest.getDateDebut());
        reservation.setDateFin(reservationRequest.getDateFin());
        if (reservationRequest.getPreferences() != null) {
            reservation.setPreferences(reservationRequest.getPreferences());
        }
        reservation.setClient(clientOpt.get());
        reservation.setChambre(chambreOpt.get());

        Reservation savedReservation = reservationRepository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    // Mettre à jour une réservation
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody ReservationRequest reservationRequest) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setDateDebut(reservationRequest.getDateDebut());
            reservation.setDateFin(reservationRequest.getDateFin());
            if (reservationRequest.getPreferences() != null) {
                reservation.setPreferences(reservationRequest.getPreferences());
            }
            Reservation updatedReservation = reservationRepository.save(reservation);
            return ResponseEntity.ok(updatedReservation);
        }
        return ResponseEntity.notFound().build();
    }

    // Supprimer une réservation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
