package environment;

import java.awt.*;

public class Environment {

    private Frame frame;
    private EnvironmentCanvas canvas;
    private String[][] map;
    private int[] robotState;
    private MapData mapdata;

    public Environment(String fileName) {
        robotState = new int[2];
        initMapInfo(fileName);
        initFrame();
    }

    public void initMapInfo(String fileName) {
        MapFileReader mapfileReader = new MapFileReader(fileName);
        map = mapfileReader.getMap();
        mapdata = new MapData(map);
        initRobotPos();
    }

    public void initRobotPos() {
        int[] pos = searchMap("S");
        robotState[0] = pos[0];
        robotState[1] = pos[1];
    }

    private void initFrame() {
        canvas = new EnvironmentCanvas();
        canvas.initCanvas(map, robotState);

        frame = new Frame("environment");
        frame.setLayout(new BorderLayout(5, 5));
        frame.add(canvas, "Center");
        frame.setSize(500, 360);
        frame.setVisible(true);
    }

    public void run(int action) {
        int[] newState = mapdata.getNextPosition(robotState[0], robotState[1], action);
        if (!((robotState[0] == newState[0]) && (robotState[1] == newState[1]))) {
            robotState[0] = newState[0];
            robotState[1] = newState[1];
            canvas.repaint();
        }
    }

    public void setGoal(int x, int y) {
        int[] goalState = fetchGoalPosition();
        if (goalState != null) {
            map[goalState[0]][goalState[1]] = "";
        }
        map[x][y] = "O(1)";
    }


    public int[] getRobotPosition() {
        return robotState;
    }

    public int[] fetchGoalPosition() {
        return searchMap("O");
    }

    public String getMapInfo(int x, int y) {
        return map[x][y];
    }

    public int[] getMapSize() {
        int[] mapSize = {map.length, map[0].length};
        return mapSize;
    }

    private int[] searchMap(String targetID) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                if (map[x][y].length() > 0) {
                    String id = map[x][y].substring(0, 1);
                    if (id.equals(targetID)) {
                        int[] pos = {x, y};
                        return pos;
                    }
                }
            }
        }
        return null;
    }
}