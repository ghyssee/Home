//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mpatric.mp3agic;

public class ID3v23TagExt extends AbstractID3v2TagExt {
    public static final String VERSION = "3.0";

    public ID3v23TagExt() {
        this.version = "3.0";
    }

    public ID3v23TagExt(byte[] var1) throws NoSuchTagException, UnsupportedTagException, InvalidDataException {
        super(var1);
    }

    protected void unpackFlags(byte[] var1) {
        this.unsynchronisation = BufferTools.checkBit(var1[5], 7);
        this.extendedHeader = BufferTools.checkBit(var1[5], 6);
        this.experimental = BufferTools.checkBit(var1[5], 5);
    }

    protected void packFlags(byte[] var1, int var2) {
        var1[var2 + 5] = BufferTools.setBit(var1[var2 + 5], 7, this.unsynchronisation);
        var1[var2 + 5] = BufferTools.setBit(var1[var2 + 5], 6, this.extendedHeader);
        var1[var2 + 5] = BufferTools.setBit(var1[var2 + 5], 5, this.experimental);
    }
}
