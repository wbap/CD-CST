package cognitivedistance;

import java.util.*;
import java.io.*;

public class LayeredAgent {

    private int layerID;
    private LayeredAgent upperLayerAgent;
    private Vector nodes;
    private StateHistory stateHistory;

    public static int shallowSearchLength = 3;
    public static int deepSearchLength = 200;
    public static int minSearchLength = 2;
    public static int maxSegmentSize = 5;
    public static int maxFamiliarCount = 10;


    private int upperLayerCnt;

    private Integer id_Vu0;
    private Integer id_Gu0;
    private boolean useUpperFlag = true;

    private Integer id_S0;
    private Integer id_F0;
    private Integer id_FVu0;
    private int familiarCount;

    public LayeredAgent(LayeredAgent upperLayeredAgent, int layerID) {
        this.layerID = layerID;
        this.upperLayerAgent = upperLayeredAgent;
        nodes = new Vector();
        stateHistory = new StateHistory(Node.maxCDLngth+1);

        System.out.println("LayerID " + layerID);
        System.out.println(" Max CD Length             " + Node.maxCDLngth);
        System.out.println(" Shallow Search Length     " + shallowSearchLength);
        System.out.println(" Deep Search Length        " + deepSearchLength);
        System.out.println(" Min Search Length         " + minSearchLength);
        System.out.println(" Max Segment Size          " + maxSegmentSize);

    }

    public Node getNode(Integer id) {
        if(id == null || nodes.size() <=id)
            return null;
        return (Node)nodes.get(id);
    }

    void createNewNode(Integer lowerID) {
        Node node = new Node(nodes.size(), lowerID);
        nodes.add(node);
    }

    private Integer getUpperLayerNodeID(Integer nodeID) {
        if(nodeID == null)
            return null;
        return renewUpperIDAndStep(nodeID);
    }

    private Integer getLowerLayerNodeID(Integer nodeID) {
        if(nodeID == null) {
            return null;
        }
        Node node = getNode(nodeID);
        return node.getLowerID();
    }

    private int getToLandmarkStep(Integer nodeID) {
        if(nodeID == null) {
            return -1;
        }
        renewUpperIDAndStep(nodeID);
        Node node = getNode(nodeID);
        return node.getToLandmarkStep();
    }

    private Integer renewUpperIDAndStep(Integer nodeID) {
        Node node = getNode(nodeID);
        Integer upperLayerNodeID = node.getUpperID();

        if(upperLayerNodeID == null) {
            return null;
        }
        return upperLayerNodeID;
    }

    private Set<Integer> getForwardNodeIDSet(Integer nodeID) {
        Node node = getNode(nodeID);
        Set<Integer> forwardNodeIDSet = node.getForwardNodeIDSet();
        node.removeSameForwardNodeID();
        return forwardNodeIDSet;
    }

    private Set<Integer> getInverseNodeIDSet(Integer nodeID) {
        Node node = getNode(nodeID);
        Set<Integer> inverseNodeIDSet = node.getInverseNodeIDSet();
        node.removeSameInverseNodeID();
        return inverseNodeIDSet;
    }

    public void addNode(Integer id_S) {
        if(id_S == null || (stateHistory.size() != 0 && id_S.equals(stateHistory.getLast())))
            return;
        learning(id_S);
        if(upperLayerAgent != null) {
            Integer id_Su = getUpperLayerNodeID(id_S);
            upperLayerAgent.addNode(id_Su);
        }
    }

