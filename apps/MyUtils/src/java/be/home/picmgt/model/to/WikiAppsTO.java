package be.home.picmgt.model.to;

public class WikiAppsTO implements Comparable<WikiAppsTO> {

	private String title;
	private String description;
	private String page;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int compareTo(WikiAppsTO compareObject) {
		if (getPage() == null || getTitle() == null || compareObject.getPage() == null || compareObject.getTitle() == null){
			System.err.println("Nullpointer : " + getPage());
			System.err.println("Nullpointer : " + getTitle());
			System.err.println("Nullpointer : " + compareObject.getPage());
			System.err.println("Nullpointer : " + compareObject.getTitle());
		}
		int ret = (getPage().toUpperCase() + getTitle().toUpperCase()).compareTo((compareObject.getPage().toUpperCase()+compareObject.getTitle().toUpperCase()));
		if (ret > 0)
			return 1;
		else if (ret == 0)
			return 0;
		else
			return -1;
	}
}
