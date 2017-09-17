package be.home.domain.model;

import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.bo.ArtistBO;
import be.home.mezzmo.domain.bo.ArtistConfigBO;
import be.home.mezzmo.domain.bo.ArtistSongRelationshipBO;
import be.home.mezzmo.domain.model.json.ArtistSongRelationship;
import be.home.mezzmo.domain.model.json.Artists;
import be.home.mezzmo.domain.model.json.MultiArtistConfig;
import be.home.model.json.AlbumInfo;
import be.home.mezzmo.domain.model.json.MP3Prettifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 23/06/2016.
 */
public class MP3Helper {

    private static MP3Helper mp3Helper = new MP3Helper();
    private static MP3Prettifier mp3Prettifer;
    private static final Logger log = Logger.getLogger(MP3Helper.class);
    private static List<MP3Prettifier.Word> multiArtistNames;
    private static List<MP3Prettifier.Word> artistNames;
    private static List<MP3Prettifier.Word> globalArtistNames;

    private MP3Helper() {
        mp3Prettifer = (MP3Prettifier) JSONUtils.openJSONWithCode(Constants.JSON.MP3PRETTIFIER, MP3Prettifier.class);
        multiArtistNames = ArtistConfigBO.getInstance().construct();
        Collections.sort(mp3Prettifer.global.filenames, (a1, b1) -> a1.priority - b1.priority);
        Collections.sort(mp3Prettifer.artist.names, (a1, b1) -> a1.priority - b1.priority);
        Collections.sort(mp3Prettifer.artistSongExceptions.items, (a1, b1) -> b1.priority - a1.priority);
        artistNames = ArtistBO.getInstance().constructArtistPatterns(false);
        Collections.sort(artistNames, (a1, b1) -> a1.priority - b1.priority);
        globalArtistNames = ArtistBO.getInstance().constructArtistPatterns(true);
        Collections.sort(globalArtistNames, (a1, b1) -> a1.priority - b1.priority);

    }

    public static MP3Helper getInstance() {
        return mp3Helper;
    }

    private static char[] startChars = new char[]{'(', ' ','.', '-', '"', '['};
    private static final String OPEN_BRACKET = "[\\(|\\[]";
    private static final String CLOSE_BRACKET = "[\\)|\\]]";

    public MP3Prettifier getMp3Prettifier(){
        return this.mp3Prettifer;
    }

    public String removeDurationFromString(String text){
        String prettifiedText = text;
        prettifiedText = prettifiedText.replaceAll("\\[[0-9]{1,2}:[0-9][0-9]\\]$", "");
        prettifiedText = prettifiedText.replaceAll("\\([0-9]{1,2}:[0-9][0-9]\\)$", "");
        prettifiedText = prettifiedText.replaceAll("[0-9]{1,2}:[0-9][0-9]$", "");
        return prettifiedText.trim();
    }

    public String replaceSpecialCharacters(String text){
        text = text.replaceAll("’", "'");
        text = text.replaceAll("´", "'");
        text = text.replaceAll("‘", "'");
        text = text.replaceAll("`", "'");
        text = text.replaceAll("“", "\"");
        text = text.replaceAll("”", "\"");
        text = text.replaceAll("\\ufeff","");
        text = text.replaceAll("\\u0092", "");
        text = text.replaceAll("\\u0301", "");
        text = text.replaceAll("\u00A0", " ");
        text = text.replaceAll("&amp;? ?", "& ");
        text = text.replaceAll("''", "\"");
        text = text.replaceAll("…", "...");

        return text;
    }

    public String prettifyString(String text){
        String prettifiedText = replaceSpecialCharacters(text);
        if (StringUtils.isNotBlank(text)) {
            prettifiedText = WordUtils.capitalizeFully(prettifiedText, startChars);
        }
        return prettifiedText;
    }

    public String replaceString(String text, MP3Prettifier.Word word){
        String oldWord = word.oldWord;
        if (word.parenthesis) {
            oldWord = replaceBetweenBrackets(oldWord);
        }
        boolean exactMatch = word.exactMatch;
        String newWord = word.newWord;
        if (word.beginOfWord) {
            oldWord = "(^| |'|\")" + oldWord;
            newWord = "$1" + newWord;
            exactMatch = false;
        }
        if (word.endOfWord > 0){
            oldWord = oldWord + "(\\)| |,|$|'|\"|:|\\.)";
            newWord = newWord + "$" + word.endOfWord;
            exactMatch = false;
        }
        if (exactMatch){
            oldWord = "^" + oldWord + "$";
        }
        text = text.replaceAll(oldWord, newWord);
        return text;
    }

