package edu.bethlehem.scinexus.ResearchPaper;

import java.util.List;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.bethlehem.scinexus.Article.Article;

import edu.bethlehem.scinexus.Article.Article;

public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Long> {
    ResearchPaper findByIdAndPublisherId(Long id, Long userId);

    List<ResearchPaper> findByPublisherId(Long userId);
}
