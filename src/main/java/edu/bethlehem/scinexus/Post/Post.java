package edu.bethlehem.scinexus.Post;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import edu.bethlehem.scinexus.Journal.Journal;
import edu.bethlehem.scinexus.User.User;

import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@DiscriminatorValue("post")

public class Post extends Journal {
  // private @Id @GeneratedValue Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "reShare")
  @JdbcTypeCode(SqlTypes.JSON)
  private Journal reShare;

  public Post(String content, User publisher) {
    super(content, publisher);

  }

  public Post() {
  }
}