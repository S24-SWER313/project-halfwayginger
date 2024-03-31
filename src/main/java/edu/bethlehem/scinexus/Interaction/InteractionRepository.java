package edu.bethlehem.scinexus.Interaction;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.bethlehem.scinexus.User.User;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    Interaction findByIdAndInteractorUser(Long interactionId, User interactioner);
}
