package cognitivedistance;

import java.util.*;
import environment.*;

public class LocalInterface {

    private CDAgentImple cdAgentImple;
    private EnvImple envImple;
    private Vector nextState;

    public LocalInterface() {
        cdAgentImple = new CDAgentImple();
        envImple = new EnvImple();
    }

    private void repeatProcess() {
        while(true) {
            envImple.setNextAction(nextState);
            envImple.repeatProcess();
            Vector state = envImple.getState();
            Vector goal = envImple.getGoal();
            boolean flg = envImple.getRestartFlg();

            cdAgentImple.setResetFlg(flg);
            cdAgentImple.setState(state);
            cdAgentImple.setGoal(goal);
            cdAgentImple.repeatProcess();
            nextState = cdAgentImple.getNextState();
        }
    }

    public static void main(String args[]) {
        LocalInterface li = new LocalInterface();
        li.repeatProcess();
    }
}
