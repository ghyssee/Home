package be.home.picmgt.model.wiki;

public class WikiHelper {
	
	public static final String LINE_BREAK = "<br/>";
	
	public static String getWikiPageTitle(String title){
		
		return "{{DISPLAYTITLE:" + title + "}}";
		
	}

}
