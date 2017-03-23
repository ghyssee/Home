package be.home.domain.model.reconciliation;

import java.util.List;

/**
 * Created by ghyssee on 22/03/2017.
 */
public class MatchEngine extends Base {

    private Stream leftStream;
    private Stream rightStream;
    private List<MatchAlgorithm> matchAlgorithms;


    public void setLeftStream(Stream leftStream) {
        this.leftStream = leftStream;
    }

    public MatchEngine(String code, String description, Stream leftStream, Stream rightStream, List<MatchAlgorithm> list) {
        super(code, description);
        this.leftStream = leftStream;
        this.rightStream = rightStream;
        this.matchAlgorithms = list;
    }
    public Stream getRightStream() {
        return rightStream;
    }

    public void setRightStream(Stream rightStream) {
        this.rightStream = rightStream;
    }

    public Stream getLeftStream() {
        return leftStream;
    }

    public List<MatchAlgorithm> getMatchAlgorithms() {
        return matchAlgorithms;
    }

    public void setMatchAlgorithms(List<MatchAlgorithm> matchAlgorithms) {
        this.matchAlgorithms = matchAlgorithms;
    }


}
