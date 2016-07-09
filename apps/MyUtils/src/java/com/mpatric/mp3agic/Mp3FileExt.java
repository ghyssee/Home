package com.mpatric.mp3agic;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class Mp3FileExt extends FileWrapper {
        private static final int DEFAULT_BUFFER_LENGTH = 65536;
        private static final int MINIMUM_BUFFER_LENGTH = 40;
        private static final int XING_MARKER_OFFSET_1 = 13;
        private static final int XING_MARKER_OFFSET_2 = 21;
        private static final int XING_MARKER_OFFSET_3 = 36;
        protected int bufferLength;
        private int xingOffset;
        private int startOffset;
        private int endOffset;
        private int frameCount;
        private Map<Integer, MutableInteger> bitrates;
        private int xingBitrate;
        private double bitrate;
        private String channelMode;
        private String emphasis;
        private String layer;
        private String modeExtension;
        private int sampleRate;
        private boolean copyright;
        private boolean original;
        private String version;
        private ID3v1 id3v1Tag;
        private ID3v2 id3v2Tag;
        private byte[] customTag;
        private boolean scanFile;

        protected Mp3FileExt() {
            this.xingOffset = -1;
            this.startOffset = -1;
            this.endOffset = -1;
            this.frameCount = 0;
            this.bitrates = new HashMap();
            this.bitrate = 0.0D;
        }

        public Mp3FileExt(String var1) throws IOException, UnsupportedTagException, InvalidDataException {
            this(var1, 65536, true);
        }

        public Mp3FileExt(String var1, int var2) throws IOException, UnsupportedTagException, InvalidDataException {
            this(var1, var2, true);
        }

        public Mp3FileExt(String var1, boolean var2) throws IOException, UnsupportedTagException, InvalidDataException {
            this(var1, 65536, var2);
        }

        public Mp3FileExt(String var1, int var2, boolean var3) throws IOException, UnsupportedTagException, InvalidDataException {
            super(var1);
            this.xingOffset = -1;
            this.startOffset = -1;
            this.endOffset = -1;
            this.frameCount = 0;
            this.bitrates = new HashMap();
            this.bitrate = 0.0D;
            this.init(var2, var3);
        }

        public Mp3FileExt(File var1) throws IOException, UnsupportedTagException, InvalidDataException {
            this(var1, 65536, true);
        }

        public Mp3FileExt(File var1, int var2) throws IOException, UnsupportedTagException, InvalidDataException {
            this(var1, var2, true);
        }

        public Mp3FileExt(File var1, int var2, boolean var3) throws IOException, UnsupportedTagException, InvalidDataException {
            super(var1);
            this.xingOffset = -1;
            this.startOffset = -1;
            this.endOffset = -1;
            this.frameCount = 0;
            this.bitrates = new HashMap();
            this.bitrate = 0.0D;
            this.init(var2, var3);
        }

        private void init(int var1, boolean var2) throws IOException, UnsupportedTagException, InvalidDataException {
            if(var1 < 41) {
                throw new IllegalArgumentException("Buffer too small");
            } else {
                this.bufferLength = var1;
                this.scanFile = var2;
                RandomAccessFile var3 = new RandomAccessFile(this.file.getPath(), "r");

                try {
                    this.initId3v1Tag(var3);
                    this.scanFile(var3);
                    if(this.startOffset < 0) {
                        throw new InvalidDataException("No mpegs frames found");
                    }

                    this.initId3v2Tag(var3);
                    if(var2) {
                        this.initCustomTag(var3);
                    }
                } finally {
                    var3.close();
                }

            }
        }

        protected int preScanFile(RandomAccessFile var1) {
            byte[] var2 = new byte[10];

            try {
                var1.seek(0L);
                int var3 = var1.read(var2, 0, 10);
                if(var3 == 10) {
                    try {
                        ID3v2TagFactory.sanityCheckTag(var2);
                        return 10 + BufferTools.unpackSynchsafeInteger(var2[6], var2[7], var2[8], var2[9]);
                    } catch (NoSuchTagException var5) {
                        ;
                    } catch (UnsupportedTagException var6) {
                        ;
                    }
                }
            } catch (IOException var7) {
                ;
            }

            return 0;
        }

        private void scanFile(RandomAccessFile var1) throws IOException, InvalidDataException {
            byte[] var2 = new byte[this.bufferLength];
            int var3 = this.preScanFile(var1);
            var1.seek((long)var3);
            boolean var4 = false;
            int var5 = var3;

            while(true) {
                int var6;
                do {
                    if(var4) {
                        return;
                    }

                    var6 = var1.read(var2, 0, this.bufferLength);
                    if(var6 < this.bufferLength) {
                        var4 = true;
                    }
                } while(var6 < 40);

                try {
                    int var7 = 0;
                    if(this.startOffset < 0) {
                        var7 = this.scanBlockForStart(var2, var6, var3, var7);
                        if(this.startOffset >= 0 && !this.scanFile) {
                            return;
                        }

                        var5 = this.startOffset;
                    }

                    var7 = this.scanBlock(var2, var6, var3, var7);
                    var3 += var7;
                    var1.seek((long)var3);
                } catch (InvalidDataException var8) {
                    if(this.frameCount >= 2) {
                        return;
                    }

                    this.startOffset = -1;
                    this.xingOffset = -1;
                    this.frameCount = 0;
                    this.bitrates.clear();
                    var4 = false;
                    var3 = var5 + 1;
                    if(var3 == 0) {
                        throw new InvalidDataException("Valid start of mpeg frames not found", var8);
                    }

                    var1.seek((long)var3);
                }
            }
        }

        private int scanBlockForStart(byte[] var1, int var2, int var3, int var4) {
            while(var4 < var2 - 40) {
                if(var1[var4] == -1 && (var1[var4 + 1] & -32) == -32) {
                    try {
                        MpegFrame var5 = new MpegFrame(var1[var4], var1[var4 + 1], var1[var4 + 2], var1[var4 + 3]);
                        if(this.xingOffset >= 0 || !this.isXingFrame(var1, var4)) {
                            this.startOffset = var3 + var4;
                            this.channelMode = var5.getChannelMode();
                            this.emphasis = var5.getEmphasis();
                            this.layer = var5.getLayer();
                            this.modeExtension = var5.getModeExtension();
                            this.sampleRate = var5.getSampleRate();
                            this.version = var5.getVersion();
                            this.copyright = var5.isCopyright();
                            this.original = var5.isOriginal();
                            ++this.frameCount;
                            this.addBitrate(var5.getBitrate());
                            var4 += var5.getLengthInBytes();
                            return var4;
                        }

                        this.xingOffset = var3 + var4;
                        this.xingBitrate = var5.getBitrate();
                        var4 += var5.getLengthInBytes();
                    } catch (InvalidDataException var6) {
                        ++var4;
                    }
                } else {
                    ++var4;
                }
            }

            return var4;
        }

        private int scanBlock(byte[] var1, int var2, int var3, int var4) throws InvalidDataException {
            while(true) {
                if(var4 < var2 - 40) {
                    MpegFrame var5 = new MpegFrame(var1[var4], var1[var4 + 1], var1[var4 + 2], var1[var4 + 3]);
                    this.sanityCheckFrame(var5, var3 + var4);
                    int var6 = var3 + var4 + var5.getLengthInBytes() - 1;
                    if(var6 < this.maxEndOffset()) {
                        this.endOffset = var3 + var4 + var5.getLengthInBytes() - 1;
                        ++this.frameCount;
                        this.addBitrate(var5.getBitrate());
                        var4 += var5.getLengthInBytes();
                        continue;
                    }
                }

                return var4;
            }
        }

        private int maxEndOffset() {
            int var1 = (int)this.getLength();
            if(this.hasId3v1Tag()) {
                var1 -= 128;
            }

            return var1;
        }

        private boolean isXingFrame(byte[] var1, int var2) {
            if(var1.length >= var2 + 13 + 3) {
                if("Xing".equals(BufferTools.byteBufferToStringIgnoringEncodingIssues(var1, var2 + 13, 4))) {
                    return true;
                }

                if("Info".equals(BufferTools.byteBufferToStringIgnoringEncodingIssues(var1, var2 + 13, 4))) {
                    return true;
                }

                if(var1.length >= var2 + 21 + 3) {
                    if("Xing".equals(BufferTools.byteBufferToStringIgnoringEncodingIssues(var1, var2 + 21, 4))) {
                        return true;
                    }

                    if("Info".equals(BufferTools.byteBufferToStringIgnoringEncodingIssues(var1, var2 + 21, 4))) {
                        return true;
                    }

                    if(var1.length >= var2 + 36 + 3) {
                        if("Xing".equals(BufferTools.byteBufferToStringIgnoringEncodingIssues(var1, var2 + 36, 4))) {
                            return true;
                        }

                        if("Info".equals(BufferTools.byteBufferToStringIgnoringEncodingIssues(var1, var2 + 36, 4))) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        private void sanityCheckFrame(MpegFrame var1, int var2) throws InvalidDataException {
            if(this.sampleRate != var1.getSampleRate()) {
                throw new InvalidDataException("Inconsistent frame header");
            } else if(!this.layer.equals(var1.getLayer())) {
                throw new InvalidDataException("Inconsistent frame header");
            } else if(!this.version.equals(var1.getVersion())) {
                throw new InvalidDataException("Inconsistent frame header");
            } else if((long)(var2 + var1.getLengthInBytes()) > this.getLength()) {
                throw new InvalidDataException("Frame would extend beyond end of file");
            }
        }

        private void addBitrate(int var1) {
            Integer var2 = new Integer(var1);
            MutableInteger var3 = (MutableInteger)this.bitrates.get(var2);
            if(var3 != null) {
                var3.increment();
            } else {
                this.bitrates.put(var2, new MutableInteger(1));
            }

            this.bitrate = (this.bitrate * (double)(this.frameCount - 1) + (double)var1) / (double)this.frameCount;
        }

        private void initId3v1Tag(RandomAccessFile var1) throws IOException {
            byte[] var2 = new byte[128];
            var1.seek(this.getLength() - 128L);
            int var3 = var1.read(var2, 0, 128);
            if(var3 < 128) {
                throw new IOException("Not enough bytes read");
            } else {
                try {
                    this.id3v1Tag = new ID3v1Tag(var2);
                } catch (NoSuchTagException var5) {
                    this.id3v1Tag = null;
                }

            }
        }

        private void initId3v2Tag(RandomAccessFile var1) throws IOException, UnsupportedTagException, InvalidDataException {
            if(this.xingOffset != 0 && this.startOffset != 0) {
                int var2;
                if(this.hasXingFrame()) {
                    var2 = this.xingOffset;
                } else {
                    var2 = this.startOffset;
                }

                byte[] var3 = new byte[var2];
                var1.seek(0L);
                int var4 = var1.read(var3, 0, var2);
                if(var4 < var2) {
                    throw new IOException("Not enough bytes read");
                }

                try {
                    this.id3v2Tag = ID3v2TagFactoryExt.createTag(var3);
                } catch (NoSuchTagException var6) {
                    this.id3v2Tag = null;
                }
            } else {
                this.id3v2Tag = null;
            }

        }

        private void initCustomTag(RandomAccessFile var1) throws IOException {
            int var2 = (int)(this.getLength() - (long)(this.endOffset + 1));
            if(this.hasId3v1Tag()) {
                var2 -= 128;
            }

            if(var2 <= 0) {
                this.customTag = null;
            } else {
                this.customTag = new byte[var2];
                var1.seek((long)(this.endOffset + 1));
                int var3 = var1.read(this.customTag, 0, var2);
                if(var3 < var2) {
                    throw new IOException("Not enough bytes read");
                }
            }

        }

        public int getFrameCount() {
            return this.frameCount;
        }

        public int getStartOffset() {
            return this.startOffset;
        }

        public int getEndOffset() {
            return this.endOffset;
        }

        public long getLengthInMilliseconds() {
            double var1 = (double)(8 * (this.endOffset - this.startOffset));
            return (long)(var1 / this.bitrate + 0.5D);
        }

        public long getLengthInSeconds() {
            return (this.getLengthInMilliseconds() + 500L) / 1000L;
        }

        public boolean isVbr() {
            return this.bitrates.size() > 1;
        }

        public int getBitrate() {
            return (int)(this.bitrate + 0.5D);
        }

        public Map<Integer, MutableInteger> getBitrates() {
            return this.bitrates;
        }

        public String getChannelMode() {
            return this.channelMode;
        }

        public boolean isCopyright() {
            return this.copyright;
        }

        public String getEmphasis() {
            return this.emphasis;
        }

        public String getLayer() {
            return this.layer;
        }

        public String getModeExtension() {
            return this.modeExtension;
        }

        public boolean isOriginal() {
            return this.original;
        }

        public int getSampleRate() {
            return this.sampleRate;
        }

        public String getVersion() {
            return this.version;
        }

        public boolean hasXingFrame() {
            return this.xingOffset >= 0;
        }

        public int getXingOffset() {
            return this.xingOffset;
        }

        public int getXingBitrate() {
            return this.xingBitrate;
        }

        public boolean hasId3v1Tag() {
            return this.id3v1Tag != null;
        }

        public ID3v1 getId3v1Tag() {
            return this.id3v1Tag;
        }

        public void setId3v1Tag(ID3v1 var1) {
            this.id3v1Tag = var1;
        }

        public void removeId3v1Tag() {
            this.id3v1Tag = null;
        }

        public boolean hasId3v2Tag() {
            return this.id3v2Tag != null;
        }

        public ID3v2 getId3v2Tag() {
            return this.id3v2Tag;
        }

        public void setId3v2Tag(ID3v2 var1) {
            this.id3v2Tag = var1;
        }

        public void removeId3v2Tag() {
            this.id3v2Tag = null;
        }

        public boolean hasCustomTag() {
            return this.customTag != null;
        }

        public byte[] getCustomTag() {
            return this.customTag;
        }

        public void setCustomTag(byte[] var1) {
            this.customTag = var1;
        }

        public void removeCustomTag() {
            this.customTag = null;
        }

        public void save(String var1) throws IOException, NotSupportedException {
            if(this.file.compareTo(new File(var1)) == 0) {
                throw new IllegalArgumentException("Save filename same as source filename");
            } else {
                RandomAccessFile var2 = new RandomAccessFile(var1, "rw");

                try {
                    if(this.hasId3v2Tag()) {
                        var2.write(this.id3v2Tag.toBytes());
                    }

                    this.saveMpegFrames(var2);
                    if(this.hasCustomTag()) {
                        var2.write(this.customTag);
                    }

                    if(this.hasId3v1Tag()) {
                        var2.write(this.id3v1Tag.toBytes());
                    }
                } finally {
                    var2.close();
                }

            }
        }

        private void saveMpegFrames(RandomAccessFile var1) throws IOException {
            int var2 = this.xingOffset;
            if(var2 < 0) {
                var2 = this.startOffset;
            }

            if(var2 >= 0) {
                if(this.endOffset >= var2) {
                    RandomAccessFile var3 = new RandomAccessFile(this.file.getPath(), "r");
                    byte[] var4 = new byte[this.bufferLength];

                    try {
                        var3.seek((long)var2);

                        while(true) {
                            int var5 = var3.read(var4, 0, this.bufferLength);
                            if(var2 + var5 > this.endOffset) {
                                var1.write(var4, 0, this.endOffset - var2 + 1);
                                return;
                            }

                            var1.write(var4, 0, var5);
                            var2 += var5;
                        }
                    } finally {
                        var3.close();
                    }
                }
            }
        }

}
