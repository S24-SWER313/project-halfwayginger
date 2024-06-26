package edu.bethlehem.scinexus.UserLinks;

import edu.bethlehem.scinexus.User.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserLinks {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "linksTo")
    User linksTo;

    @ManyToOne
    @JoinColumn(name = "linksFrom")
    User linksFrom;

    @Default
    Boolean accepted = false;

    public UserLinks(User linksTo, User linksFrom) {
        this.linksTo = linksTo;
        this.linksFrom = linksFrom;
    }

}
