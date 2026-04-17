package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.common.utils.StringUtils;
import be.home.domain.model.service.MP3Service;
import be.home.model.json.AlbumInfo;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;


import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumDiscogs extends SeleniumService {

    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumDiscogs.class);

    public static void main(String[] args) {

        SeleniumDiscogs instance = new SeleniumDiscogs();
        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        AlbumInfo.Config configAlbum = initConfigAlbum();

        WebDriver driver = initDriver();

        //driver.get("https://www.discogs.com/release/31149296-Various-Now-Thats-What-I-Call-A-Summer-Party");
        driver.get("https://www.discogs.com/release/33180777-Sabrina-Carpenter-Short-N-Sweet?srsltid=AfmBOooaKWRCIWLGdBVMhkWlwHHMnJbifeeBfrowOf_23BXQ5-z6bDZ0");

        getAlbumInfo(driver, configAlbum);
        getTracks(driver, configAlbum);
        printAlbumInfo(log, configAlbum);

        driver.quit();
        writeAlbumConfiguration(configAlbum);

    }

    public void getAlbumInfo(WebDriver driver, AlbumInfo.Config configAlbum) {

        try {

            WebElement element = driver.findElement(By.xpath("//h1[contains(@class,'title')]"));

            //JavascriptExecutor js = (JavascriptExecutor) driver;
            //js.executeScript("arguments[0].remove();", element);


            boolean albumArtistFound = checkAlbumArtist(configAlbum, element);
            String SPLITTER = "–";
            String albumInfo = element.getText();
            log.info("Splitter: " + SPLITTER);
            log.info("albumInfo: " + albumInfo);
            String[] result = albumInfo.split(SPLITTER);
            if (result.length ==  1){
                configAlbum.setCompilation(false);
                configAlbum.setAlbumArtist(MP3Service.VARIOUS);
            }
            else if (result.length > 1) {
                String album = result[1].trim();
                configAlbum.setAlbum(album);
                if (!albumArtistFound){
                    // album title starts with a hyphen
                    configAlbum.setCompilation(false);
                    configAlbum.setAlbumArtist(MP3Service.VARIOUS);
                }
            }
        } catch (NoSuchElementException ex) {
            throw new RuntimeException("Album info not found");
        }
    }


    public boolean checkAlbumArtist(AlbumInfo.Config configAlbum, WebElement element) {
        boolean found = false;
        try {
            WebElement elementAlbumArtist = element.findElement(By.xpath(".//a"));
            String albumArtist = elementAlbumArtist.getText().trim();
            if (albumArtist.equalsIgnoreCase("VARIOUS")) {
                configAlbum.setCompilation(true);
                configAlbum.setAlbumArtist(MP3Service.VARIOUS);
            } else {
                configAlbum.setCompilation(false);
                configAlbum.setAlbumArtist(albumArtist);
            }
            found = true;
        } catch (NoSuchElementException ex) {
            found = false;
        }
        return found;
    }

    public void getTracks(WebDriver driver, AlbumInfo.Config albumConfig) {

        List<WebElement> trackList = driver.findElements(By.xpath("//tr[@data-track-position]"));
        List<AlbumInfo.Track> tracks = albumConfig.getTracks();
        log.info("nr of tracks found: " + trackList.size());
        for (WebElement track : trackList) {
            AlbumInfo.Track trackRec = new AlbumInfo().new Track();
            if (trackRec != null) {
                updateTrack(track, albumConfig, trackRec);
                updateArtist(track, albumConfig, trackRec);
                updateTitle(track, trackRec);
                updateExtraArtists(track, trackRec);
                tracks.add(trackRec);
            }
        }
    }

    public enum SONG_TYPE {

        TRACK, ARTIST_TITLE, AUDIO, LENGTH_TRACK
    }

    public void updateTrack(WebElement songInfo, AlbumInfo.Config albumConfig, AlbumInfo.Track trackRec) {

        try {
            WebElement trackElement = songInfo.findElement(By.xpath(".//td[starts-with(@class,'trackPos')]"));
            String trackInfo = trackElement.getText();
            String trackSplitter = "-";
            if (trackInfo.contains(trackSplitter)) {
                String[] result = trackInfo.split(trackSplitter);
                if (result.length > 1) {
                    trackRec.setCd(result[0].trim());
                    trackRec.setTrack(result[1].trim());
                } else {
                    trackRec.setTrack(trackInfo);
                }
            }
            else {
                trackRec.setTrack(trackInfo);
            }
        }
        catch (NoSuchElementException ex){
            // no track info found
            albumConfig.setCurrentTrack(albumConfig.getCurrentTrack()+1);
            trackRec.setTrack(String.valueOf(albumConfig.getCurrentTrack()));
        }
    }

    public void updateArtist(WebElement songInfo, AlbumInfo.Config albumConfig, AlbumInfo.Track trackRec) {

        try {
            WebElement trackElement = songInfo.findElement(By.xpath(".//td[starts-with(@class,'trackTitleNoArtist_')]"));
            // a title found with no artist
            trackRec.setArtist(albumConfig.getAlbumArtist());

        }
        catch (NoSuchElementException ex){
            WebElement trackElement = songInfo.findElement(By.xpath(".//td[starts-with(@class,'artist_') or starts-with(@class,'tracklist_track_artists')]"));
            String artistInfo = trackElement.getText();
            if (StringUtils.isBlank(artistInfo)){
                // artist example: <td class="artist_VsG56"></td>. An album of an artist/group but with no trackTitleNoArtist class
                trackRec.setArtist(albumConfig.getAlbumArtist());
            }
            else {
                trackRec.setArtist(clearArtistTag(artistInfo));
            }
        }
    }

    public String clearArtistTag(String artist){
        artist = artist.replaceAll("–", "");
        artist = artist.replaceAll(" ?\\([0-9]{1,3}\\)", "");
        artist = artist.replaceAll("\\*$", ""); // remove * at end of string
        artist = artist.replaceAll("\\* ", " "); // replace *<space> with <space>
        artist = artist.replaceAll("\\*, ?", ", "); // replace *, with ,
        artist = artist.trim();
        return artist;
    }

    public void updateTitle(WebElement songInfo, AlbumInfo.Track trackRec) {

        //WebElement trackElement = songInfo.findElement(By.xpath(".//td[starts-with(@class,'trackTitle')]"));
        WebElement trackElement = songInfo.findElement(By.xpath(".//span[starts-with(@class,'trackTitle')] | .//td[starts-with(@class,'trackTitleNoArtist')]"));
        String artistInfo = trackElement.getText();
        trackRec.setTitle(artistInfo);
    }

    public void updateExtraArtists(WebElement songInfo, AlbumInfo.Track trackRec) {

        // ex. url: https://www.discogs.com/release/31149296-Various-Now-Thats-What-I-Call-A-Summer-Party
        try {
            WebElement trackElement = songInfo.findElement(By.xpath(".//div[contains(@class,'trackCredits')] | .//span[contains(class, 'tracklist_extra_artist_span')]"));
            // get all lines that contain track credits
            List<WebElement> extraArtistElements = trackElement.findElements(By.xpath(".//span[starts-with(@class, 'MuiTypography-root')]"));
            for (WebElement extraArtistElement : extraArtistElements){
                // split it up by getting all spans for current element
                List<WebElement> extraArtistItems = extraArtistElement.findElements(By.xpath("./span"));
                if (extraArtistItems.size() > 1) {
                    // first item is the type
                    log.info("Extra Information on song found...");
                    AlbumInfo.ExtraArtist extraArtistRec = new AlbumInfo().new ExtraArtist();
                    String extraArtist = null;
                    List<AlbumInfo.ExtraArtist> extraArtists = trackRec.getExtraArtists();
                    for (int i = 0; i < extraArtistItems.size(); i++) {
                        WebElement extraArtistItem = extraArtistItems.get(i);
                        String singleExtraArtist = clearArtistTag(extraArtistItem.getText().trim());
                        if (i == 0) {
                            extraArtistRec.setType(extraArtistItem.getText().trim());
                        } else if (i == 1) {
                            extraArtist = singleExtraArtist;
                        } else {
                            extraArtist += ", " + singleExtraArtist;
                        }
                    }
                    extraArtistRec.setExtraArtist(extraArtist);
                    extraArtists.add(extraArtistRec);
                }
            }
        }
        catch (NoSuchElementException ex){
            // no extra information found
        }

    }

}