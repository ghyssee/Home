package be.home.picmgt.model.to;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public class TarFileTO {
  private String fileName;
  private File file;
  private Long crc;
  private Long length;
  private String version;
  private String type;

  public TarFileTO(String fileName, File file, Long crc, Long length){
	  this.fileName = fileName;
	  this.file = file;
	  this.crc = crc;
	  this.length = length;
  }

  public String getFileName() {
	return fileName;
}

public void setFileName(String fileName) {
	this.fileName = fileName;
}

public File getFile() {
	return file;
}

public void setFile(File file) {
	this.file = file;
}

public Long getCrc() {
	return crc;
}

public void setCrc(Long crc) {
	this.crc = crc;
}

public Long getLength() {
	return length;
}

public void setLength(Long length) {
	this.length = length;
}

public void setVersion(String version) {
	this.version = version;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

public String getcheckSum(){
	return StringUtils.rightPad(getCrc() + " " + getLength(), 20, " ");
}

public String getVersion(){
	return this.version == null ? "???" : this.version;
}




}
