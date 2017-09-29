package cognitivedistance;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class Agent {

    private InterfaceAgent interfaceAgent;
    private LayeredAgent[] layeredAgentArray;
    private int layerNum;

    public Agent(int layerNum, int maxCDLngth, int shallowSearchLngth,
                 int deepSearchLngth, int minSearchLngth, int maxSegmentSize){
        this.layerNum = layerNum;
        Node.maxCDLngth = maxCDLngth;
        LayeredAgent.shallowSearchLength = shallowSearchLngth;
        LayeredAgent.deepSearchLength = deepSearchLngth;
        LayeredAgent.minSearchLength = minSearchLngth;
        LayeredAgent.maxSegmentSize = maxSegmentSize;
        initAgent();
    }

    private void initAgent() {
        layeredAgentArray = new LayeredAgent[layerNum];
        for(int i = layerNum-1; i >= 0; i--) {
            if(i == layerNum-1) {
                layeredAgentArray[i] = new LayeredAgent(null, i);
            } else {
                layeredAgentArray[i] = new LayeredAgent(layeredAgentArray[i+1], i);
            }
        }

        interfaceAgent = new InterfaceAgent(layeredAgentArray[0]);
    }

    public Vector searchNextState(Vector currentState, Vector goalState) {
        interfaceAgent.addNode(currentState);

        Vector nextState = interfaceAgent.searchSubGoal(currentState,goalState);

        if(nextState == null) {
            nextState = interfaceAgent.novelSearch(currentState);
        } else {
            interfaceAgent.counterReset();
        }

        return nextState;
    }

    public void load(String fileName) {
        System.out.println("Loading learning data....");
        try {
            FileInputStream istream = new FileInputStream(fileName);
            ObjectInputStream oInputStream = new ObjectInputStream(istream);

            interfaceAgent.load(oInputStream);
            for(int i = 0; i < layerNum; i++) {
                layeredAgentArray[i].load(oInputStream);
            }

            oInputStream.close();
            istream.close();

        } catch(Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public void save(String fileName) {
        System.out.println("Saving learning data....");
        try {
            FileOutputStream ostream = new FileOutputStream(fileName, false);
            ObjectOutputStream oOutputStream = new ObjectOutputStream(ostream);

            interfaceAgent.save(oOutputStream);
            for(int i = 0; i < layerNum; i++) {
                layeredAgentArray[i].save(oOutputStream);
            }

            oOutputStream.flush();
            oOutputStream.close();
            ostream.close();

        } catch(Exception e) {
            System.out.println(e);
        }

    }

    public void reset() {
        interfaceAgent.reset();
    }
}
