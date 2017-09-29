package environment;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MapDataTest {

    public static String[][] map = {
        {"W","W","W","W"},
        {"W","","","W"},
        {"W","","","W"},
        {"W","W","W","W"},
    };

    @Test
    public void testIsMovableWhenNotMovable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MapData mapdata = new MapData(map);
        Method method = MapData.class.getDeclaredMethod("isMovable", int.class, int.class);
        method.setAccessible(true);

        boolean expected = false;
        boolean actual = (boolean)method.invoke(mapdata, 3,3);
        assertEquals("testIsMovableWhenNotMovable", expected, actual);
    }

    @Test
    public void testIsMovableWhenMovable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MapData mapdata = new MapData(map);
        Method method = MapData.class.getDeclaredMethod("isMovable", int.class, int.class);
        method.setAccessible(true);

        boolean expected = true;
        boolean actual = (boolean)method.invoke(mapdata, 2,2);
        assertEquals("testIsMovableWhenNotMovable", expected, actual);
    }

    @Test
    public void testGetNextOrOriginalWhenNext() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MapData mapdata = new MapData(map);
        int[] array = {2,2};
        Method method = MapData.class.getDeclaredMethod("getNextOrOriginal", int[].class, int.class, int.class);
        method.setAccessible(true);

        int[] expected = {2,2};
        int[] actual = (int[])method.invoke(mapdata, array, 1,1);
        assertArrayEquals("testGetNextOrOriginalWhenNext", expected, actual);
    }

    @Test
    public void testGetNextOrOriginalWhenOriginal() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MapData mapdata = new MapData(map);
        int[] array = {0,0};
        Method method = MapData.class.getDeclaredMethod("getNextOrOriginal", int[].class, int.class, int.class);
        method.setAccessible(true);

        int[] expected = {1,1};
        int[] actual = (int[])method.invoke(mapdata, array, 1,1);
        assertArrayEquals("testGetNextOrORiginalWhenOriginal", expected, actual);
    }


    @Test
    public void testNormalMoveWhenNotMove() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MapData mapdata = new MapData(map);
        Method method = MapData.class.getDeclaredMethod("normalMove", int.class, int.class, int.class);
        method.setAccessible(true);

        int[] expected = {1,1};
        int[] actual = (int[])method.invoke(mapdata,1,1,2);

        assertArrayEquals("testNormalMoveWhenNotMove",expected, actual);
    }

    @Test
    public void testNormalMoveWhenMove() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MapData mapdata = new MapData(map);
        Method method = MapData.class.getDeclaredMethod("normalMove", int.class, int.class, int.class);
        method.setAccessible(true);

        int[] expected = {2,2};
        int[] actual = (int[])method.invoke(mapdata,1,1,5);
        assertArrayEquals("testNormalMoveWhenMove",expected, actual);
    }
}