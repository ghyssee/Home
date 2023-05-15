package be.home.domain.model.service;

public class MP3FramePattern {

    private String frameId;
    private String pattern;

    public MP3FramePattern (String frameId, String pattern) {
        this.frameId = frameId;
        this.pattern = pattern;
    }

    public String getFrameId() {
        return frameId;
    }

    public String getPattern() {
        return pattern;
    }
}