    public Integer searchSubGoal(Integer id_S, Integer id_G) {
        if( id_S == null) {
            System.out.println("state == null");
            return null;
        }

        System.out.println("");
        System.out.println("[layerID:" + layerID + "] exec");
        System.out.println("  CurrentNodeID " + id_S);
        System.out.println("  GoalNodeID    " + id_G);

        if( upperLayerAgent != null ) {
            Integer id_Su = getUpperLayerNodeID(id_S);
            Integer id_Gu = getUpperLayerNodeID(id_G);
            if(id_Su == null) {
                return null;
            }

            boolean isGoalChanged = (id_Gu == null || !id_Gu.equals(id_Gu0));


            if( id_Gu != null ) {
                if( id_Su.equals(id_Gu) ) {
                    useUpperFlag = false;
                } else if(isGoalChanged) {
                    useUpperFlag = true;
                }
            } else {
                useUpperFlag = false;
            }

            id_Gu0 = id_Gu;

            if(useUpperFlag) {
                Integer id_D1 = getNextNodeID(id_S, id_G, shallowSearchLength);

                if(id_D1 != null) {
                    id_Vu0 = null;
                    printSubGoal(1, id_D1);
                    return id_D1;
                }

                if((!id_Su.equals(id_Vu0) ) && ( id_Vu0 != null) && (!isGoalChanged)) {
                    Integer id_V0 = upperLayerAgent.getLowerLayerNodeID(id_Vu0);
                    Integer id_D2 = getNextNodeID(id_S, id_V0, deepSearchLength);
                    if(id_D2 != null) {
                        printSubGoal(2, id_D2);
                        return id_D2;
                    }
                }
                id_Vu0 = upperLayerAgent.searchSubGoal(id_Su, id_Gu);
                Integer id_V0 = upperLayerAgent.getLowerLayerNodeID(id_Vu0);
                Integer id_D3 = getNextNodeID(id_S, id_V0, deepSearchLength);

                if(id_D3 != null) {
                    printSubGoal(3, id_D3);
                    return id_D3;
                }
            }
        }

        id_Vu0 = null;
        Integer id_D4 = getNextNodeID(id_S, id_G, deepSearchLength);
        if(id_D4 != null) {
            printSubGoal(4, id_D4);
            return id_D4;
        }
        return null;
    }

    private void printSubGoal(int subGoalNum, int subGoalID) {
        System.out.println("");
        System.out.println("[layerID:" + layerID + "] exec");
        System.out.println("D" + subGoalNum + " = " + subGoalID);
    }

    Integer novelSearch(Integer id_S) {
        if(!id_S.equals(id_S0)) {
            Node node = getNode(id_S);
            if(node.getVisitCount() > 0) {
                familiarCount++;
            } else {
                resetUpperAndThisLayerFamiliarCount();
            }
        }

        Integer id_D = novelSearchCore(id_S);
        if(id_D != null) {
            resetFamiliarCount();
        }
        id_S0 = id_S;
        return id_D;
    }

    private Integer novelSearchCore(Integer id_S) {
        if( (id_F0 != null) && (!id_S.equals(id_F0)) ) {
            Integer id_D8 = getNextNodeID(id_S, id_F0, 1);
            if(id_D8 != null) {
                return id_D8;
            }
        }
        id_F0 = null;

        if( upperLayerAgent != null ) {
            Integer id_Su = getUpperLayerNodeID(id_S);

            if(id_Su.equals(id_FVu0) || id_FVu0 == null){
                Integer id_FVu = upperLayerAgent.novelSearch(id_Su);
                Integer id_FV = upperLayerAgent.getLowerLayerNodeID(id_FVu);
                Integer id_D6 = getNextNodeID(id_S, id_FV, deepSearchLength);
                if(id_D6 != null) {
                    id_FVu0 = id_FVu;
                    return id_D6;
                }
            } else {
                Integer id_FV0 = upperLayerAgent.getLowerLayerNodeID(id_FVu0);
                Integer id_D5 = getNextNodeID(id_S, id_FV0, deepSearchLength);
                if(id_D5 != null) {
                    return id_D5;
                }

            }
        }
        id_FVu0 = null;

        if( familiarCount > maxFamiliarCount ) {
            Integer id_F = getNovelNodeID(id_S);
            Integer id_D7 = getNextNodeID(id_S, id_F, 1);
            if(id_D7 != null) {
                id_F0 = id_F;
                return id_D7;
            }
        }
        return null;
    }

