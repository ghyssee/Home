package be.home.picmgt.model.to;

import java.io.File;

public class ErrorFilesTO {
  private File file;
  private String errorMessage;

  public ErrorFilesTO(File file, String errorMessage){
    this.file = file;
    this.errorMessage = errorMessage;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public File getFile() {
    return file;
  }

  public String getErrorMessage() {
    return errorMessage;
  }


}
