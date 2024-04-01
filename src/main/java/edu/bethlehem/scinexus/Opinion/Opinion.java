package edu.bethlehem.scinexus.Opinion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import edu.bethlehem.scinexus.Interaction.Interaction;
import edu.bethlehem.scinexus.Journal.Journal;
import edu.bethlehem.scinexus.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
public class Opinion {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "The Opinion Content Can't Be Null")
    @NotBlank(message = "The Opinion Content Can't Be Empty")
    private String content;

    @Min(value = 0)
    private Integer interactionsCount;
    @Min(value = 0)
    private Integer opinionsCount;

    @ManyToOne(fetch = FetchType.LAZY) // Fetch Type Has been Changed from Lazy To Eager, Because When I request one
                                       // opinion there is an error, and this is how I solved it
    @JoinColumn(name = "journal", updatable = false)
    // @NotNull(message = "The Opinion Reference Journal Shouldn't Be Null")
    @JsonManagedReference
    private Journal journal;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "papaOpinion")
    @JdbcTypeCode(SqlTypes.JSON)
    private Opinion papaOpinion;

    // @OneToMany(fetch = FetchType.LAZY, mappedBy = "id", cascade =
    // CascadeType.ALL)
    // @JsonIgnore
    // private List<Opinion> opinions;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "opinion")
    private List<Interaction> interactions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opinionOwner")
    private User opinionOwner;

    public Opinion(String content, Journal journal, User opinionOwner) {
        this.interactionsCount = 0;
        this.opinionsCount = 0;
        this.content = content;
        this.journal = journal;
        this.opinionOwner = opinionOwner;
    }

    public Opinion() {
    }

    public void removeInteraction() {
        interactionsCount--;
    }

    public void addInteraction() {
        interactionsCount++;
    }

    public void removeOpinion() {
        opinionsCount--;
    }

    public void addOpinion() {
        opinionsCount++;
    }
}