    private Integer getNextNodeID(Integer nodeID, Integer goalNodeID,
                                  int maxSearchLength) {
        if(goalNodeID == null)
            return null;
        Object[] selectedStateInfo = getNextNodeInfo(nodeID, goalNodeID,
                                     maxSearchLength);
        return pathLearning(selectedStateInfo, goalNodeID);
    }

    private Object[] getNextNodeInfo(Integer nodeID, Integer goalNodeID,
                                     int maxSearchLength) {

        Hashtable checkTable = new Hashtable();
        checkTable.put(nodeID, nodeID);

        StateList[] stateListArray = null;
        Object[] selectedObj = null;
        int selectedLngth = 0;
        int currentLngth = 0;

        for(; currentLngth < minSearchLength; currentLngth++) {
            if(stateListArray == null) {
                StateList nextNodeIDList = getChildList(nodeID, null, checkTable);
                stateListArray = new StateList[1];
                stateListArray[0] = nextNodeIDList;
            } else {
                stateListArray = getChildListArray(stateListArray, checkTable);
            }

            Object[] obj = getNextNodeInfoFromStateListArray(stateListArray, goalNodeID);
            if(obj == null)
                continue;
            if(selectedObj == null) {
                selectedObj = obj;
                continue;
            }
            if((Integer) obj[1] + currentLngth <= ((Integer) selectedObj[1]
                     + selectedLngth)) {
                selectedLngth = currentLngth;
                selectedObj = obj;
            }
        }

        for( ; currentLngth < maxSearchLength; currentLngth++) {
            if( selectedObj != null )
                break;
            if(stateListArray == null) {
                StateList nextNodeIDList = getChildList(nodeID, null, checkTable);
                stateListArray = new StateList[1];
                stateListArray[0] = nextNodeIDList;
            } else {
                stateListArray = getChildListArray(stateListArray, checkTable);
            }

            selectedObj = getNextNodeInfoFromStateListArray(stateListArray,
                          goalNodeID);
        }
        return selectedObj;
    }

    private Object[] getNextNodeInfoFromStateListArray(
        StateList[] stateListArray, Integer goalNodeID) {

        Object[] wkObj = null;
        StateList selectedList = null;
        for(int i = 0; i < stateListArray.length; i++) {
            Object[] obj = getNextNodeInfoFromStateList(stateListArray[i], goalNodeID);
            if(obj != null) {
                if((wkObj == null) || ((Integer) wkObj[1] > (Integer) obj[1]) ) {
                    wkObj = obj;
                    selectedList = stateListArray[i];
                }
            }
        }

        Object[] selectedObj = null;
        if(wkObj != null) {
            selectedObj = new Object[3];
            selectedObj[0] = wkObj[0];
            selectedObj[1] = wkObj[1];
            selectedObj[2] = selectedList;
        }
        return selectedObj;
    }

    private Object[] getNextNodeInfoFromStateList(StateList stateList,
            Integer goalNodeID) {

        Integer selectedNodeID = null;
        int shortestDistance = -1;
        Object[] obj = null;

        ListIterator stateIterator
            = stateList.listIterator();
        while(stateIterator.hasNext()) {
            Integer directAccessNodeID
                = (Integer)stateIterator.next();
            Node directAccessNode = getNode(directAccessNodeID);
            int distance = directAccessNode.getCognitiveDistance(
                               goalNodeID);
            if(distance != -1) {
                if((shortestDistance==-1)||(shortestDistance>=distance)) {
                    selectedNodeID = directAccessNodeID;
                    shortestDistance = distance;
                }
            }
        }

        if(selectedNodeID != null) {
            obj = new Object[2];
            obj[0] = selectedNodeID;
            obj[1] = shortestDistance;
        }

        return obj;
    }

