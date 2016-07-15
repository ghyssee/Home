package com.mpatric.mp3agic;

import com.mpatric.mp3agic.BufferTools;

import java.io.UnsupportedEncodingException;

/**
 * Created by Gebruiker on 8/07/2016.
 */
public class BufferToolsExt extends BufferTools {

    protected static final String defaultCharsetName = "UTF-8";

    public static void stringIntoByteBuffer(String var0, int var1, int var2, byte[] var3, int var4) throws UnsupportedEncodingException {
        stringIntoByteBuffer(var0, var1, var2, var3, var4, "UTF-8");
    }

    public static String byteBufferToStringIgnoringEncodingIssues(byte[] var0, int var1, int var2) {
        try {
            return byteBufferToString(var0, var1, var2, "UTF-8");
        } catch (UnsupportedEncodingException var4) {
            return null;
        }
    }
}
