//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mpatric.mp3agic;

public class ID3v22TagExt extends AbstractID3v2TagExt {
    public static final String VERSION = "2.0";

    public ID3v22TagExt() {
        this.version = "2.0";
    }

    public ID3v22TagExt(byte[] var1) throws NoSuchTagException, UnsupportedTagException, InvalidDataException {
        super(var1);
    }

    public ID3v22TagExt(byte[] var1, boolean var2) throws NoSuchTagException, UnsupportedTagException, InvalidDataException {
        super(var1, var2);
    }

    protected void unpackFlags(byte[] var1) {
        this.unsynchronisation = BufferTools.checkBit(var1[5], 7);
        this.compression = BufferTools.checkBit(var1[5], 6);
    }

    protected void packFlags(byte[] var1, int var2) {
        var1[var2 + 5] = BufferTools.setBit(var1[var2 + 5], 7, this.unsynchronisation);
        var1[var2 + 5] = BufferTools.setBit(var1[var2 + 5], 6, this.compression);
    }
}
