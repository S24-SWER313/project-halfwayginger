package edu.bethlehem.scinexus.ResearchPaper;

import edu.bethlehem.scinexus.Error.GeneralException;
import org.springframework.http.HttpStatus;

public class ResearchPaperNotFoundException extends GeneralException {

  public ResearchPaperNotFoundException(long id, HttpStatus httpStatus) {
    super("Research Paper With Id : " + id + ", is Not Found", httpStatus);
  }

  public ResearchPaperNotFoundException(String message, HttpStatus httpStatus) {
    super(message, httpStatus);
  }

  public ResearchPaperNotFoundException(long id) {
    super("Research Paper With Id : " + id + ", is Not Found", HttpStatus.NOT_FOUND);
  }
}
