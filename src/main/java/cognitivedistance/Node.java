package cognitivedistance;

import java.util.*;
import java.io.*;

public class Node implements Serializable {

    private Integer ID;
    private Integer upperID;
    private Integer lowerID;
    private int toLandmarkLngth;

    // Key = nodeID, Element = distance
    private Hashtable cognitiveDistances;

    private Set<Integer> forwardNodeIDSet;
    private Set<Integer> inverseNodeIDSet;

    private int visitCount;

    public static int maxCDLngth = 10;

    public Node(Integer ownID, Integer lowerID) {
        this.ID = ownID;
        this.lowerID = lowerID;
        toLandmarkLngth = -1;

        cognitiveDistances = new Hashtable();
        forwardNodeIDSet = new HashSet<Integer>();
        inverseNodeIDSet = new HashSet<Integer>();
    }

    public Integer getID() {
        return ID;
    }

    public Integer getUpperID() {
        return upperID;
    }

    public Integer getLowerID() {
        return lowerID;
    }

    public int getToLandmarkStep() {
        return toLandmarkLngth;
    }

    public int getCognitiveDistance(Integer nodeID) {
        if(getID().equals(nodeID))
            return 0;
        Integer distanceObj = (Integer)cognitiveDistances.get(nodeID);
        return (distanceObj == null) ? -1 : distanceObj;
    }

    public Set<Integer> getForwardNodeIDSet() {
        return forwardNodeIDSet;
    }

    public Set<Integer> getInverseNodeIDSet() {
        return inverseNodeIDSet;
    }

    public LinkedList getCDKeys() {
        LinkedList ll = new LinkedList();
        ll.addAll(cognitiveDistances.keySet());
        return ll;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setUpperIDAndStep(Integer upperID, int toLandmarkLngth) {
        this.upperID = upperID;
        this.toLandmarkLngth = toLandmarkLngth;
    }


    public void setCognitiveDistance(Integer nodeID, int distance) {
        if(distance > maxCDLngth)
            return;
        Integer distanceObj = (Integer)cognitiveDistances.get(nodeID);
        if((distanceObj==null) || (distanceObj >distance)) {
            cognitiveDistances.put(nodeID, distance);
        }
    }

    public void setForwardNode(Integer nodeID) {
        forwardNodeIDSet.add(nodeID);
    }

    public void setInverseNode(Integer nodeID) {
        inverseNodeIDSet.add(nodeID);
    }

    public void countVisitCount() {
        visitCount++;
    }

    public void removeSameForwardNodeID() {
        forwardNodeIDSet.remove(ID);
    }

    public void removeSameInverseNodeID() {
        inverseNodeIDSet.remove(ID);
    }
}