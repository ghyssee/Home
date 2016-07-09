//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mpatric.mp3agic;

public class ID3v24TagExt extends AbstractID3v2TagExt {

    public ID3v24TagExt() {
        this.version = "4.0";
    }

    public ID3v24TagExt(byte[] var1) throws NoSuchTagException, UnsupportedTagException, InvalidDataException {
        super(var1);
    }

    protected void unpackFlags(byte[] var1) {
        this.unsynchronisation = BufferTools.checkBit(var1[5], 7);
        this.extendedHeader = BufferTools.checkBit(var1[5], 6);
        this.experimental = BufferTools.checkBit(var1[5], 5);
        this.footer = BufferTools.checkBit(var1[5], 4);
    }

    @Override
    protected void packFlags(byte[] bytes, int i) {

    }
}
