package be.home.domain.model.reconciliation;

import java.util.List;

/**
 * Created by ghyssee on 22/03/2017.
 */
public class MatchAlgorithm extends Base {

    private String leftCard;
    private String rightCard;
    private String sequence;
    private String process;
    private List<MatchPredicate> matchPredicates;


    public MatchAlgorithm(String code, String description, String leftCard, String rightCard,
                          String sequence, String process, List<MatchPredicate> matchPredicates) {
        super(code, description);
        this.leftCard = leftCard;
        this.rightCard = rightCard;
        this.sequence = sequence;
        this.process = process;
        this.matchPredicates = matchPredicates;

    }

    public String getLeftCard() {
        return leftCard;
    }

    public void setLeftCard(String leftCard) {
        this.leftCard = leftCard;
    }

    public String getRightCard() {
        return rightCard;
    }

    public void setRightCard(String rightCard) {
        this.rightCard = rightCard;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public List<MatchPredicate> getMatchPredicates() {
        return matchPredicates;
    }

    public void setMatchPredicates(List<MatchPredicate> matchPredicates) {
        this.matchPredicates = matchPredicates;
    }

}
