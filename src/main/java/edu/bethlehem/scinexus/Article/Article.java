package edu.bethlehem.scinexus.Article;

import edu.bethlehem.scinexus.Journal.Journal;

import edu.bethlehem.scinexus.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@DiscriminatorValue("article")
public class Article extends Journal {

    private String subject;

    private String title;
    @Column(length = 5000)
    private String brief;

    public Article(String title, String content, String subject, User publisher) {
        super(content, publisher);
        this.title = title;
        this.subject = subject;

    }

    public Article() {
    }
}