    public String checkRegExpDollar(String text, int offset){
       boolean found = false;
        int counter = 1;
        do {
            if (text.matches("(.*)\\$" + offset + "(.*)")){
               //text = text.replaceFirst("\\$" + counter++, "\\$" + ++offset);
               int occurence = counter == 1 ? 1 : 2;
               text = test(text, "\\$" + offset++, "\\$" + offset, occurence);
                counter++;
               found = true;
           }
            else {
                found = false;
            }
       }
       while (found);
        text = test(text, "\\$" + (counter-1), "\\$" + (counter-1), 1);
        return text;
    }

    public String test(String text, String searchText, String replaceText, int occurence){
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(searchText);
        Matcher m = p.matcher(text);
        int count = 1;
        while(m.find()) {
            if(count++ % occurence == 0) {
                m.appendReplacement(sb, replaceText);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public String replaceSentence(String text, MP3Prettifier.Word word){
        String oldWord = word.oldWord;
        String newWord = word.newWord;
        if (word.beginOfWord) {
            oldWord = "(^| )" + oldWord;
            newWord = "$1" + newWord;
        }
        if (word.endOfWord > 0){
            oldWord = oldWord + "( |,|$)";
            newWord = "$" + word.endOfWord;
        }
        text = text.replaceAll(oldWord, newWord);
        return text;
    }

    public String prettifySong(String text){
        String prettifiedText = prettifyString(text);
        if (StringUtils.isNotBlank(text)) {
            prettifiedText = checkWords(prettifiedText, Mp3Tag.TITLE);
            prettifiedText = prettifySentence(mp3Prettifer.global.sentences, prettifiedText, "Global Sentence");
            prettifiedText = prettifySentence(globalArtistNames, prettifiedText, "Global Artist Names");
            for (MP3Prettifier.Word wordObj : mp3Prettifer.song.replacements) {

                String oldText = prettifiedText;
                prettifiedText = replaceString(oldText, wordObj);
                if (!oldText.equals(prettifiedText)) {
                    logRule("Song Replacements", wordObj);
                }
                prettifiedText = stripSong(prettifiedText);
                prettifiedText = prettifiedText.replace("  ", " ");
                prettifiedText = prettifiedText.trim();
            }
        }
        return prettifiedText;
    }

    public String replaceBetweenBrackets(String text){
        return OPEN_BRACKET + text + CLOSE_BRACKET;

    }

    public String prettifyArtist(String text){
        if (text == null) return "";
        String prettifiedText = prettifyString(text);
        if (StringUtils.isNotBlank(text)) {
            prettifiedText = prettifiedText.replaceAll(replaceBetweenBrackets("Remix"), "");
            prettifiedText = prettifiedText.replaceAll(replaceBetweenBrackets("Black Box Radio Edit"), "");

            prettifiedText = stripSong(prettifiedText);
            prettifiedText = prettifiedText.replaceAll("  ", " ");
            prettifiedText = prettifiedText.trim();
            prettifiedText = checkWords(prettifiedText, Mp3Tag.ARTIST);
            prettifiedText = prettifySentence(mp3Prettifer.global.sentences, prettifiedText, "Global Sentence");
            prettifiedText = prettifySentence(mp3Prettifer.artist.names, prettifiedText, "Artist Names");
            prettifiedText = prettifySentence(artistNames, prettifiedText, "Artist Names Patterns");
            prettifiedText = prettifySentence(globalArtistNames, prettifiedText, "Global Artist Names Patterns");
            prettifiedText = prettifySentence(mp3Prettifer.artist.postprocess, prettifiedText, "Artist Post Processing");
            prettifiedText = checkArtistNames2(multiArtistNames, prettifiedText, "Multi Artist Names");
            /*
            for (MP3Prettifier.Word wordObj : mp3Prettifer.artist.names){
               //prettifiedText = prettifiedText.replaceAll(wordObj.oldWord, wordObj.newWord);
                String oldText = prettifiedText;
                //System.out.println(wordObj.oldWord);
                prettifiedText = replaceString(oldText, wordObj);
                if (!oldText.equals(prettifiedText)){
                    logRule("Artist Names", wordObj);
                }
            }*/
        }
        return prettifiedText;

    }
    private String checkArtistNames2(List<MP3Prettifier.Word> list, String text, String logMsg){
        if (ArtistConfigBO.getInstance().isArtist(text)){
            return text;
        }
        for (MP3Prettifier.Word wordObj : list){
            //Pattern pattern = Pattern.compile(wordObj.oldWord);
            try {
                if (text.matches(wordObj.oldWord)) {
                    String oldText = text;
                    text = replaceString(oldText, wordObj);
                    if (!oldText.equals(text)) {
                        logRule(logMsg, wordObj);
                    }
                    // exit the loop if a match is found
                    break;
                }
            }
            catch (Exception ex){
                System.out.println(ex.getStackTrace());
            }
        }
        return text;
    }

    private String prettifySentence(List<MP3Prettifier.Word> list, String text, String logMsg){
        for (MP3Prettifier.Word wordObj : list){
            String oldText = text;
            text = replaceString(oldText, wordObj);
            if (!oldText.equals(text)){
                logRule(logMsg, wordObj);
            }
        }
        return text;
    }

    private void logRule(String category, MP3Prettifier.Word word){
        log.info("Rule applied: Category: " + category +
                 " / Old Word: " + word.oldWord +
                 " / New Word: " + word.newWord +
                " / BeginOfWord: " + (word.beginOfWord ? "Yes" : "No") +
                " / EndOfWord Index: " + word.endOfWord);
    }

    private void logRule(String category, MP3Prettifier.ArtistSongExceptions.ArtistSong word){
        log.info("Rule applied: Category: " + category);
        log.info("Old Artist: " + word.oldArtist + " / New Artist: " + word.newArtist);
        log.info("Old Title: " + word.oldSong + " / New Title: " + word.newSong);
    }

    private void logRule(String category, ArtistSongRelationship.ArtistSongRelation word){
        log.info("Rule applied: Category: " + category);
        String newArtist = StringUtils.isNotBlank(word.newArtistId) ? "(ARTIST) " + word.newArtistId : "(MULTI) " + word.newMultiArtistId;
        log.info("Old Artist: " + "<LIST>" + " / New Artist: " + newArtist);
        log.info("Old Title: " + word.oldSong + " / New Title: " + word.newSong);
    }

    private enum Mp3Tag {
        ARTIST, TITLE, ALBUM
    }

    private String checkWords(String text, Mp3Tag tag) {
        String newText = "";
        if (StringUtils.isNotBlank(text)) {
            String words[] = text.split(" ");
            for (int i=0; i < words.length; i++){
                String prefix = "";
                String suffix = "";
                if (i > 0){
                    newText += " ";
                }
                String word = words[i];
                // check if word starts with a " or a (
                if (word.matches(("^(\"|\\()(.*)"))){
                    prefix = word.substring(0,1);
                    word = word.substring(1);
                }
                // check if word ends with a " or a )
                if (word.matches(".*(\\)|\"|,)$")){
                    suffix = word.substring(word.length()-1,word.length());
                    word = word.substring(0, word.length()-1);
                }

                for (MP3Prettifier.Word wordObj : mp3Prettifer.global.words){
                    String oldWord = word;
                    word = replaceWord(oldWord, wordObj);
                    if (!oldWord.equals(word)){
                        logRule("Global Words", wordObj);
                    }

                }

                switch (tag){
                    case ARTIST:
                        for (MP3Prettifier.Word wordObj : mp3Prettifer.artist.words){
                            String oldWord = word;
                            word = replaceWord(word, wordObj);
                            if (!oldWord.equals(word)){
                                logRule("Artist Words", wordObj);
                            }
                        }
                        break;
                    case TITLE:
                        break;
                }
                newText += prefix + word + suffix;
            }
        }
        else {
            newText = text;
        }
        return newText;
    }

    private String replaceWord(String word, MP3Prettifier.Word wordObj){
        String returnWord;
        if (wordObj.exactMatch){
            returnWord = replaceExactWord(word, wordObj.oldWord, wordObj.newWord);
        }
        else {
            returnWord = word.replaceAll(wordObj.oldWord, wordObj.newWord);
        }
        return returnWord;
    }

    private String replaceExactWord(String word, String oldword, String newWord){
        String returnWord = word;
        if (StringUtils.isNotBlank(returnWord)){
            if (returnWord.equals(oldword)){
                returnWord = newWord;
            }
        }
        return returnWord;

    }

    private String stripSong(String text){
        String prettifiedText = text;
        if (StringUtils.isNotBlank(prettifiedText)){
            if (prettifiedText.endsWith("*")){
                prettifiedText = prettifiedText.substring(0, prettifiedText.length()-1);
            }
            prettifiedText = prettifiedText.replaceAll("\\([0-9]\\)", "");
        }
        return prettifiedText;
    }

    public void checkTrack(AlbumInfo.Track track){
        // search for (Feat. xxx) or Feat. xxx or Feat xxx or (Feat xxx)

        String FEAT = ".*[\\(|\\[| ][Ff](ea)?t[.| ]";
        String CLOSE_FEAT = "(?!^Edit\\))\\)|\\]";
        if (!StringUtils.endsWith(track.title, "Edit)")) {
            checkTrackPattern(track, FEAT, CLOSE_FEAT);
        }
    }

    private void checkTrackPattern(AlbumInfo.Track track, String startPattern, String endPattern){
        Pattern pattern = Pattern.compile(startPattern + "(.*)"); // + endPattern);
        Matcher matcher = pattern.matcher(track.title);
        if (matcher.matches()) {
            String extraArtist = track.title.replaceAll(startPattern, "").replaceFirst(endPattern, "");
            extraArtist = prettifyArtist(prettifySong(extraArtist));
            track.artist += " Feat. " + extraArtist;
            Pattern p = Pattern.compile("(.*)" + startPattern );
            Matcher m = p.matcher(track.title);
            if (m.find()) {
                String s = m.group(1);
                track.title = s.trim();
            }
        }
    }

    public String prettifyAlbum(String album, String albumArtist){
        String prettifiedText = prettifySong(album);
        if (albumArtist != null) {
            prettifiedText = prettifySongArtist(albumArtist, prettifiedText);
        }
        /*
        if (StringUtils.isNotBlank(prettifiedText)) {
            prettifiedText = prettifyString(prettifiedText) ;
            prettifiedText = prettifiedText.replaceFirst("\\[[Ee]xplicit\\]", "");
            prettifiedText = checkWords(prettifiedText, Mp3Tag.ALBUM);
            prettifiedText = prettifiedText.replaceAll("R'n'b", "R'n'B");


            prettifiedText = prettifiedText.trim();
        }
        */
        return prettifiedText;
    }

    public String formatTrack(AlbumInfo.Config albumInfo, String track) {
        int trackSize = albumInfo.trackSize == 0 ? 2 : albumInfo.trackSize;
        return StringUtils.leftPad(track, trackSize, "0");

    }

    public String stripFilename(String filename) {
        String prettifiedText = filename;
        for (MP3Prettifier.Word wordObj : mp3Prettifer.global.filenames) {
            String oldText = prettifiedText;
            prettifiedText = prettifiedText.replaceAll(wordObj.oldWord, wordObj.newWord);
            prettifiedText = prettifiedText.trim();
            if (!oldText.equals(prettifiedText)) {
                logRule("Global Filenames", wordObj);
            }

        }
        return prettifiedText;
    }

    public class ArtistSongItem{
        private boolean matched = false;
        private String item;

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public boolean isMatched() {
            return matched;
        }

        public void enableMatched() {
            this.matched = true;
        }

    }

    private ArtistSongItem checkTitle(String song, String oldSong, String newSong, boolean exactMatchTitle, int index){
        String matchKey = oldSong + (exactMatchTitle ? "" : "(.*)");
        String newKey = newSong + (exactMatchTitle ? "": "$" + (index + 1));;
        String newFormattedSong = song;
        ArtistSongItem item = new ArtistSongItem();
        if (song.matches(matchKey)) {
            newFormattedSong = song.replaceAll(matchKey, newKey);
            item.enableMatched();
        }
        item.setItem(newFormattedSong);
        return item;
    }

    private ArtistSongItem checkArtist(String artist, String oldArtist, String newArtist, boolean exactMatchArtist){
        String matchKey = oldArtist + (exactMatchArtist ? "" : "(.*)");
        String newKey = newArtist;
        String newFormattedArtist = artist;
        ArtistSongItem item = new ArtistSongItem();
        if (artist.matches(matchKey)) {
            item.enableMatched();
            newFormattedArtist = artist.replaceAll(matchKey, newKey);
        }
        item.setItem(newFormattedArtist);
        return item;
    }

    private String getMatchKey(String key, boolean exact){
        String matchKey = key + (exact ? "" : "(.*)");
        return matchKey;
    }



    public String checkTitleArtistRelation(String artist, String song, String logRule, List<MP3Prettifier.ArtistSongExceptions.ArtistSong> listOfSongs){
        for (MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong : listOfSongs) {
            if (artist.matches(getMatchKey(artistSong.oldArtist, artistSong.exactMatchArtist))) {
                ArtistSongItem item = checkTitle(song, artistSong.oldSong, artistSong.newSong, artistSong.exactMatchTitle, artistSong.indexTitle);
                if (item.isMatched()) {
                    song = item.getItem();
                    logRule(logRule, artistSong);
                    break;
                }
            }
        }
        return song;

    }

    public String checkArtistTitleRelation(String artist, String song, String logRule, List<MP3Prettifier.ArtistSongExceptions.ArtistSong> listOfSongs){
        for (MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong : listOfSongs) {
            if (song.matches(getMatchKey(artistSong.oldSong, artistSong.exactMatchTitle))) {
                ArtistSongItem item = checkArtist(artist, artistSong.oldArtist, artistSong.newArtist, artistSong.exactMatchArtist);
                if (item.isMatched()) {
                    artist = item.getItem();
                    logRule(logRule, artistSong);
                    break;
                }
            }
        }
        return artist;
    }

    public boolean isModified(String oldVal, String newVal, String logMessage){
        boolean changed = !oldVal.equals(newVal);
        if (changed){
            log.info(logMessage);
        }
        return changed;
    }

    public String prettifySongArtist(String artist, String song){
        String prettifiedSong = checkTitleArtistRelation(artist, song, "Title Exception", mp3Prettifer.artistSongExceptions.items);
        if (isModified(song, prettifiedSong, "ArtistSong / Song / Level 1 => Updated")){
            return prettifiedSong;
        }
        prettifiedSong = checkArtistTitleExceptions3(artist, song, ARTIST_SONG_TYPE.SONG);
        if (isModified(song, prettifiedSong, "ArtistSong / Song / Level 2 => Updated")){
            return prettifiedSong;
        }
        return prettifiedSong;
    }


    public String prettifyArtistSong(String artist, String song){
        String prettifiedArtist = checkArtistTitleRelation(artist, song, "Artist Exception",  mp3Prettifer.artistSongExceptions.items);
        if (isModified(artist, prettifiedArtist, "ArtistSong / Artist / Level 1 => Updated")){
            return prettifiedArtist;
        }
        prettifiedArtist = checkArtistTitleExceptions3(artist, song, ARTIST_SONG_TYPE.ARTIST);
        if (isModified(artist, prettifiedArtist, "ArtistSong / Artist / Level 2 => Updated")){
            return prettifiedArtist;
        }
        return prettifiedArtist;
    }

    public enum ARTIST_SONG_TYPE {
        ARTIST, SONG
    }

    /*
     This Will lookup Artist Song Relations which starts with one of the artists from the list
     This is never an exact match. For exact match use oldArtistId or oldMultiArtistId
     */


    public String checkArtistTitleExceptions3(String artist, String song, ARTIST_SONG_TYPE type){
        String value = null;
        switch (type) {
            case ARTIST:
                value = artist;
                break;
            case SONG:
                value = song;
                break;
        }
        List <ArtistSongRelationship.ArtistSongRelation>  list = ArtistSongRelationshipBO.getInstance().getArtistSongRelationshipList();
        for (ArtistSongRelationship.ArtistSongRelation item : list) {
            if (ArtistSongRelationshipBO.getInstance().matchArtist(artist, item)){
                ArtistSongItem artistSongItem = null;
                artistSongItem = checkTitle(song, item.oldSong, item.newSong, item.exactMatchTitle, item.indexTitle);
                switch (type){
                    case ARTIST:
                        if (StringUtils.isNotBlank(item.newMultiArtistId)){
                            MultiArtistConfig.Item tmp = ArtistConfigBO.getInstance().getMultiArtist(item.newMultiArtistId);
                            MP3Prettifier.Word word = ArtistConfigBO.getInstance().constructItem(tmp);
                            artistSongItem.setItem(word.newWord);
                        }
                        else if (StringUtils.isNotBlank(item.newArtistId)){
                            Artists.Artist newArtistObj = ArtistBO.getInstance().getArtist(item.newArtistId);
                            artistSongItem.setItem(ArtistBO.getInstance().getStageName(newArtistObj));
                        }
                        break;
                    case SONG:
                        break;
                }
                if (artistSongItem != null && artistSongItem.isMatched()) {
                    value = artistSongItem.getItem();
                    logRule("Title ExceptionV3", item);
                    break;
                }
            }
        }
        return value;
    }

    public String[] splitArtist(String artist){
        String tmp = ArtistConfigBO.getInstance().constructSplitter();
        String[] artists = artist.split(tmp);
        return artists;
    }

}
