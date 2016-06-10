package be.home.common.model;

/**
 * Created by ghyssee on 7/06/2016.
 */
public class TransferObject {

    private long index = 0;
    private long limit = 500;

    private boolean endOfList = false;

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public boolean isEndOfList() {
        return endOfList;
    }

    public void setEndOfList(boolean endOfList) {
        this.endOfList = endOfList;
    }

    public void addIndex(int number){
        this.index += number;
    }

    public void addIndex(){
        this.index += limit;
    }
}
