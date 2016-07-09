//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mpatric.mp3agic;

import com.mpatric.mp3agic.BufferTools;
import com.mpatric.mp3agic.EncodedText;
import com.mpatric.mp3agic.ID3v1Genres;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v2ChapterFrameData;
import com.mpatric.mp3agic.ID3v2ChapterTOCFrameData;
import com.mpatric.mp3agic.ID3v2CommentFrameData;
import com.mpatric.mp3agic.ID3v2Frame;
import com.mpatric.mp3agic.ID3v2FrameSet;
import com.mpatric.mp3agic.ID3v2ObseleteFrame;
import com.mpatric.mp3agic.ID3v2ObseletePictureFrameData;
import com.mpatric.mp3agic.ID3v2PictureFrameData;
import com.mpatric.mp3agic.ID3v2TagFactory;
import com.mpatric.mp3agic.ID3v2TextFrameData;
import com.mpatric.mp3agic.ID3v2UrlFrameData;
import com.mpatric.mp3agic.ID3v2WWWFrameData;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.NoSuchTagException;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractID3v2TagExt implements ID3v2 {
    public static final String ID_IMAGE = "APIC";
    public static final String ID_ENCODER = "TENC";
    public static final String ID_URL = "WXXX";
    public static final String ID_ARTIST_URL = "WOAR";
    public static final String ID_COMMERCIAL_URL = "WCOM";
    public static final String ID_COPYRIGHT_URL = "WCOP";
    public static final String ID_AUDIOFILE_URL = "WOAF";
    public static final String ID_AUDIOSOURCE_URL = "WOAS";
    public static final String ID_RADIOSTATION_URL = "WORS";
    public static final String ID_PAYMENT_URL = "WPAY";
    public static final String ID_PUBLISHER_URL = "WPUB";
    public static final String ID_COPYRIGHT = "TCOP";
    public static final String ID_ORIGINAL_ARTIST = "TOPE";
    public static final String ID_BPM = "TBPM";
    public static final String ID_COMPOSER = "TCOM";
    public static final String ID_PUBLISHER = "TPUB";
    public static final String ID_COMMENT = "COMM";
    public static final String ID_GENRE = "TCON";
    public static final String ID_YEAR = "TYER";
    public static final String ID_DATE = "TDAT";
    public static final String ID_ALBUM = "TALB";
    public static final String ID_TITLE = "TIT2";
    public static final String ID_KEY = "TKEY";
    public static final String ID_ARTIST = "TPE1";
    public static final String ID_ALBUM_ARTIST = "TPE2";
    public static final String ID_TRACK = "TRCK";
    public static final String ID_PART_OF_SET = "TPOS";
    public static final String ID_COMPILATION = "TCMP";
    public static final String ID_CHAPTER_TOC = "CTOC";
    public static final String ID_CHAPTER = "CHAP";
    public static final String ID_GROUPING = "TIT1";
    public static final String ID_IMAGE_OBSELETE = "PIC";
    public static final String ID_ENCODER_OBSELETE = "TEN";
    public static final String ID_URL_OBSELETE = "WXX";
    public static final String ID_COPYRIGHT_OBSELETE = "TCR";
    public static final String ID_ORIGINAL_ARTIST_OBSELETE = "TOA";
    public static final String ID_BPM_OBSELETE = "TBP";
    public static final String ID_COMPOSER_OBSELETE = "TCM";
    public static final String ID_PUBLISHER_OBSELETE = "TBP";
    public static final String ID_COMMENT_OBSELETE = "COM";
    public static final String ID_GENRE_OBSELETE = "TCO";
    public static final String ID_YEAR_OBSELETE = "TYE";
    public static final String ID_DATE_OBSELETE = "TDA";
    public static final String ID_ALBUM_OBSELETE = "TAL";
    public static final String ID_TITLE_OBSELETE = "TT2";
    public static final String ID_KEY_OBSELETE = "TKE";
    public static final String ID_ARTIST_OBSELETE = "TP1";
    public static final String ID_ALBUM_ARTIST_OBSELETE = "TP2";
    public static final String ID_TRACK_OBSELETE = "TRK";
    public static final String ID_PART_OF_SET_OBSELETE = "TPA";
    public static final String ID_COMPILATION_OBSELETE = "TCP";
    public static final String ID_GROUPING_OBSELETE = "TT1";
    protected static final String TAG = "ID3";
    protected static final String FOOTER_TAG = "3DI";
    protected static final int HEADER_LENGTH = 10;
    protected static final int FOOTER_LENGTH = 10;
    protected static final int MAJOR_VERSION_OFFSET = 3;
    protected static final int MINOR_VERSION_OFFSET = 4;
    protected static final int FLAGS_OFFSET = 5;
    protected static final int DATA_LENGTH_OFFSET = 6;
    protected static final int FOOTER_BIT = 4;
    protected static final int EXPERIMENTAL_BIT = 5;
    protected static final int EXTENDED_HEADER_BIT = 6;
    protected static final int COMPRESSION_BIT = 6;
    protected static final int UNSYNCHRONISATION_BIT = 7;
    protected static final int PADDING_LENGTH = 256;
    private static final String ITUNES_COMMENT_DESCRIPTION = "iTunNORM";
    protected boolean unsynchronisation;
    protected boolean extendedHeader;
    protected boolean experimental;
    protected boolean footer;
    protected boolean compression;
    protected boolean padding;
    protected String version;
    private int dataLength;
    private int extendedHeaderLength;
    private byte[] extendedHeaderData;
    private boolean obseleteFormat;
    private final Map<String, ID3v2FrameSet> frameSets;

    public AbstractID3v2TagExt() {
        this.unsynchronisation = false;
        this.extendedHeader = false;
        this.experimental = false;
        this.footer = false;
        this.compression = false;
        this.padding = false;
        this.version = null;
        this.dataLength = 0;
        this.obseleteFormat = false;
        this.frameSets = new TreeMap();
    }

    public AbstractID3v2TagExt(byte[] var1) throws NoSuchTagException, UnsupportedTagException, InvalidDataException {
        this(var1, false);
    }

    public AbstractID3v2TagExt(byte[] var1, boolean var2) throws NoSuchTagException, UnsupportedTagException, InvalidDataException {
        this.unsynchronisation = false;
        this.extendedHeader = false;
        this.experimental = false;
        this.footer = false;
        this.compression = false;
        this.padding = false;
        this.version = null;
        this.dataLength = 0;
        this.obseleteFormat = false;
        this.frameSets = new TreeMap();
        this.obseleteFormat = var2;
        this.unpackTag(var1);
    }

    private void unpackTag(byte[] var1) throws NoSuchTagException, UnsupportedTagException, InvalidDataException {
        ID3v2TagFactory.sanityCheckTag(var1);
        int var2 = this.unpackHeader(var1);

        try {
            if(this.extendedHeader) {
                var2 = this.unpackExtendedHeader(var1, var2);
            }

            int var3 = this.dataLength;
            if(this.footer) {
                var3 -= 10;
            }

            this.unpackFrames(var1, var2, var3);
            if(this.footer) {
                this.unpackFooter(var1, this.dataLength);
            }

        } catch (ArrayIndexOutOfBoundsException var4) {
            throw new InvalidDataException("Premature end of tag", var4);
        }
    }

    private int unpackHeader(byte[] var1) throws UnsupportedTagException, InvalidDataException {
        byte var2 = var1[3];
        byte var3 = var1[4];
        this.version = var2 + "." + var3;
        if(var2 != 2 && var2 != 3 && var2 != 4) {
            throw new UnsupportedTagException("Unsupported version " + this.version);
        } else {
            this.unpackFlags(var1);
            if((var1[5] & 15) != 0) {
                throw new UnsupportedTagException("Unrecognised bits in header");
            } else {
                this.dataLength = BufferTools.unpackSynchsafeInteger(var1[6], var1[7], var1[8], var1[9]);
                if(this.dataLength < 1) {
                    throw new InvalidDataException("Zero size tag");
                } else {
                    return 10;
                }
            }
        }
    }

    protected abstract void unpackFlags(byte[] var1);

    private int unpackExtendedHeader(byte[] var1, int var2) {
        this.extendedHeaderLength = BufferTools.unpackSynchsafeInteger(var1[var2], var1[var2 + 1], var1[var2 + 2], var1[var2 + 3]) + 4;
        this.extendedHeaderData = BufferTools.copyBuffer(var1, var2 + 4, this.extendedHeaderLength);
        return this.extendedHeaderLength;
    }

    protected int unpackFrames(byte[] var1, int var2, int var3) {
        int var4 = var2;

        while(var4 <= var3) {
            try {
                ID3v2Frame var5 = this.createFrame(var1, var4);
                this.addFrame(var5, false);
                var4 += var5.getLength();
            } catch (InvalidDataException var7) {
                break;
            }
        }

        return var4;
    }

    protected void addFrame(ID3v2Frame var1, boolean var2) {
        ID3v2FrameSet var3 = (ID3v2FrameSet)this.frameSets.get(var1.getId());
        if(var3 == null) {
            var3 = new ID3v2FrameSet(var1.getId());
            var3.addFrame(var1);
            this.frameSets.put(var1.getId(), var3);
        } else if(var2) {
            var3.clear();
            var3.addFrame(var1);
        } else {
            var3.addFrame(var1);
        }

    }

    protected ID3v2Frame createFrame(byte[] var1, int var2) throws InvalidDataException {
        return (ID3v2Frame)(this.obseleteFormat?new ID3v2ObseleteFrame(var1, var2):new ID3v2Frame(var1, var2));
    }

    protected ID3v2Frame createFrame(String var1, byte[] var2) {
        return (ID3v2Frame)(this.obseleteFormat?new ID3v2ObseleteFrame(var1, var2):new ID3v2Frame(var1, var2));
    }

    private int unpackFooter(byte[] var1, int var2) throws InvalidDataException {
        if(!"3DI".equals(BufferTools.byteBufferToStringIgnoringEncodingIssues(var1, var2, "3DI".length()))) {
            throw new InvalidDataException("Invalid footer");
        } else {
            return 10;
        }
    }

    public byte[] toBytes() throws NotSupportedException {
        byte[] var1 = new byte[this.getLength()];
        this.packTag(var1);
        return var1;
    }

    public void packTag(byte[] var1) throws NotSupportedException {
        int var2 = this.packHeader(var1, 0);
        if(this.extendedHeader) {
            var2 = this.packExtendedHeader(var1, var2);
        }

        this.packFrames(var1, var2);
        if(this.footer) {
            this.packFooter(var1, this.dataLength);
        }

    }

    private int packHeader(byte[] var1, int var2) {
        try {
            BufferToolsExt.stringIntoByteBuffer("ID3", 0, "ID3".length(), var1, var2);
        } catch (UnsupportedEncodingException var5) {
            ;
        }

        String[] var3 = this.version.split("\\.");
        byte var4;
        if(var3.length > 0) {
            var4 = Byte.parseByte(var3[0]);
            var1[var2 + 3] = var4;
        }

        if(var3.length > 1) {
            var4 = Byte.parseByte(var3[1]);
            var1[var2 + 4] = var4;
        }

        this.packFlags(var1, var2);
        BufferTools.packSynchsafeInteger(this.getDataLength(), var1, var2 + 6);
        return var2 + 10;
    }

    protected abstract void packFlags(byte[] var1, int var2);

    private int packExtendedHeader(byte[] var1, int var2) {
        BufferTools.packSynchsafeInteger(this.extendedHeaderLength, var1, var2);
        BufferTools.copyIntoByteBuffer(this.extendedHeaderData, 0, this.extendedHeaderData.length, var1, var2 + 4);
        return var2 + 4 + this.extendedHeaderData.length;
    }

    public int packFrames(byte[] var1, int var2) throws NotSupportedException {
        int var3 = this.packSpecifiedFrames(var1, var2, (String)null, "APIC");
        var3 = this.packSpecifiedFrames(var1, var3, "APIC", (String)null);
        return var3;
    }

    private int packSpecifiedFrames(byte[] var1, int var2, String var3, String var4) throws NotSupportedException {
        Iterator var5 = this.frameSets.values().iterator();

        while(true) {
            ID3v2FrameSet var6;
            do {
                do {
                    if(!var5.hasNext()) {
                        return var2;
                    }

                    var6 = (ID3v2FrameSet)var5.next();
                } while(var3 != null && !var3.equals(var6.getId()));
            } while(var4 != null && var4.equals(var6.getId()));

            Iterator var7 = var6.getFrames().iterator();

            while(var7.hasNext()) {
                ID3v2Frame var8 = (ID3v2Frame)var7.next();
                if(var8.getDataLength() > 0) {
                    byte[] var9 = var8.toBytes();
                    BufferTools.copyIntoByteBuffer(var9, 0, var9.length, var1, var2);
                    var2 += var9.length;
                }
            }
        }
    }

    private int packFooter(byte[] var1, int var2) {
        try {
            BufferTools.stringIntoByteBuffer("3DI", 0, "3DI".length(), var1, var2);
        } catch (UnsupportedEncodingException var5) {
            ;
        }

        String[] var3 = this.version.split("\\.");
        byte var4;
        if(var3.length > 0) {
            var4 = Byte.parseByte(var3[0]);
            var1[var2 + 3] = var4;
        }

        if(var3.length > 1) {
            var4 = Byte.parseByte(var3[1]);
            var1[var2 + 4] = var4;
        }

        this.packFlags(var1, var2);
        BufferTools.packSynchsafeInteger(this.getDataLength(), var1, var2 + 6);
        return var2 + 10;
    }

    private int calculateDataLength() {
        int var1 = 0;
        if(this.extendedHeader) {
            var1 += this.extendedHeaderLength;
        }

        if(this.footer) {
            var1 += 10;
        } else if(this.padding) {
            var1 += 256;
        }

        Iterator var2 = this.frameSets.values().iterator();

        while(var2.hasNext()) {
            ID3v2FrameSet var3 = (ID3v2FrameSet)var2.next();

            ID3v2Frame var5;
            for(Iterator var4 = var3.getFrames().iterator(); var4.hasNext(); var1 += var5.getLength()) {
                var5 = (ID3v2Frame)var4.next();
            }
        }

        return var1;
    }

    protected boolean useFrameUnsynchronisation() {
        return false;
    }

    public String getVersion() {
        return this.version;
    }

    protected void invalidateDataLength() {
        this.dataLength = 0;
    }

    public int getDataLength() {
        if(this.dataLength == 0) {
            this.dataLength = this.calculateDataLength();
        }

        return this.dataLength;
    }

    public int getLength() {
        return this.getDataLength() + 10;
    }

    public Map<String, ID3v2FrameSet> getFrameSets() {
        return this.frameSets;
    }

    public boolean getPadding() {
        return this.padding;
    }

    public void setPadding(boolean var1) {
        if(this.padding != var1) {
            this.invalidateDataLength();
            this.padding = var1;
        }

    }

    public boolean hasFooter() {
        return this.footer;
    }

    public void setFooter(boolean var1) {
        if(this.footer != var1) {
            this.invalidateDataLength();
            this.footer = var1;
        }

    }

    public boolean hasUnsynchronisation() {
        return this.unsynchronisation;
    }

    public void setUnsynchronisation(boolean var1) {
        if(this.unsynchronisation != var1) {
            this.invalidateDataLength();
            this.unsynchronisation = var1;
        }

    }

    public boolean getObseleteFormat() {
        return this.obseleteFormat;
    }

    public String getTrack() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TRK":"TRCK");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setTrack(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TRCK", var2.toBytes()), true);
        }

    }

    public String getPartOfSet() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TPA":"TPOS");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setPartOfSet(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TPOS", var2.toBytes()), true);
        }

    }

    public boolean isCompilation() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TCP":"TCMP");
        return var1 != null && var1.getText() != null?"1".equals(var1.getText().toString()):false;
    }

    public void setCompilation(boolean var1) {
        this.invalidateDataLength();
        ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1?"1":"0"));
        this.addFrame(this.createFrame("TCMP", var2.toBytes()), true);
    }

    public String getGrouping() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TT1":"TIT1");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setGrouping(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TIT1", var2.toBytes()), true);
        }

    }

    public String getArtist() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TP1":"TPE1");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setArtist(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TPE1", var2.toBytes()), true);
        }

    }

    public String getAlbumArtist() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TP2":"TPE2");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setAlbumArtist(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TPE2", var2.toBytes()), true);
        }

    }

    public String getTitle() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TT2":"TIT2");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setTitle(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TIT2", var2.toBytes()), true);
        }

    }

    public String getAlbum() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TAL":"TALB");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setAlbum(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TALB", var2.toBytes()), true);
        }

    }

    public String getYear() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TYE":"TYER");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setYear(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TYER", var2.toBytes()), true);
        }

    }

    public String getDate() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TDA":"TDAT");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setDate(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TDAT", var2.toBytes()), true);
        }

    }

    private int getGenre(String var1) {
        if(var1 != null && var1.length() > 0) {
            try {
                return this.extractGenreNumber(var1);
            } catch (NumberFormatException var4) {
                String var3 = this.extractGenreDescription(var1);
                return ID3v1Genres.matchGenreDescription(var3);
            }
        } else {
            return -1;
        }
    }

    public int getGenre() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TCO":"TCON");
        return var1 != null && var1.getText() != null?this.getGenre(var1.getText().toString()):-1;
    }

    public void setGenre(int var1) {
        if(var1 >= 0) {
            this.invalidateDataLength();
            String var2 = var1 < ID3v1Genres.GENRES.length?ID3v1Genres.GENRES[var1]:"";
            String var3 = "(" + Integer.toString(var1) + ")" + var2;
            ID3v2TextFrameData var4 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var3));
            this.addFrame(this.createFrame("TCON", var4.toBytes()), true);
        }

    }

    public int getBPM() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TBP":"TBPM");
        if(var1 != null && var1.getText() != null) {
            String var2 = var1.getText().toString();

            try {
                return Integer.parseInt(var2);
            } catch (NumberFormatException var4) {
                return (int)Float.parseFloat(var2.trim().replaceAll(",", "."));
            }
        } else {
            return -1;
        }
    }

    public void setBPM(int var1) {
        if(var1 >= 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(Integer.toString(var1)));
            this.addFrame(this.createFrame("TBPM", var2.toBytes()), true);
        }

    }

    public String getKey() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TKE":"TKEY");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setKey(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TKEY", var2.toBytes()), true);
        }

    }

    public String getGenreDescription() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TCO":"TCON");
        if(var1 != null && var1.getText() != null) {
            String var2 = var1.getText().toString();
            if(var2 != null) {
                int var3 = this.getGenre(var2);
                if(var3 >= 0 && var3 < ID3v1Genres.GENRES.length) {
                    return ID3v1Genres.GENRES[var3];
                }

                String var4 = this.extractGenreDescription(var2);
                if(var4 != null && var4.length() > 0) {
                    return var4;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public void setGenreDescription(String var1) throws IllegalArgumentException {
        int var2 = ID3v1Genres.matchGenreDescription(var1);
        if(var2 < 0) {
            throw new IllegalArgumentException("Unknown genre: " + var1);
        } else {
            this.setGenre(var2);
        }
    }

    protected int extractGenreNumber(String var1) throws NumberFormatException {
        String var2 = var1.trim();
        if(var2.length() > 0 && var2.charAt(0) == 40) {
            int var3 = var2.indexOf(41);
            if(var3 > 0) {
                return Integer.parseInt(var2.substring(1, var3));
            }
        }

        return Integer.parseInt(var2);
    }

    protected String extractGenreDescription(String var1) throws NumberFormatException {
        String var2 = var1.trim();
        if(var2.length() > 0) {
            if(var2.charAt(0) == 40) {
                int var3 = var2.indexOf(41);
                if(var3 > 0) {
                    return var2.substring(var3 + 1);
                }
            }

            return var2;
        } else {
            return null;
        }
    }

    public String getComment() {
        ID3v2CommentFrameData var1 = this.extractCommentFrameData(this.obseleteFormat?"COM":"COMM", false);
        return var1 != null && var1.getComment() != null?var1.getComment().toString():null;
    }

    public void setComment(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2CommentFrameData var2 = new ID3v2CommentFrameData(this.useFrameUnsynchronisation(), "eng", (EncodedText)null, new EncodedText(var1));
            this.addFrame(this.createFrame("COMM", var2.toBytes()), true);
        }

    }

    public String getItunesComment() {
        ID3v2CommentFrameData var1 = this.extractCommentFrameData(this.obseleteFormat?"COM":"COMM", true);
        return var1 != null && var1.getComment() != null?var1.getComment().toString():null;
    }

    public void setItunesComment(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2CommentFrameData var2 = new ID3v2CommentFrameData(this.useFrameUnsynchronisation(), "eng", new EncodedText("iTunNORM"), new EncodedText(var1));
            this.addFrame(this.createFrame("COMM", var2.toBytes()), true);
        }

    }

    public String getComposer() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TCM":"TCOM");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setComposer(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TCOM", var2.toBytes()), true);
        }

    }

    public String getPublisher() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TBP":"TPUB");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setPublisher(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TPUB", var2.toBytes()), true);
        }

    }

    public String getOriginalArtist() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TOA":"TOPE");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setOriginalArtist(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TOPE", var2.toBytes()), true);
        }

    }

    public String getCopyright() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TCR":"TCOP");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setCopyright(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TCOP", var2.toBytes()), true);
        }

    }

    public String getArtistUrl() {
        ID3v2WWWFrameData var1 = this.extractWWWFrameData("WOAR");
        return var1 != null?var1.getUrl():null;
    }

    public void setArtistUrl(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2WWWFrameData var2 = new ID3v2WWWFrameData(this.useFrameUnsynchronisation(), var1);
            this.addFrame(this.createFrame("WOAR", var2.toBytes()), true);
        }

    }

    public String getCommercialUrl() {
        ID3v2WWWFrameData var1 = this.extractWWWFrameData("WCOM");
        return var1 != null?var1.getUrl():null;
    }

    public void setCommercialUrl(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2WWWFrameData var2 = new ID3v2WWWFrameData(this.useFrameUnsynchronisation(), var1);
            this.addFrame(this.createFrame("WCOM", var2.toBytes()), true);
        }

    }

    public String getCopyrightUrl() {
        ID3v2WWWFrameData var1 = this.extractWWWFrameData("WCOP");
        return var1 != null?var1.getUrl():null;
    }

    public void setCopyrightUrl(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2WWWFrameData var2 = new ID3v2WWWFrameData(this.useFrameUnsynchronisation(), var1);
            this.addFrame(this.createFrame("WCOP", var2.toBytes()), true);
        }

    }

    public String getAudiofileUrl() {
        ID3v2WWWFrameData var1 = this.extractWWWFrameData("WOAF");
        return var1 != null?var1.getUrl():null;
    }

    public void setAudiofileUrl(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2WWWFrameData var2 = new ID3v2WWWFrameData(this.useFrameUnsynchronisation(), var1);
            this.addFrame(this.createFrame("WOAF", var2.toBytes()), true);
        }

    }

    public String getAudioSourceUrl() {
        ID3v2WWWFrameData var1 = this.extractWWWFrameData("WOAS");
        return var1 != null?var1.getUrl():null;
    }

    public void setAudioSourceUrl(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2WWWFrameData var2 = new ID3v2WWWFrameData(this.useFrameUnsynchronisation(), var1);
            this.addFrame(this.createFrame("WOAS", var2.toBytes()), true);
        }

    }

    public String getRadiostationUrl() {
        ID3v2WWWFrameData var1 = this.extractWWWFrameData("WORS");
        return var1 != null?var1.getUrl():null;
    }

    public void setRadiostationUrl(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2WWWFrameData var2 = new ID3v2WWWFrameData(this.useFrameUnsynchronisation(), var1);
            this.addFrame(this.createFrame("WORS", var2.toBytes()), true);
        }

    }

    public String getPaymentUrl() {
        ID3v2WWWFrameData var1 = this.extractWWWFrameData("WPAY");
        return var1 != null?var1.getUrl():null;
    }

    public void setPaymentUrl(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2WWWFrameData var2 = new ID3v2WWWFrameData(this.useFrameUnsynchronisation(), var1);
            this.addFrame(this.createFrame("WPAY", var2.toBytes()), true);
        }

    }

    public String getPublisherUrl() {
        ID3v2WWWFrameData var1 = this.extractWWWFrameData("WPUB");
        return var1 != null?var1.getUrl():null;
    }

    public void setPublisherUrl(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2WWWFrameData var2 = new ID3v2WWWFrameData(this.useFrameUnsynchronisation(), var1);
            this.addFrame(this.createFrame("WPUB", var2.toBytes()), true);
        }

    }

    public String getUrl() {
        ID3v2UrlFrameData var1 = this.extractUrlFrameData(this.obseleteFormat?"WXX":"WXXX");
        return var1 != null?var1.getUrl():null;
    }

    public void setUrl(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2UrlFrameData var2 = new ID3v2UrlFrameData(this.useFrameUnsynchronisation(), (EncodedText)null, var1);
            this.addFrame(this.createFrame("WXXX", var2.toBytes()), true);
        }

    }

    public ArrayList<ID3v2ChapterFrameData> getChapters() {
        return this.obseleteFormat?null:this.extractChapterFrameData("CHAP");
    }

    public void setChapters(ArrayList<ID3v2ChapterFrameData> var1) {
        if(var1 != null) {
            this.invalidateDataLength();
            boolean var2 = true;
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
                ID3v2ChapterFrameData var4 = (ID3v2ChapterFrameData)var3.next();
                if(var2) {
                    var2 = false;
                    this.addFrame(this.createFrame("CHAP", var4.toBytes()), true);
                } else {
                    this.addFrame(this.createFrame("CHAP", var4.toBytes()), false);
                }
            }
        }

    }

    public ArrayList<ID3v2ChapterTOCFrameData> getChapterTOC() {
        return this.obseleteFormat?null:this.extractChapterTOCFrameData("CTOC");
    }

    public void setChapterTOC(ArrayList<ID3v2ChapterTOCFrameData> var1) {
        if(var1 != null) {
            this.invalidateDataLength();
            boolean var2 = true;
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
                ID3v2ChapterTOCFrameData var4 = (ID3v2ChapterTOCFrameData)var3.next();
                if(var2) {
                    var2 = false;
                    this.addFrame(this.createFrame("CTOC", var4.toBytes()), true);
                } else {
                    this.addFrame(this.createFrame("CTOC", var4.toBytes()), false);
                }
            }
        }

    }

    public String getEncoder() {
        ID3v2TextFrameData var1 = this.extractTextFrameData(this.obseleteFormat?"TEN":"TENC");
        return var1 != null && var1.getText() != null?var1.getText().toString():null;
    }

    public void setEncoder(String var1) {
        if(var1 != null && var1.length() > 0) {
            this.invalidateDataLength();
            ID3v2TextFrameData var2 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), new EncodedText(var1));
            this.addFrame(this.createFrame("TENC", var2.toBytes()), true);
        }

    }

    public byte[] getAlbumImage() {
        ID3v2PictureFrameData var1 = this.createPictureFrameData(this.obseleteFormat?"PIC":"APIC");
        return var1 != null?var1.getImageData():null;
    }

    public void setAlbumImage(byte[] var1, String var2) {
        if(var1 != null && var1.length > 0 && var2 != null && var2.length() > 0) {
            this.invalidateDataLength();
            ID3v2PictureFrameData var3 = new ID3v2PictureFrameData(this.useFrameUnsynchronisation(), var2, (byte)0, (EncodedText)null, var1);
            this.addFrame(this.createFrame("APIC", var3.toBytes()), true);
        }

    }

    public void clearAlbumImage() {
        this.clearFrameSet(this.obseleteFormat?"PIC":"APIC");
    }

    public String getAlbumImageMimeType() {
        ID3v2PictureFrameData var1 = this.createPictureFrameData(this.obseleteFormat?"PIC":"APIC");
        return var1 != null && var1.getMimeType() != null?var1.getMimeType():null;
    }

    public void clearFrameSet(String var1) {
        if(this.frameSets.remove(var1) != null) {
            this.invalidateDataLength();
        }

    }

    private ArrayList<ID3v2ChapterFrameData> extractChapterFrameData(String var1) {
        ID3v2FrameSet var2 = (ID3v2FrameSet)this.frameSets.get(var1);
        if(var2 == null) {
            return null;
        } else {
            ArrayList var3 = new ArrayList();
            List var4 = var2.getFrames();
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
                ID3v2Frame var6 = (ID3v2Frame)var5.next();

                try {
                    ID3v2ChapterFrameData var7 = new ID3v2ChapterFrameData(this.useFrameUnsynchronisation(), var6.getData());
                    var3.add(var7);
                } catch (InvalidDataException var9) {
                    ;
                }
            }

            return var3;
        }
    }

    private ArrayList<ID3v2ChapterTOCFrameData> extractChapterTOCFrameData(String var1) {
        ID3v2FrameSet var2 = (ID3v2FrameSet)this.frameSets.get(var1);
        if(var2 == null) {
            return null;
        } else {
            ArrayList var3 = new ArrayList();
            List var4 = var2.getFrames();
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
                ID3v2Frame var6 = (ID3v2Frame)var5.next();

                try {
                    ID3v2ChapterTOCFrameData var7 = new ID3v2ChapterTOCFrameData(this.useFrameUnsynchronisation(), var6.getData());
                    var3.add(var7);
                } catch (InvalidDataException var9) {
                    ;
                }
            }

            return var3;
        }
    }

    protected ID3v2TextFrameData extractTextFrameData(String var1) {
        ID3v2FrameSet var2 = (ID3v2FrameSet)this.frameSets.get(var1);
        if(var2 != null) {
            ID3v2Frame var3 = (ID3v2Frame)var2.getFrames().get(0);

            try {
                ID3v2TextFrameData var4 = new ID3v2TextFrameData(this.useFrameUnsynchronisation(), var3.getData());
                return var4;
            } catch (InvalidDataException var6) {
                ;
            }
        }

        return null;
    }

    private ID3v2WWWFrameData extractWWWFrameData(String var1) {
        ID3v2FrameSet var2 = (ID3v2FrameSet)this.frameSets.get(var1);
        if(var2 != null) {
            ID3v2Frame var3 = (ID3v2Frame)var2.getFrames().get(0);

            try {
                ID3v2WWWFrameData var4 = new ID3v2WWWFrameData(this.useFrameUnsynchronisation(), var3.getData());
                return var4;
            } catch (InvalidDataException var6) {
                ;
            }
        }

        return null;
    }

    private ID3v2UrlFrameData extractUrlFrameData(String var1) {
        ID3v2FrameSet var2 = (ID3v2FrameSet)this.frameSets.get(var1);
        if(var2 != null) {
            ID3v2Frame var3 = (ID3v2Frame)var2.getFrames().get(0);

            try {
                ID3v2UrlFrameData var4 = new ID3v2UrlFrameData(this.useFrameUnsynchronisation(), var3.getData());
                return var4;
            } catch (InvalidDataException var6) {
                ;
            }
        }

        return null;
    }

    private ID3v2CommentFrameData extractCommentFrameData(String var1, boolean var2) {
        ID3v2FrameSet var3 = (ID3v2FrameSet)this.frameSets.get(var1);
        if(var3 != null) {
            Iterator var4 = var3.getFrames().iterator();

            while(var4.hasNext()) {
                ID3v2Frame var5 = (ID3v2Frame)var4.next();

                try {
                    ID3v2CommentFrameData var6 = new ID3v2CommentFrameData(this.useFrameUnsynchronisation(), var5.getData());
                    if(var2 && "iTunNORM".equals(var6.getDescription().toString())) {
                        return var6;
                    }

                    if(!var2) {
                        return var6;
                    }
                } catch (InvalidDataException var8) {
                    ;
                }
            }
        }

        return null;
    }

    private ID3v2PictureFrameData createPictureFrameData(String var1) {
        ID3v2FrameSet var2 = (ID3v2FrameSet)this.frameSets.get(var1);
        if(var2 != null) {
            ID3v2Frame var3 = (ID3v2Frame)var2.getFrames().get(0);

            try {
                Object var4;
                if(this.obseleteFormat) {
                    var4 = new ID3v2ObseletePictureFrameData(this.useFrameUnsynchronisation(), var3.getData());
                } else {
                    var4 = new ID3v2PictureFrameData(this.useFrameUnsynchronisation(), var3.getData());
                }

                return (ID3v2PictureFrameData)var4;
            } catch (InvalidDataException var6) {
                ;
            }
        }

        return null;
    }

    public boolean equals(Object var1) {
        if(!(var1 instanceof AbstractID3v2Tag)) {
            return false;
        } else if(super.equals(var1)) {
            return true;
        } else {
            AbstractID3v2TagExt var2 = (AbstractID3v2TagExt)var1;
            if(this.unsynchronisation != var2.unsynchronisation) {
                return false;
            } else if(this.extendedHeader != var2.extendedHeader) {
                return false;
            } else if(this.experimental != var2.experimental) {
                return false;
            } else if(this.footer != var2.footer) {
                return false;
            } else if(this.compression != var2.compression) {
                return false;
            } else if(this.dataLength != var2.dataLength) {
                return false;
            } else if(this.extendedHeaderLength != var2.extendedHeaderLength) {
                return false;
            } else {
                if(this.version == null) {
                    if(var2.version != null) {
                        return false;
                    }
                } else {
                    if(var2.version == null) {
                        return false;
                    }

                    if(!this.version.equals(var2.version)) {
                        return false;
                    }
                }

                if(this.frameSets == null) {
                    if(var2.frameSets != null) {
                        return false;
                    }
                } else {
                    if(var2.frameSets == null) {
                        return false;
                    }

                    if(!this.frameSets.equals(var2.frameSets)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }
}
