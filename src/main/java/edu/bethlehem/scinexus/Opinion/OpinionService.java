package edu.bethlehem.scinexus.Opinion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import edu.bethlehem.scinexus.DatabaseLoading.DataLoader;
import edu.bethlehem.scinexus.Interaction.Interaction;
import edu.bethlehem.scinexus.Interaction.InteractionModelAssembler;
import edu.bethlehem.scinexus.JPARepository.JournalRepository;
import edu.bethlehem.scinexus.JPARepository.OpinionRepository;
import edu.bethlehem.scinexus.JPARepository.UserRepository;
import edu.bethlehem.scinexus.Journal.Journal;
import edu.bethlehem.scinexus.Journal.JournalNotFoundException;
import edu.bethlehem.scinexus.SecurityConfig.JwtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@Data
@RequiredArgsConstructor
public class OpinionService {

        private final JournalRepository journalRepository;
        private final OpinionRepository opinionRepository;
        private final JwtService jwtService;
        private final UserRepository userRepository;
        private final InteractionModelAssembler interactionAssembler;

        private final OpinionModelAssembler assembler;
        Logger logger = LoggerFactory.getLogger(DataLoader.class);

        public Opinion convertOpinionDtoToOpinionEntity(OpinionDTO opinionDTO, Authentication auth) {

                return Opinion.builder()
                                .content(opinionDTO.getContent())
                                .journal(journalRepository.findById(opinionDTO.getJournalId())
                                                .orElseThrow(() -> new JournalNotFoundException(
                                                                opinionDTO.getJournalId())))
                                .opinionOwner(jwtService.getUser(auth))
                                .build();

        }

        public EntityModel<Opinion> getOneOpinion(Long opinionId) {
                logger.trace("Finding Opinion by ID");
                Opinion opinion = opinionRepository.findById(opinionId)
                                .orElseThrow(() -> new OpinionNotFoundException(opinionId, HttpStatus.NOT_FOUND));
                return assembler.toModel(opinion);
        }

        public List<EntityModel<Opinion>> getAllOpinions() {
                logger.trace("Finding All Opinions");
                return opinionRepository.findAll().stream().map(assembler::toModel)
                                .collect(Collectors.toList());
        }

        public EntityModel<Opinion> postOpinion(OpinionDTO newOpinionDTO, Authentication auth) {
                logger.trace("Posting New Opinion");
                Journal journal = journalRepository.findById(newOpinionDTO.getJournalId())
                                .orElseThrow(() -> new JournalNotFoundException(newOpinionDTO.getJournalId()));
                journal.addOpinion();
                Opinion newOpinion = convertOpinionDtoToOpinionEntity(newOpinionDTO, auth);
                return assembler.toModel(opinionRepository.save(newOpinion));
        }

        public EntityModel<Opinion> postOpinionToOpinion(OpinionDTO newOpinionDTO, Authentication auth) {
                Opinion papaOpinion = opinionRepository.findById(newOpinionDTO.getJournalId())
                                .orElseThrow(() -> new OpinionNotFoundException(newOpinionDTO.getJournalId(),
                                                HttpStatus.NOT_FOUND));
                papaOpinion.getJournal().addOpinion();
                newOpinionDTO.setJournalId(papaOpinion.getJournal().getId());
                Opinion newOpinion = convertOpinionDtoToOpinionEntity(newOpinionDTO, auth);
                newOpinion.setPapaOpinion(papaOpinion);
                return assembler.toModel(opinionRepository.save(newOpinion));
        }

        public CollectionModel<EntityModel<Interaction>> getInteractions(Long opinionId) {
                Opinion opinion = opinionRepository.findById(
                                opinionId)
                                .orElseThrow(() -> new OpinionNotFoundException(opinionId, HttpStatus.NOT_FOUND));
                List<EntityModel<Interaction>> interactions = opinion.getInteractions()
                                .stream()
                                .map(interactionAssembler::toModel)
                                .collect(Collectors.toList());
                return CollectionModel.of(interactions);
        }

        public EntityModel<Opinion> updateOpinionPartially(Long opinionId, OpinionPatchDTO opinionPatchDTO) {
                logger.trace("Partially Updating Opinion");
                Opinion opinion = opinionRepository.findById(opinionId)
                                .orElseThrow(() -> new OpinionNotFoundException(opinionId, HttpStatus.NOT_FOUND));

                if (opinionPatchDTO.getContent() != null)
                        opinion.setContent(opinionPatchDTO.getContent());

                return assembler.toModel(opinionRepository.save(opinion));

        }

        public void deleteOpinion(Long id) {
                logger.trace("Deleting Opinion");
                // opinionRepository.deleteByPapaOpinionId(id);
                ArrayList<Opinion> subOpinions = new ArrayList<Opinion>();
                opinionRepository.findAll().stream()
                                .map(opinion -> {
                                        if (opinion.getPapaOpinion() != null
                                                        && opinion.getPapaOpinion().getId() == id) {
                                                subOpinions.add(opinion);
                                                logger.trace("subOpinion found and added to the list: "
                                                                + opinion.getId());
                                        }
                                        return opinion;
                                }).collect(Collectors.toList());
                subOpinions.forEach(subOpinion -> {
                        opinionRepository.delete(subOpinion);
                });
                logger.trace("Deleted SubOpinions of Opinion");
                Opinion opinion = opinionRepository.findById(id)
                                .orElseThrow(() -> new OpinionNotFoundException(id, HttpStatus.NOT_FOUND));
                logger.trace("Deleted go the opinion");
                opinion.getJournal().removeOpinion();
                opinionRepository.delete(opinion);

        }

}
