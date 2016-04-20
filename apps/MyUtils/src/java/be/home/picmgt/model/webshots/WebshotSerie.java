package be.home.picmgt.model.webshots;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ini4j.Profile.Section;

import be.home.common.constants.InitConstants;
import be.home.common.utils.FileUtils;
import be.home.common.utils.URLUtils;
import be.home.picmgt.model.to.WebshotPhotoTO;

public class WebshotSerie {

	private static final Logger log = Logger.getLogger(WebshotSerie.class);

	private String title = null;

	private String name = null;

	private String export = null;

	private String renameBat = null;

	public WebshotSerie(Section map, String export, String renameBat) {
		this.title = map.fetch(InitConstants.WEBSHOTS_TITLE);
		this.export = export.replace("%name%", map
				.fetch(InitConstants.WEBSHOTS_NAME));
		this.renameBat = renameBat.replace("%name%", map
				.fetch(InitConstants.WEBSHOTS_NAME));
	}

	public WebshotSerie(String title, String name, String export, String renameBat) {
		this.title = title;
		this.name = name;
		this.export = export.replace("%name%", name);
		this.renameBat = renameBat.replace("%name%", name);
	}

	public void begin() throws IOException {
		cleanup();
	}

	private void cleanup() throws IOException {
		File cleanup = new File(export);
		cleanup.delete();
		cleanup = new File(this.renameBat);
		cleanup.delete();
		PrintWriter renameBat = null;
		try {
			renameBat = new PrintWriter(new BufferedWriter(new FileWriter(
					this.renameBat, true)));
			renameBat.println("md \"" + title + "\"");
		} finally {
			if (renameBat != null) {
				renameBat.close();
			}
		}
	}

	private void validateParams() {

		// this.validateParam(InitConstants.WEBSHOTS_PREFIX);
		// this.validateParam(InitConstants.WEBSHOTS_SERIES);
	}

	public void readUrl(String url) throws IOException, FileNotFoundException {
		List<String> lines = URLUtils.dump2(url);
		List<WebshotPhotoTO> photos = new ArrayList<WebshotPhotoTO>();
		for (String line : lines) {
			if (line.trim().startsWith("<a href=\"/g/")) {
				WebshotPhotoTO wb = getInfoFromHtml(line);
				if (wb != null) {
					photos.add(wb);
					System.out.println(line);
				}
			}
		}

		PrintWriter outList = null;
		PrintWriter renameBat = null;
		try {
			outList = new PrintWriter(new BufferedWriter(new FileWriter(
					this.export, true)));
			renameBat = new PrintWriter(new BufferedWriter(new FileWriter(
					this.renameBat, true)));
			for (WebshotPhotoTO photo : photos) {
				System.out.println(photo.toString());
				outList.println(photo.getNewUrl());
				renameBat.println("move /-Y \""
						+ FileUtils.stripFilenameFromUrl(photo.getNewUrl())
						+ "\" \""
						+ title
						+ "\\"
						+ FileUtils.stripIllegalCharsFromFileName(photo
								.getAlt()) + ".jpg\"");
			}
		} finally {
			if (outList != null) {
				outList.close();
			}
			if (renameBat != null) {
				renameBat.close();
			}
		}
	}

	private WebshotPhotoTO getInfoFromHtml(String html) throws IOException {

		if (html == null) {
			return null;
		}
		int start = html.indexOf("/g/");
		int end = html.indexOf("><img id=");
		WebshotPhotoTO webshotPhoto = new WebshotPhotoTO();
		if (start > -1 && end > -1) {
			webshotPhoto.setUrl(html.substring(start, end - 1));
			String altString = "alt=\"&quot;";
			start = html.indexOf(altString);
			end = html.indexOf("&quot; &copy;", start);
			String alt = html.substring(start + altString.length(), end);
			alt = alt.replaceAll("&quot;", "");
			alt = alt.replaceAll("&copy;", "");
			webshotPhoto.setAlt(alt);
			List<String> lines = URLUtils
					.dump2("http://myxinh.com/webshots.php?url=http%3A//www.webshots.com"
							+ webshotPhoto.getUrl());
			webshotPhoto.setNewUrl(getWebshotsUrl(lines));
		} else {
			return null;
		}
		return webshotPhoto;

	}

	private String getWebshotsUrl(List<String> lines) {

		if (lines == null || lines.size() == 0) {
			return null;
		}
		String wbUrl = null;
		for (String line : lines) {
			if (line != null && line.trim().startsWith("http://webshots.com")) {
				wbUrl = line;
				break;
			}
		}
		return wbUrl;
	}

}
