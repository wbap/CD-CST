package cognitivedistance;

import java.util.*;

public class StateHistory extends LinkedList {

    private int maxCDLngth;
    StateHistory(int maxCDLngth) {
        this.maxCDLngth = maxCDLngth;
    }

    void add(Integer state) {
        super.add(state);
        if(size() > maxCDLngth) {
            removeFirst();
        }
    }
}