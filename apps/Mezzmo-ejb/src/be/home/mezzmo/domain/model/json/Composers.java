package be.home.mezzmo.domain.model.json;

import java.util.List;

public class Composers {


    public List<Composers.FramePattern> exclusionList;
    public List<Composers.FramePattern> cleanupList;

    public List<Composers.FramePattern> customTagList;

    public class FramePattern {
        public String id;
        public String pattern;
        public String frameId;
        public boolean contains;

        public FramePattern (String id, String frameId, String pattern){
            this.id = id;
            this.frameId = frameId;
            this.pattern = pattern;
        }

        public FramePattern (String id, String frameId, String pattern, boolean contains){
            this.id = id;
            this.frameId = frameId;
            this.pattern = pattern;
            this.contains = contains;
        }
        public String getFrameId() {
            return frameId;
        }

        public void setFrameId(String frameId) {
            this.frameId = frameId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

    }
}
