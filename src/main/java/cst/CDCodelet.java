package cst;

import br.unicamp.cst.core.entities.Codelet;
import cognitivedistance.CDAgentImple;
import environment.EnvImple;
import java.util.Vector;

public class CDCodelet extends Codelet {

    private CDAgentImple cdAgentImple;
    private EnvImple envImple;
    private Vector nextState;

    public CDCodelet() {
        cdAgentImple = new CDAgentImple();
        envImple = new EnvImple();
    }

    @Override
    public void accessMemoryObjects() {
        // it is better to move private members to global MemoryObjects
    }

    @Override
    public void proc() {
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

    @Override
    public void calculateActivation() {

    }

}