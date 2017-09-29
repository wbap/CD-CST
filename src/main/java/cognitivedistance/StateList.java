package cognitivedistance;

import java.util.*;

public class StateList extends LinkedList {

    private Integer parentNodeID;
    private StateList parentList;

    StateList(LinkedList stateList, Integer parentNodeID,
                     StateList parentList) {
        super(stateList);
        this.parentNodeID = parentNodeID;
        this.parentList = parentList;
    }

    Integer getParentNodeID() {
        return parentNodeID;
    }

    StateList getParentList() {
        return parentList;
    }
}