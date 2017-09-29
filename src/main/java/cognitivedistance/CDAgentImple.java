package cognitivedistance;

import java.util.Vector;

public class CDAgentImple {

    Agent agent;

    private int repeatProcessCount = 0;

    private Vector state;
    private Vector goal;
    private Vector nextState;
    private boolean resetFlg;

    private int layerNum = 3;
    private int maxCDLngth = 9;
    private int shallowSearchLngth = 1;
    private int deepSearchLngth = 200;
    private int minSearchLngth = 1;
    private int maxSegmentSize = 3;

    private String loadFileName = "src/main/resources/20000LearnData";
    private String saveFileName = null;
//    private String loadFileName = null;
//    private String saveFileName = "src/main/resources/20000LearnData";

    private int saveCount = 20000;

    public CDAgentImple() {
        agent = new Agent(layerNum, maxCDLngth, shallowSearchLngth,
                          deepSearchLngth, minSearchLngth, maxSegmentSize);
        if(loadFileName != null) {
            agent.load(loadFileName);
        }
    }

    public void repeatProcess() {
        System.out.println("");
        System.out.println("[ Agent ]");
        repeatProcessCount++;
        System.out.println("  repeatProcessCount " + repeatProcessCount);

        nextState = agent.searchNextState(state, goal);

        if(resetFlg)
            agent.reset();
        if(repeatProcessCount == saveCount && saveFileName != null)
            agent.save(saveFileName);
    }

    public void setResetFlg(boolean resetFlg) {
        this.resetFlg = resetFlg;
    }

    public void setState(Vector state) {
        this.state = state;
    }

    public void setGoal(Vector goal) {
        this.goal = goal;
    }

    public Vector getNextState() {
        return nextState;
    }
}