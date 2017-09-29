package environment;

import java.util.*;

public class EnvImple {

    private Environment environment = null;

    private final String MAPFILENAME = "src/main/resources/CDmap3.csv";

    private Random randomActionGenerator;
    private Random randomGoalGenerator;

    private Vector state;
    private int action;
    private Vector goal;

    private boolean restart = true;

    private int repeatProcessCount = 0;
    private int trialNum;

    private int GOALSEARCHSTARTSTEP = 1;
    private boolean goalSearchFlg = false;

    private int envSizeX;
    private int envSizeY;

    private int goalAction = 6;


    public EnvImple() {
        environment  = new Environment(MAPFILENAME);
        int[] mapSize = environment.getMapSize();
        envSizeX = mapSize[0];
        envSizeY = mapSize[1];
        randomActionGenerator = new Random(0);
        randomGoalGenerator = new Random(0);
    }

    public void repeatProcess() {
        repeatProcessCount++;

        if(restart) {
            initRobotPos();
            restart = false;
        } else {
            act();
        }

        setCurrentState(action);

        if(goalSearchFlg) {
            setGoal();
            if(state.equals(goal)) {
                trialNum++;
            }
        }

        if( repeatProcessCount == GOALSEARCHSTARTSTEP) {
            goalSearchFlg = true;
            makeRandomGoal();
        }

        while(state.equals(goal)) {
            makeRandomGoal();
            setGoal();
        }

        systemOut();
    }

    public void setCurrentState(int action) {
        setRobotPositionToState();
        state.add(new Integer(action));
    }

    public void setNextAction(Vector nextState) {
        this.action = fetchNextAction(nextState);
    }

    public Vector getState() {
        return state;
    }

    public Vector getGoal() {
        return goal;
    }

    public boolean getRestartFlg() {
        return restart;
    }

    private void initRobotPos() {
        environment.initRobotPos();
        action = 0;
    }


    private void act() {
        environment.run(action);
    }

    private int fetchNextAction(Vector subgoal) {
        if (subgoal != null)
            return ((Integer)subgoal.lastElement());
        else
            return randomActionGenerator.nextInt(4)*2;
    }

    private void setRobotPositionToState() {
        state = new Vector();
        int[] newState = environment.getRobotPosition();

        for(int i = 0; i < newState.length; i++) {
            state.add(new Integer(newState[i]));
        }
    }

    private void setGoal() {
        int[] goalState = environment.fetchGoalPosition();

        if(goalState == null) {
            goal = null;
            return;
        }
        goal = new Vector();

        for(int i = 0; i <goalState.length; i++ ) {
            goal.add(goalState[i]);
        }
        goal.add(new Integer(goalAction));
    }

    private void makeRandomGoal() {

        int[] randomGoal = null;
        while(true) {
            randomGoal = getRandomState();
            String info = environment.getMapInfo(randomGoal[0], randomGoal[1]);
            if(info.equals("")) {
                String nextState = "";
                switch(randomGoal[2]) {
                    case 0: nextState = environment.getMapInfo(randomGoal[0], randomGoal[1]+1); break;
                    case 2: nextState = environment.getMapInfo(randomGoal[0]+1, randomGoal[1]); break;
                    case 4: nextState = environment.getMapInfo(randomGoal[0], randomGoal[1]-1); break;
                    case 6: nextState = environment.getMapInfo(randomGoal[0]-1, randomGoal[1]); break;
                }
                if(nextState.equals(""))
                    break;
            }
        }

        environment.setGoal(randomGoal[0], randomGoal[1]);
        goalAction = randomGoal[2];
    }

    private int[] getRandomState() {
        int[] randomState = new int[3];
        randomState[0] = randomGoalGenerator.nextInt(envSizeX-1) + 1;
        randomState[1] = randomGoalGenerator.nextInt(envSizeY-1) + 1;
        randomState[2] = randomGoalGenerator.nextInt(4)*2;
        return randomState;
    }

    private void systemOut() {
        System.out.println("");
        System.out.println("[ Environment ]");
        System.out.println("  RepeatProcess Count " + repeatProcessCount);
        System.out.println("  Trial Count " + trialNum);
        System.out.println("  state      " + state);
        System.out.println("  goal       " + goal);
    }
}