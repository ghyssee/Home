//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mpatric.mp3agic;

public class ID3v2TagFactoryExt {
    public ID3v2TagFactoryExt() {
    }

    public static AbstractID3v2TagExt createTag(byte[] var0) throws NoSuchTagException, UnsupportedTagException, InvalidDataException {
        sanityCheckTag(var0);
        byte var1 = var0[3];
        switch(var1) {
            case 2:
                return createID3v22Tag(var0);
            case 3:
                return new ID3v23TagExt(var0);
            case 4:
                return new ID3v24TagExt(var0);
            default:
                throw new UnsupportedTagException("Tag version not supported");
        }
    }

    private static AbstractID3v2TagExt createID3v22Tag(byte[] var0) throws NoSuchTagException, UnsupportedTagException, InvalidDataException {
        ID3v22TagExt var1 = new ID3v22TagExt(var0);
        if(var1.getFrameSets().size() == 0) {
            var1 = new ID3v22TagExt(var0, true);
        }

        return var1;
    }

    public static void sanityCheckTag(byte[] var0) throws NoSuchTagException, UnsupportedTagException {
        if(var0.length < 10) {
            throw new NoSuchTagException("Buffer too short");
        } else if(!"ID3".equals(BufferTools.byteBufferToStringIgnoringEncodingIssues(var0, 0, "ID3".length()))) {
            throw new NoSuchTagException();
        } else {
            byte var1 = var0[3];
            if(var1 != 2 && var1 != 3 && var1 != 4) {
                byte var2 = var0[4];
                throw new UnsupportedTagException("Unsupported version 2." + var1 + "." + var2);
            }
        }
    }
}