    private StateList[] getChildListArray(StateList[] stateListArray,
                                          Hashtable checkTable) {
        int nextStateCount = 0;
        for(int i = 0; i < stateListArray.length; i++) {
            nextStateCount += stateListArray[i].size();
        }

        StateList[] nextStateList = new StateList[nextStateCount];
        int n = 0;
        for(int i = 0; i < stateListArray.length; i++) {
            ListIterator il = stateListArray[i].listIterator();
            while( il.hasNext() ) {
                Integer nodeID = (Integer)il.next();
                nextStateList[n] = getChildList(nodeID, stateListArray[i],
                                                checkTable);
                n++;
            }
        }

        return nextStateList;
    }

    private StateList getChildList(Integer parentNodeID, StateList stateList,
                                   Hashtable checkTable) {
        LinkedList checkedforwardNodeIDList = new LinkedList();
        getForwardNodeIDSet(parentNodeID).forEach(nodeID -> {
            if(!checkTable.contains(nodeID)) {
                checkedforwardNodeIDList.add(nodeID);
                checkTable.put(nodeID, nodeID);
            }
        });
        return new StateList(checkedforwardNodeIDList, parentNodeID, stateList);
    }

    void resetUpperAndThisLayerFamiliarCount() {
        resetFamiliarCount();
        id_FVu0 = null;
        id_F0 = null;
        if(upperLayerAgent != null) {
            upperLayerAgent.resetUpperAndThisLayerFamiliarCount();
        }
    }

    private void resetFamiliarCount() {
        familiarCount = 0;
    }

    private Integer getNovelNodeID(Integer nodeID) {
        Integer selectedNodeID = null;
        int minVisitCount = 0;

        Node node = getNode(nodeID);
        LinkedList ll = node.getCDKeys();
        for (Object aLl : ll) {
            Integer cdNodeID = (Integer) aLl;
            Node cdNode = getNode(cdNodeID);
            int visitCount = cdNode.getVisitCount();
            if ((selectedNodeID == null) || (minVisitCount > visitCount)) {
                selectedNodeID = cdNodeID;
                minVisitCount = visitCount;
            }
        }
        return selectedNodeID;
    }

    private void learning(Integer nodeID) {
        Node node = getNode(nodeID);
        stateHistory.add(nodeID);
        int sbSize = stateHistory.size();
        if(sbSize >= 2) {
            Integer oldNodeID = (Integer)stateHistory.get(sbSize - 2);
            Node oldNode = getNode(oldNodeID);
            oldNode.countVisitCount();
        }

        int distance = 1;
        ListIterator itr
            = stateHistory.listIterator(stateHistory.size());
        itr.previous();
        while(itr.hasPrevious()) {
            Integer fromNodeID = (Integer)itr.previous();
            Node fromNode = getNode(fromNodeID);

            fromNode.setCognitiveDistance(nodeID, distance);

            Set<Integer> forwardNodeIDSet = getForwardNodeIDSet(nodeID);

            int dist = distance;
            forwardNodeIDSet.forEach(forwardNodeID -> {
                fromNode.setCognitiveDistance(forwardNodeID, dist+1);
            });

            if(distance == 1) {
                fromNode.setForwardNode(nodeID);
                node.setInverseNode(fromNodeID);
            }
            distance++;
        }
        if(upperLayerAgent != null) {
            landmarkLearning(nodeID);
        }
    }

    private void landmarkLearning(Integer nodeID) {
        Object[] nearestLandmarkAndStep = getNearestLandmark(nodeID);
        Integer nearestLandmarkID = (Integer)nearestLandmarkAndStep[0];
        int shortestStep = (Integer) nearestLandmarkAndStep[1];

        int currentStep = getToLandmarkStep(nodeID);

        Node node = getNode(nodeID);
        if(nearestLandmarkID == null) {
            if(currentStep == -1) {
                Integer newID = upperLayerCnt;
                node.setUpperIDAndStep(newID, 0);
                upperLayerAgent.createNewNode(nodeID);
                upperLayerCnt++;
            }
            return;
        }
        if((currentStep != 0) || (currentStep > shortestStep)) {
            node.setUpperIDAndStep(nearestLandmarkID, shortestStep);
        }
    }

