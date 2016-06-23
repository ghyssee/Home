package be.home.domain.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by ghyssee on 23/06/2016.
 */
public class MP3Helper {

    public String prettifySong(String text){
        String prettifiedText = text;
        if (StringUtils.isNotBlank(text)){
            char[] tmp = {'('};
            prettifiedText = WordUtils.capitalizeFully(prettifiedText, new char[]{'(', ' ','.'});
            prettifiedText = prettifiedText.replace("(Radio Edit)", "");
            prettifiedText = prettifiedText.replace("(Vocal Radio Edit)", "");
            prettifiedText = prettifiedText.replace("(Vocal Radio Cut)", "");
            prettifiedText = prettifiedText.replace("(Radio Mix)", "");
            prettifiedText = prettifiedText.replace("(Video Version)", "");
            prettifiedText = prettifiedText.replace("(Single Cut)", "");
            prettifiedText = prettifiedText.replace("(Original Radio Cut)", "");
            prettifiedText = prettifiedText.replace("(Radio Version / Original)", "");
            prettifiedText = prettifiedText.replace("(Single Edit)", "");
            prettifiedText = prettifiedText.replace("(Original)", "");
            //prettifiedText = prettifiedText.replaceFirst("^Dj |dj ", "DJ ");
            //prettifiedText = prettifiedText.replaceAll(" Dj | dj \"", " DJ ");
            //prettifiedText = prettifiedText.replaceAll(" Mc \"", " MC ");
            prettifiedText = prettifiedText.replace("Fpi Project", "FPI Project");
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
                word = replaceWord(word, "Am", "AM");
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

}
