package edu.bethlehem.scinexus.Interaction;

import edu.bethlehem.scinexus.JPARepository.InteractionRepository;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.hateoas.*;
import org.springframework.web.bind.annotation.*;

import edu.bethlehem.scinexus.JPARepository.JournalRepository;
import edu.bethlehem.scinexus.JPARepository.OpinionRepository;
import edu.bethlehem.scinexus.JPARepository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/interactions")
public class InteractionController {

private final InteractionService service;

  @GetMapping("/{interactionId}")
 public ResponseEntity<EntityModel<Interaction>> one(@PathVariable Long interactionId) {
    // TBC
    return ResponseEntity.ok(service.findInteractionById(interactionId));
  }

  @GetMapping()
  public ResponseEntity<CollectionModel<EntityModel<Interaction>>> all() {
    // TBC
    return ResponseEntity.ok(service.findAllInteractions());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> updateInteractionPartially(@PathVariable(value = "id") Long interactionId,
      @Valid @RequestBody InteractionRequestDTO newInteraction) {
    return ResponseEntity.ok().body(service.updateInteraction(interactionId, newInteraction));
  }

  @DeleteMapping("/{id}")
  ResponseEntity<?> deleteInteraction(@PathVariable Long id, Authentication authentication) {
    service.deleteInteraction(id, authentication);
    return ResponseEntity.noContent().build();

  }

  @PostMapping("/opinion/{opinionId}")
  public ResponseEntity<?> addOpinionInteraction(
      @PathVariable(value = "opinionId") Long opinionId,
      @RequestBody InteractionRequestDTO interaction,
      Authentication authentication) {

    return ResponseEntity.ok().body(service.addOpinionInteraction(opinionId, interaction, authentication));
  }

  @PostMapping("/journal/{journalId}")
  public ResponseEntity<?> addJournalInteraction(
      @PathVariable(value = "journalId") Long journalId,
      @RequestBody InteractionRequestDTO interaction,
      Authentication authentication) {
    return ResponseEntity.ok(service.addJournalInteraction(journalId, interaction, authentication));
  }

}