    private Object[] getNearestLandmark(Integer nodeID) {
        Hashtable checkTable = new Hashtable();

        Integer selectedNodeID = null;
        int selectedStep = -1;

        Set[] searchNodeIDSet = new Set[maxSegmentSize];
        searchNodeIDSet[0] = getInverseNodeIDSet(nodeID);
        for(int i = 1; i < maxSegmentSize; i++) {
            searchNodeIDSet[i] = getMovableNodeList(searchNodeIDSet[i-1], checkTable);
        }

        for(int i = 0; i < maxSegmentSize; i++) {
            for (Object o : searchNodeIDSet[i]) {
                Integer searchNodeID = (Integer) o;
                int toLandmarkStep = getToLandmarkStep(searchNodeID);

                if (toLandmarkStep == -1)
                    continue;

                Integer upperNodeID = getUpperLayerNodeID(nodeID);
                Integer upperSearchNodeID = getUpperLayerNodeID(searchNodeID);
                if ((upperNodeID != null) && upperNodeID.equals(upperSearchNodeID))
                    continue;

                toLandmarkStep = toLandmarkStep + i + 1;
                if (toLandmarkStep > maxSegmentSize)
                    continue;

                if ((selectedStep == -1) || (selectedStep > toLandmarkStep)) {
                    selectedNodeID = upperSearchNodeID;
                    selectedStep = toLandmarkStep;
                }
            }
        }

        Object[] nearestLandmarkInfo = new Object[2];
        nearestLandmarkInfo[0] = selectedNodeID;
        nearestLandmarkInfo[1] = selectedStep;

        return nearestLandmarkInfo;
    }


    private Set getMovableNodeList(Set nodeSet, Hashtable checkTable) {
        Set nextNodeSet = new HashSet();
        for (Object aNodeSet : nodeSet) {
            Integer nodeID = (Integer) aNodeSet;
            Set<Integer> set = getInverseNodeIDSet(nodeID);
            set.forEach(nextNodeID -> {
                if (!checkTable.containsKey(nextNodeID)) {
                    nextNodeSet.add(nextNodeID);
                    checkTable.put(nextNodeID, nextNodeID);
                }
            });
        }
        return nextNodeSet;
    }

    private Integer pathLearning(Object[] stateInfo, Integer goalNodeID) {

        if(stateInfo == null) {
            return null;
        }

        Integer nodeID = (Integer)stateInfo[0];
        int distance = (Integer) stateInfo[1];
        StateList stateList = (StateList)stateInfo[2];

        Node node = getNode(nodeID);
        node.setCognitiveDistance(goalNodeID, distance);

        StateList parentList = stateList.getParentList();

        Integer parentNodeID = null;
        if(parentList != null) {
            Object[] parentObj = new Object[3];
            parentObj[0] = stateList.getParentNodeID();
            parentObj[1] = distance + 1;
            parentObj[2] = parentList;
            parentNodeID = pathLearning(parentObj, goalNodeID);
        } else {
            parentNodeID = nodeID;
        }
        return parentNodeID;
    }

    void load(ObjectInputStream oInputStream) throws IOException,
        ClassNotFoundException {
        nodes = (Vector)oInputStream.readObject();
    }

    void save(ObjectOutputStream oOutputStream) throws IOException {
        oOutputStream.writeObject(nodes);
    }

    void reset() {
        stateHistory.clear();
        id_Vu0 = null;
        id_Gu0 = null;
        if(upperLayerAgent != null) {
            upperLayerAgent.reset();
        }
    }
}