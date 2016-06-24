package be.home.main;

import com.mpatric.mp3agic.EncodedText;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.ID3v2TextFrameData;

/**
 * Created by ghyssee on 24/06/2016.
 */
public class ID3 extends ID3v24Tag {

    public String getTrack() {
        ID3v2TextFrameData var1 = this.extractTextFrameData("DISC");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setTrack(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            //this.addFrame(this.createFrame("DISC", var2.toBytes()), true);
        }

    }


}
