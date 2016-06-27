package be.home.domain.model;

import be.home.model.AlbumInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 23/06/2016.
 */
public class MP3Helper {

    private static char[] startChars = new char[]{'(', ' ','.', '-', '"'};

    public String removeDurationFromString(String text){
        String prettifiedText = text;
        prettifiedText = prettifiedText.replaceAll("[0-9][0-9]:[0-9][0-9]$", "");
        prettifiedText = prettifiedText.replaceAll("\\([0-9][0-9]:[0-9][0-9]\\)$", "");
        return prettifiedText;
    }


    public String prettifySong(String text){
        String prettifiedText = text;
        if (StringUtils.isNotBlank(text)){
            char[] tmp = {'('};
            prettifiedText = WordUtils.capitalizeFully(prettifiedText, startChars);
            prettifiedText = prettifiedText.replace("(Album Version)", "");
            prettifiedText = prettifiedText.replace("(Radio Edit)", "");
            prettifiedText = prettifiedText.replace("(Radio Mix)", "");
            prettifiedText = prettifiedText.replace("(Vocal Radio Edit)", "");
            prettifiedText = prettifiedText.replace("(Vocal Radio Cut)", "");
            prettifiedText = prettifiedText.replace("(Video Edit)", "");
            prettifiedText = prettifiedText.replace("(Video Version)", "");
            prettifiedText = prettifiedText.replace("(Single Cut)", "");
            prettifiedText = prettifiedText.replace("(Single Edit)", "");
            prettifiedText = prettifiedText.replace("(Single Version)", "");
            prettifiedText = prettifiedText.replace("(Original Radio Cut)", "");
            prettifiedText = prettifiedText.replace("(Radio Version / Original)", "");
            prettifiedText = prettifiedText.replace("(Original)", "");
            prettifiedText = prettifiedText.replace("Fpi Project", "FPI Project");
            prettifiedText = prettifiedText.replaceAll("\\[[eE]xplicit\\]", "");
            prettifiedText = prettifiedText.replace("(Uk Radio Version)", "");
            prettifiedText = prettifiedText.replace("Tourist Lemc", "Tourist LeMC");
            prettifiedText = prettifiedText.replace("Bart Kaell", "Bart Kaëll");

            prettifiedText = stripSong(prettifiedText);
            prettifiedText = prettifiedText.replace("  ", " ");
            prettifiedText = prettifiedText.trim();
            prettifiedText = checkWords(prettifiedText);
        }
        return prettifiedText;
    }

    private String checkWords(String text){
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
                // check if word ends with a " or a (
                if (word.matches(".*(\\)|\")$")){
                    suffix = word.substring(word.length()-1,word.length());
                    word = word.substring(0, word.length()-1);
                }
                word = replaceWord(word, "Vs", "Vs.");
                word = replaceWord(word, "Ft", "Feat.");
                word = replaceWord(word, "Ft.", "Feat.");
                word = replaceWord(word, "Feat", "Feat.");
                word = replaceWord(word, "Dj", "DJ");
                word = replaceWord(word, "Ii", "II");
                word = replaceWord(word, "Pm", "PM");
                word = replaceWord(word, "Dcup", "DCup");
                word = replaceWord(word, "Dcup", "DCup");
                word = replaceWord(word, "Deville", "DeVille");
                word = replaceWord(word, "Pres.", "Presents");
                word = replaceWord(word, "Atb", "ATB");
                word = replaceWord(word, "Mc's", "MC's");
                word = replaceWord(word, "Mcs", "MC's");
                word = replaceWord(word, "Mc", "MC");
                word = replaceWord(word, "Sq-1", "SQ-1");
                word = replaceWord(word, "Dnce", "DNCE");
                word = replaceWord(word, "In-grid", "In-Grid");
                word = replaceWord(word, "Rns", "RNS");
                word = replaceWord(word, "P!Nk", "P!nk");
                word = replaceWord(word, "Tp4y", "TP4Y");
                word = replaceWord(word, "Omi", "OMI");
                word = replaceWord(word, "Tjr", "TJR");
                word = replaceWord(word, "Featuring", "Feat.");
                word = replaceWord(word, "Clmd", "CLMD");
                word = replaceWord(word, "Dna", "DNA");
                word = replaceWord(word, "3lw", "3LW");
                word = replaceWord(word, "Tlc", "TLC");
                word = replaceWord(word, "Lp", "LP");

                // 19eighty7 => 19Eighty7
                // (17) => remove



                newText += prefix + word + suffix;
            }
        }
        else {
            newText = text;
        }
        return newText;
    }

    private String replaceWord(String word, String oldword, String newWord){
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
        // [feat. Majid Jordan]";
        String FEAT = ".*\\[[Ff]eat.";
        Pattern pattern = Pattern.compile(FEAT + "(.*)\\]");
        Matcher matcher = pattern.matcher(track.title);
        if (matcher.matches()) {
            //System.out.println("match found: " + track.title);
            String extraArtist = track.title.replaceAll(FEAT, "").replaceFirst("]", "");
            //System.out.println("extraArtist = " + extraArtist);
            track.artist += " Feat." + extraArtist;
            Pattern p = Pattern.compile("(.*)" + FEAT );
            Matcher m = p.matcher(track.title);
            if (m.find()) {
                String s = m.group(1);
                //System.out.println("new title: **** " + s);
                track.title = s.trim();
            }
        }
    }

    public String prettifyAlbum(String title){
        String prettifiedText = title;
        if (StringUtils.isNotBlank(prettifiedText)) {
            prettifiedText = WordUtils.capitalizeFully(prettifiedText, startChars);
            prettifiedText = prettifiedText.replaceFirst("\\[[Ee]xplicit\\]", "");
            prettifiedText = prettifiedText.trim();
        }
        return prettifiedText;
    }

}
