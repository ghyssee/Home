package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.domain.model.service.MP3Service;
import be.home.model.json.AlbumInfo;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;


import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumUltratopNewSongs extends SeleniumService {

    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumUltratopNewSongs.class);

    public static void main(String[] args) {

        SeleniumUltratopNewSongs instance = new SeleniumUltratopNewSongs();
        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        AlbumInfo.Config configAlbum = initConfigAlbum();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uMMdd", Locale.ENGLISH);

        DateTimeFormatter yearF = DateTimeFormatter.ofPattern("u", Locale.ENGLISH);

        WebDriver driver = initDriver();

        LocalDate localDate = getLastSaturday();

        String url = localDate.format(yearF) + "/" + localDate.format(dtf);

        driver.get("https://www.ultratop.be/nl/ultratop50/" + url);

        getAlbumInfo(driver, configAlbum);
        getTracks(driver, configAlbum);
        //printAlbumInfo(log, configAlbum);

        driver.quit();
        //writeAlbumConfiguration(configAlbum);

    }

    public LocalDate getLastSaturday()
    {
        //LocalDate localDate = LocalDate.parse(fromDate, dtf);

        LocalDate localDate = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.SATURDAY));

        return localDate;

    }
    public void getAlbumInfo(WebDriver driver, AlbumInfo.Config configAlbum) {
        WebElement element = driver.findElement(By.xpath("//div[starts-with(@class,'heading')]"));

        String text = element.getText();
        // replace new line with space
        text = text.replaceAll("[\\t\\n\\r]+"," ");

        // get the selected date
        WebElement dateElement = element.findElement(By.xpath("//select[starts-with(@id,'chartdate')]"));
        Select select = new Select(dateElement);
        String strDate = select.getFirstSelectedOption().getText();
        configAlbum.setAlbum(text + " " + strDate);
        System.out.println(strDate);
    }


    public void getTracks(WebDriver driver, AlbumInfo.Config albumConfig){

        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='content chartitem']"));
        List<AlbumInfo.Track> tracks = albumConfig.getTracks();

        // find artist + song title
        for (WebElement element : elements){
            WebElement track = element.findElement(By.xpath(".//div[@class='chart_title']"));
        }



        //List<WebElement> trackList = element.findElements(By.xpath(".//div[[@class='chart_title']"));
        //log.info("nr of tracks found: " + trackList.size());
        //for (WebElement track : trackList) {
        //    List<WebElement> trackInfo = track.findElements(By.xpath(".//div[contains(@style,'table-cell')]"));
        //    AlbumInfo.Track trackRec = getSongInfo(trackInfo, albumConfig);
        //    if (trackRec != null) {
        //        tracks.add(trackRec);
        //    }
        //}
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