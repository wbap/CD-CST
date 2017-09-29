package environment;

class MapData {
    private String[][] map;
    private enum Direction {
        STOP(-1), UP(0), UPPERLEFT(1), LEFT(2), LOWERLEFT(3), DOWN(4), LOWERRIGHT(5), RIGHT(6), UPPERRIGHT(7);
        private final int id;
        Direction(int i) {
            id = i;
        }
        public int getId(){
            return id;
        }
        public static Direction valueOf(int id) {
            for (Direction d: values()) {
                if (d.getId() == id)
                    return d;
            }
            throw new IllegalArgumentException("no such enum object");
        }
    }

    MapData(String[][] map) {
        this.map = map;
    }

    int[] getNextPosition(int x, int y, int action) {
        String here = map[x][y];

        if(here.isEmpty())
            return normalMove(x,y,action);
        switch(here.charAt(0)) {
            case 'J': return jampMove(x, y, action);
            case 'F': return flowMove(x, y, action);
            case 'C': return cliffMove(x, y, action);
            default:  return normalMove(x, y, action);
        }
    }

    private boolean isMovable(int[] array) {
        return isMovable(array[0], array[1]);
    }

    private boolean isMovable(int x, int y) {
        if (!((x >= 0) && (x < map.length) && (y >= 0) && (y < map[0].length)))
            return false;
        return !(map[x][y].equals("W") || map[x][y].equals("D"));
    }

    private int[] getNextOrOriginal(int[] newpos, int oldx, int oldy) {
        if(isMovable(newpos))
            return newpos;
        else
            return new int[]{oldx, oldy};
    }

    private int[] normalMove(int oriX, int oriY, int action) {
        int[] nextpos = new int[2];
        switch(Direction.valueOf(action)) {
            case STOP:       nextpos = new int[]{oriX,   oriY};   break;
            case UP:         nextpos = new int[]{oriX,   oriY-1}; break;
            case UPPERLEFT:  nextpos = new int[]{oriX-1, oriY-1}; break;
            case LEFT:       nextpos = new int[]{oriX-1, oriY};   break;
            case LOWERLEFT:  nextpos = new int[]{oriX-1, oriY+1}; break;
            case DOWN:       nextpos = new int[]{oriX,   oriY+1}; break;
            case LOWERRIGHT: nextpos = new int[]{oriX+1, oriY+1}; break;
            case RIGHT:      nextpos = new int[]{oriX+1, oriY};   break;
            case UPPERRIGHT: nextpos = new int[]{oriX+1, oriY-1}; break;
        }
        return getNextOrOriginal(nextpos, oriX, oriY);
    }

    private int[] jampMove(int x, int y, int action) {
        int startIndex = map[x][y].indexOf( "(" );
        int endIndex = map[x][y].indexOf( ")" );
        String[] strs = map[x][y].substring(startIndex+1, endIndex).split(":");

        int toStateX = Integer.parseInt(strs[0]);
        int toStateY = Integer.parseInt(strs[1]);
        int jampAction = Integer.parseInt(strs[2]);
        int[] elem = new int[2];
        if(action == jampAction) {
            elem[0] = toStateX;
            elem[1] = toStateY;
        } else {
            elem = normalMove(x, y, action);
        }
        return getNextOrOriginal(elem, x, y);
    }

    private int[] flowMove(int x, int y, int action) {
        Character flow = map[x][y].charAt(2);
        int[] next = normalMove(x, y, action);
        if(!isMovable(next))
            return new int[]{x,y};
        switch (flow) {
            case 'U':
                next[1]--;
            case 'L':
                next[0]--;
            case 'D':
                next[1]++;
            case 'R':
                next[0]++;
        }
        return getNextOrOriginal(next, x, y);
    }

    private int[] cliffMove(int x, int y, int action) {
        Character cantGO = map[x][y].charAt(2);
        int invalidAction = -1;
        switch(cantGO) {
            case 'U': invalidAction = 0; break;
            case 'L': invalidAction = 2; break;
            case 'D': invalidAction = 4; break;
            case 'R': invalidAction = 6; break;
        }
        if(action != invalidAction) {
            return getNextOrOriginal(normalMove(x, y, action), x, y);
        }
        return new int[]{x,y};
    }
}