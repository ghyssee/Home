package be.home.picmgt.model.to;

public class WebshotPhotoTO {
	  private String alt = null;
	  private String url = null;
	  private String newUrl = null;

	  public String getAlt() {
		return alt;
	}
	public void setAlt(String alt) {
		this.alt = alt;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String toString(){
	
		String info = "Url = " + url + ", alt = " + alt + ", newUrl = " + newUrl;
		return info;
	}
	public String getNewUrl() {
		return newUrl;
	}
	public void setNewUrl(String newUrl) {
		this.newUrl = newUrl;
	}

}
