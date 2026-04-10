package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.domain.model.service.MP3Service;
import be.home.model.json.AlbumInfo;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;


import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumUltratopTracks extends SeleniumService {

    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumUltratopTracks.class);

    public static void main(String[] args) {

        SeleniumUltratopTracks instance = new SeleniumUltratopTracks();
        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        AlbumInfo.Config configAlbum = initConfigAlbum();

        WebDriver driver = initDriver();

        driver.get("https://www.ultratop.be/nl/compilation/890f4/MNM-Big-Hits-Best-Of-2025");

        getAlbumInfo(driver, configAlbum);
        getTracks(driver, configAlbum);
        printAlbumInfo(log, configAlbum);

        driver.quit();
        writeAlbumConfiguration(configAlbum);

    }

    public void getAlbumInfo(WebDriver driver, AlbumInfo.Config configAlbum) {
        WebElement element = driver.findElement(By.xpath("//div[starts-with(@class,'heading')]"));

        //JavascriptExecutor js = (JavascriptExecutor) driver;
        //js.executeScript("arguments[0].remove();", element);


        String splitter = null;
        if (isCompilation(driver)) {
            configAlbum.setCompilation(true);
            configAlbum.setAlbumArtist(MP3Service.VARIOUS);
            String albumInfo = element.getText().trim();
            configAlbum.setAlbum(albumInfo);
        }
        else {
            configAlbum.setCompilation(false);
            try {
                WebElement splitElement = element.findElement(By.xpath(".//span[@class='notmobile']"));
                splitter = splitElement.getText();
                String albumInfo = element.getText().trim();
                String[] result = albumInfo.split(splitter);
                if (result.length > 1){
                    // title contains album artist + album title
                    String albumArtist=result[0].trim();
                    // remove \n from string
                    albumArtist = albumArtist.replace("\n", "");
                    configAlbum.setAlbum(result[1].trim());
                    configAlbum.setAlbumArtist(albumArtist);
                }
                else {
                    // it's probably a compilation cd. albuminfo only contains album title
                    configAlbum.setAlbum(albumInfo);
                }
                log.info("Splitter: " + splitter);
            }
            catch (InvalidSelectorException ex){
                throw new RuntimeException("Album info not found");
            }
        }
    }


    public boolean isCompilation(WebDriver driver){
        WebElement element = driver.findElement(By.xpath("//div[starts-with(@class,'item_info')]"));
        boolean compilation = false;
            if (hasClass(element, "album")){
                compilation = false;
            }
            else if (hasClass(element, "compilatie")){
                compilation = true;
            }
            else {
                log.info("Unknown - consider it as Compilation");
                compilation = true;
            }
            return compilation;
        }

        public void getTracks(WebDriver driver, AlbumInfo.Config albumConfig){

            WebElement element = driver.findElement(By.xpath("//div[@class='tracklists']"));
            List<AlbumInfo.Track> tracks = albumConfig.getTracks();
            List<WebElement> trackList = element.findElements(By.xpath(".//div[contains(@style,'table-row')]"));
            log.info("nr of tracks found: " + trackList.size());
            for (WebElement track : trackList) {
                List<WebElement> trackInfo = track.findElements(By.xpath(".//div[contains(@style,'table-cell')]"));
                AlbumInfo.Track trackRec = getSongInfo(trackInfo, albumConfig);
                if (trackRec != null) {
                    tracks.add(trackRec);
                }
            }
        }

    public enum SONG_TYPE {

        TRACK, ARTIST_TITLE, AUDIO, LENGTH_TRACK
    }

        public AlbumInfo.Track getSongInfo(List<WebElement> trackInfo, AlbumInfo.Config albumConfig){
            // 1 = track
            // 2 = Artist - Title
            // 3 = Audio
            // 4 = Length of track

            AlbumInfo.Track trackRec = new AlbumInfo().new Track();

            if (trackInfo.size() == 4){
                trackRec.setTrack(trackInfo.get(SONG_TYPE.TRACK.ordinal()).getText().trim());
                getArtistTitle(trackInfo.get(SONG_TYPE.ARTIST_TITLE.ordinal()), trackRec, albumConfig.getAlbumArtist());
                if (albumConfig.total > 0){
                    trackRec.setCd(String.valueOf(albumConfig.total));
                }
            }
            else if (trackInfo.size() == 1){
                // check if table cell contains cd number
                String cdInfo = trackInfo.get(0).getText().trim();
                Pattern pattern = Pattern.compile("(CD|LP) ([0-9]{1,2}):", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(cdInfo);
                if (matcher.find()){
                    // 0 = whole matched expression
                    // 1 = first expression from round brackets (CD/LP)
                    // 2 = second expression from round brackets ([0-9]{1,2})
                    log.info("CD Tag Found: " + matcher.group(2));
                    albumConfig.setTotal(Integer.parseInt(matcher.group(2)));
                }
                return null;
            }
            else {
                return null;
            }
            return trackRec;
        }

        public void getArtistTitle(WebElement element, AlbumInfo.Track trackRec, String albumArtist){
           String artistTitle = element.getText();
            int asciiVal = 8211;
            String HYPHEN = new Character((char) asciiVal).toString();
            String[] items = artistTitle.split(HYPHEN);
            if (items.length == 2) {
                trackRec.setArtist(items[0].trim());
                trackRec.setTitle(items[1].trim());
            }
            else if (items.length == 1) {
                trackRec.setTitle(artistTitle);
                trackRec.setArtist(albumArtist);
            }
            else {
                // this should never occur / artist + title will not be filled in
            }
        }
}