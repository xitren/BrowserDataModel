package io.github.xitren.data.container;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaticDataContainerTest {

    @Test
    void length() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        int view1 = sdc.length();
        int view2 = array.length;

        assertEquals(view1, view2);

    }

    @Test
    void get() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        int i = 45;
        double expected = sdc.get(i);
        double actual = array[i];
        assertEquals(expected, actual);
    }

    @Test
    void lastblock() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        int size = 5;
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        sdc.lastblock(view1, size);
        int j = 0;
        for(int i = array.length - size; i< array.length; i++){
            view2[j] = array[i];
            j = j+1;
            }
        assertArrayEquals(view1, view2);
    }

    @Test
    void testLastblock() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        int size = 5;
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        int a = 0;
        for(int i = array.length - size; i< array.length; i++){
            view2[a] = sdc.lastblock(size)[a];
            a = a+1;
        }
        int j =0;
        for(int i = array.length - size; i< array.length; i++){
            view1[j] = array[i];
            j = j+1;
        }
        assertArrayEquals(view1, view2);
    }

    @Test
    void add() {
        int size = 100;
        double[] array = new double[size];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        sdc.add(array);
        double[] view1 = new double[size];
        sdc.datacopy(0, view1, 0, size);
        assertArrayEquals(array,view1);
    }

    @Test
    void testAdd() {
        int size = 100;
        int[] array = new int[size];
        for (int i = 0; i< array.length;i++){
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer();
        sdc.add(array);
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        sdc.datacopy(0, view1, 0, size);
        for (int i = 0; i<size; i++) {
            view2[i] = array[i];
        }
        assertArrayEquals(view2,view1);

    }

    @Test
    void testAdd1() {
        int size = 100;
        long[] array = new long[size];
        for (int i = 0; i< array.length;i++){
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer();
        sdc.add(array);
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        sdc.datacopy(0, view1, 0, size);
        for (int i = 0; i<size; i++) {
            view2[i] = array[i];
        }
        assertArrayEquals(view2,view1);
    }

    @Test
    void datacopy() {
        int size = 100;
        double[] array = new double[size];
        for (int i = 0; i< array.length;i++){
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        int srcPos = 0;
        int destPos =0;
        int sizeBlock = 10;
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        sdc.datacopy(srcPos, view1, destPos, sizeBlock);
        int j = destPos;
        for (int i = srcPos; i<sizeBlock; i++){
            view2[j] = array[i];
            j = j+1;
        }
        assertArrayEquals(view1, view2);
    }

    @Test
    void cut() {
        int size = 100;
        double[] array = new double[size];
        for (int i = 0; i< array.length;i++){
            array[i] = i;
        }
        int sizeCut = 47;
        int start = 5;
        StaticDataContainer sdc = new StaticDataContainer(array);
        sdc.cut(start, sizeCut);
        double[] view1 = new double[sizeCut];
        double[] view2 = new double[sizeCut];
        for (int i = 0; i<sizeCut; i++){
            view1[i] = sdc.get(i);
        }
        for (int i = 0; i<sizeCut; i++){
            view2[i] = array[start+i];
        }
        assertArrayEquals(view1, view2);
    }

    @Test
    void testClone() {
        StaticDataContainer sdc = new StaticDataContainer();
        if (sdc.clone() instanceof StaticDataContainer) {
            System.out.println("True");
        }else {
            System.out.println("False");
        }
    }
}
