package environment;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class EnvironmentCanvas extends Canvas {

    private String[][] map;
    private int[] robotState;

    private int xInterval;
    private int yInterval;
    private int height;
    private int width;

    private boolean resized;

    private Image offImage;
    private Graphics offGraphics;

    private enum info2Map {
        START("S"), OBJECTIVE("O"), DOORCLOSE("D"), DOOROPEN("d"), KEY("K"),
        JUMP("J"), FLOW("F"), CLIFF("C"), WALL("W");
        private final static EnumMap<info2Map, Color> map;
        static {
            map = new EnumMap<info2Map, Color>(info2Map.class);
            map.put(info2Map.START, Color.pink);
            map.put(info2Map.OBJECTIVE, Color.green);
            map.put(info2Map.DOORCLOSE, Color.darkGray);
            map.put(info2Map.DOOROPEN, Color.lightGray);
            map.put(info2Map.KEY, Color.yellow);
            map.put(info2Map.JUMP, Color.gray);
            map.put(info2Map.FLOW, Color.cyan);
            map.put(info2Map.CLIFF, Color.orange);
            map.put(info2Map.WALL, Color.black);
        }
        private final String s;
        info2Map(String s) {
            this.s = s;
        }
        public static Color getColor(String s) {
            return map.get(getInfo(s));
        }
        public String getId(){
            return s;
        }
        public static info2Map getInfo(final String s) {
            for (info2Map d : values()) {
                if (d.getId().equals(s)) {
                    return d;
                }
            }
            throw new IllegalArgumentException("no such enum object");
        }
    }

    EnvironmentCanvas() {
        super();
        addComponentListener(new CanvasComponentAdapter());
        resized = false;
    }

    void initCanvas(String[][] map, int[] robotState) {
        this.map = map;
        this.robotState = robotState;
        setSizeInfo();
    }

    @Override
    public void paint(Graphics g) {
        if( (offGraphics == null) || resized ) {
            offImage = createImage(width, height);
            offGraphics = offImage.getGraphics();
            resized = false;
        }
        drawOffImage(offGraphics);
        g.drawImage(offImage, 0, 0, this);
    }

    private void drawOffImage(Graphics graphics) {
        graphics.setColor(getBackground());
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(Color.black);

        int xNum = map.length+1;
        int yNum = map[0].length+1;

        for(int i = yInterval; i <= yInterval * yNum; i = i+yInterval) {
            graphics.drawLine(xInterval, i, xInterval * xNum, i);
        }
        for(int i = xInterval; i <= xInterval * xNum; i = i+xInterval) {
            graphics.drawLine(i, yInterval, i, yInterval * yNum);
        }
        for(int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[0].length; y++) {
                int[] rectInfo = getMapInfo(x, y);

                String mapID = "";
                if(map[x][y].length() > 0) {
                    mapID = map[x][y].substring(0, 1);
                }
                if(mapID.equals(""))
                    continue;
                graphics.setColor(info2Map.getColor(mapID));
                graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);
                if(mapID.equals("F") || mapID.equals("C")) {
                    int startIndex = map[x][y].indexOf("(");
                    int endIndex = map[x][y].indexOf(")");
                    String dir = map[x][y].substring(startIndex + 1, endIndex);
                    drawDirection(graphics, rectInfo, dir);
                }
            }
        }
        int[] rectInfo = getMapInfo(robotState[0], robotState[1]);
        graphics.setColor(Color.blue);
        graphics.fillOval(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);
        drawRewardValue(graphics);
    }

    private void drawDirection(Graphics graphics, int[] rectInfo, String dir) {
        graphics.setColor(Color.black);
        String direction = "";
        switch(dir) {
            case "U": direction = "↑"; break;
            case "D": direction = "↓"; break;
            case "L": direction = "←"; break;
            case "R": direction = "→"; break;
        }
        graphics.drawString(direction, rectInfo[0], rectInfo[1]+rectInfo[3]);
    }


    private void drawRewardValue(Graphics graphics) {
        for(int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[0].length; y++) {
                int[] rectInfo = getMapInfo(x, y);
                String reward = getReward(x, y);
                graphics.setColor(Color.black);
                graphics.drawString(reward, rectInfo[0],rectInfo[1]+rectInfo[3]);
            }
        }
    }

    private String getReward(int x, int y) {
        String mapID = "";
        if(map[x][y].length() > 0) {
            mapID = map[x][y].substring(0, 1);
        }
        switch (mapID) {
            case "W":
                return "";
            case "O": {
                int startIndex = map[x][y].indexOf("(");
                int endIndex = map[x][y].indexOf(")");
                return map[x][y].substring(startIndex + 1, endIndex);
            }
            case "J": {
                int startIndex = map[x][y].indexOf("(");
                int endIndex = map[x][y].indexOf(")");
                String str = map[x][y].substring(startIndex + 1, endIndex);

                StringTokenizer st = new StringTokenizer(str, ":");
                if (st.countTokens() == 4) {
                    for (int i = 0; i < 3; i++) {
                        st.nextToken();
                    }
                    return st.nextToken();
                }
                break;
            }
            default:
                if(isAdjacentToWall(x,y))
                    return "-1";
                break;
        }
        return "";
    }

    private boolean isAdjacentToWall(int x, int y) {
        for (int n = -1; n < 2; n++) {
            for (int m = -1; m < 2; m++) {
                if ((x + n >= 0) && (y + m >= 0) && (x + n < map.length) &&
                    (y + m < map[0].length)) {
                    if (map[x + n][y + m].equals("W"))
                        return true;
                }
            }
        }
        return false;
    }

    private void setSizeInfo() {
        Dimension d = getSize();
        height = d.height;
        width = d.width;
        xInterval = d.width / (map.length+2);
        yInterval = d.height / (map[0].length+2);
    }

    private int[] getMapInfo(int x, int y) {
        int[] rectInfo = null;
        if( (x >= 0)&&(y >= 0) && (x < map.length)&&(y < map[0].length) ) {
            rectInfo = new int[4];
            rectInfo[0] = ((x+1)*xInterval) + 1;
            rectInfo[1] = ((y+1)*yInterval) + 1;
            rectInfo[2] = xInterval-1;
            rectInfo[3] = yInterval-1;
        }
        return rectInfo;
    }

    class CanvasComponentAdapter extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            setSizeInfo();
            resized = true;
            repaint();
        }
    }
}