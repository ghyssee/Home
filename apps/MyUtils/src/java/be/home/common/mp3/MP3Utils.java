package be.home.common.mp3;

import be.home.domain.model.service.MP3Service;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by ghyssee on 16/12/2016.
 */
public class MP3Utils {


    public static boolean checkId3v2Tag(MP3Service mp3Service){
        if (mp3Service.getArtist() == null ||
                mp3Service.getTitle() == null ||
                mp3Service.getAlbumArtist() == null ||
                mp3Service.getAlbum() == null){
            return false;
        }
        return true;

    }

    public int convertRating (String rating) {
        int rat = 0;
        if (StringUtils.isNotBlank(rating)){
            rat = Integer.valueOf(rating);
        }
        return convertRating(rat);
    }

    public int convertRating (int rating){
        int stars = 0;
        if (rating >= 1 && rating < 63){
            stars = 1;
        }
        else if (rating >= 64 && rating < 128){
            stars = 2;
        }
        else if (rating >= 128 && rating < 196){
            stars = 3;
        }
        else if (rating >= 196 && rating < 255){
            stars = 4;
        }
        else if (rating == 255){
            stars = 5;
        }
        return stars;
    }
}
