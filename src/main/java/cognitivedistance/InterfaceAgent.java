package cognitivedistance;

import java.util.*;
import java.io.*;


public class InterfaceAgent {

    private LayeredAgent bottomLayeredAgent;
    private Hashtable stateToId;
    private Vector idToState;

    public InterfaceAgent(LayeredAgent bottomLayeredAgent) {
        this.bottomLayeredAgent = bottomLayeredAgent;
        stateToId = new Hashtable();
        idToState = new Vector();
    }

    private Integer getID(Vector state) {

        if(state == null) {
            return null;
        }

        return (Integer)stateToId.get(state);
    }

    private Vector getState(Integer id) {
        if((id == null) || ((idToState.size()) <= id)) {
            return null;
        }
        return (Vector)idToState.get(id);
    }

    private  Integer setNewNodeToBottom(Vector state) {
        if(state == null) {
            return null;
        }
        int newId = idToState.size();
        idToState.add(state);
        stateToId.put(state, newId);

        bottomLayeredAgent.createNewNode(null);
        return newId;
    }

    public void addNode(Vector currentState) {
        Integer currentNodeID = getID(currentState);
        if(currentNodeID == null) {
            currentNodeID = setNewNodeToBottom(currentState);
        }
        bottomLayeredAgent.addNode(currentNodeID);
    }

    public Vector searchSubGoal(Vector currentState, Vector goalState) {
        if(goalState == null) {
            return null;
        }

        Integer currentNodeID = getID(currentState);
        Vector newGoalState = getSameStates(goalState);
        Integer goalNodeID = getID(newGoalState);

        Integer nextNodeID = bottomLayeredAgent.searchSubGoal(currentNodeID,
                             goalNodeID);
        return getState(nextNodeID);
    }

    public Vector novelSearch(Vector currentState) {
        Integer currentNodeID = getID(currentState);
        Integer nextNodeID = bottomLayeredAgent.novelSearch(currentNodeID);
        return getState(nextNodeID);
    }

    public void counterReset() {
        bottomLayeredAgent.resetUpperAndThisLayerFamiliarCount();
    }

    public void load(ObjectInputStream oInputStream) throws IOException,
        ClassNotFoundException {
        stateToId = (Hashtable)oInputStream.readObject();
        idToState = (Vector)oInputStream.readObject();
    }

    public void save(ObjectOutputStream oOutputStream) throws IOException  {
        oOutputStream.writeObject(stateToId);
        oOutputStream.writeObject(idToState);
    }

    public void reset() {
        bottomLayeredAgent.reset();
    }

    private Vector getSameStates(Vector a) {
        for(int i = 0; i < idToState.size(); i++) {
            if( checkValidElement(a, (Vector)idToState.get(i)) ) {
                return (Vector)idToState.get(i);
            }
        }
        return null;
    }

    private boolean checkValidElement(Vector a, Vector b) {
        for(int i = 0; i < a.size(); i++) {
            if(a.get(i) != null) {
                if(!a.get(i).equals(b.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}