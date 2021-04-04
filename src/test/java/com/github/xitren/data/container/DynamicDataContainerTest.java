package com.github.xitren.data.container;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicDataContainerTest {
    @Test
    void length() {
        DynamicDataContainer ddc = new DynamicDataContainer();
        int size = 26;
        double[] array = new double[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        ddc.add(array);
        int view1 = ddc.length();
        int view2 = array.length + 16;

        assertEquals(view1, view2);
    }

    @Test
    void get() {
        double[] array = new double[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        int i = 83;
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        double view1 = ddc.get(i + 16);
        double view2 = array[i];

        assertEquals(view1, view2);
    }

    @Test
    void lastblock() {
        double[] array = new double[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        int size = 14;
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        ddc.lastblock(view1, size);
        int j = 0;
        for (int i = array.length - size; i < array.length; i++) {
            view2[j] = array[i];
            j = j + 1;
        }
        assertArrayEquals(view1, view2);
    }


    @Test
    void testLastblock() {
        int size = 14;
        double[] array = new double[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        for (int i = 0; i < size; i++) {
            view1[i] = ddc.lastblock(size)[i];
        }
        int j = 0;
        for (int i = array.length - size; i < array.length; i++) {
            view2[j] = array[i];
            j = j + 1;
        }
        assertArrayEquals(view1, view2);
    }

    @Test
    void testLastblock1000() {
        int size = 341;
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        for (int i = 0; i < size; i++) {
            view1[i] = ddc.lastblock(size)[i];
        }
        int j = 0;
        for (int i = array.length - size; i < array.length; i++) {
            view2[j] = array[i];
            j = j + 1;
        }
        assertArrayEquals(view1, view2);
    }

    @Test
    void add() {
        int size = 100;
        double[] array = new double[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        double[] view1 = new double[size];
        ddc.datacopy(ddc.length() - size, view1, 0, size);
        assertArrayEquals(array, view1);
    }

    @Test
    void testAdd() {
        int size = 100;
        int[] array = new int[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        for (int i = 0; i < size; i++) {
            view2[i] = array[i];
        }
        ddc.datacopy(ddc.length() - size, view1, 0, size);
        assertArrayEquals(view2, view1);

    }

    @Test
    void testAdd1() {
        int size = 100;
        long[] array = new long[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        for (int i = 0; i < size; i++) {
            view2[i] = array[i];
        }
        ddc.datacopy(ddc.length() - size, view1, 0, size);
        assertArrayEquals(view2, view1);

    }

    @Test
    void cut() {
        int size = 500;
        double[] array = new double[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        double[] array1 = new double[size];
        for (int i = 0; i < array1.length; i++) {
            array[i] = Math.random();
        }
        double[] array2 = new double[size];
        for (int i = 0; i < array2.length; i++) {
            array[i] = Math.random();
        }
        double[] view1 = new double[size];
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        ddc.add(array1);
        ddc.add(array2);
        ddc.cut(400, 700);
        ddc.datacopy(0, view1, 0, ddc.length());
        assertArrayEquals(array1, view1);
    }
    @Test
    void testClone() {
        DynamicDataContainer ddc = new DynamicDataContainer();
        if (ddc.clone() instanceof DynamicDataContainer) {
            System.out.println("True");
        }else {
            System.out.println("False");
        }
    }

    @Test
    public void getDataArray() {
        DynamicDataContainer ddc = new DynamicDataContainer();
        double[] view = new double[32];
        double[] view2 = new double[32];
        for (int j = 1;j < 5;j++) {
            double[] data = new double[16];
            for (int i = 0; i < data.length; i++) {
                data[i] = j;
            }
            ddc.add(data);
        }
        ddc.datacopy(8, view, 0, view.length);
        ddc.datacopy(24, view, 0, 4);
        for (int i = 0; i < view.length; i++) {
            view2[i] = ddc.get(i + 8);
        }
        for (int i = 0; i < 4; i++) {
            view2[i] = ddc.get(i + 24);
        }
        Assertions.assertArrayEquals(view, view2);
    }

    private final int SIZE = 32;

    @Test
    public void getLastDataArray() {
        DynamicDataContainer ddc = new DynamicDataContainer();
        double[] view = new double[SIZE];
        double[] view2 = new double[SIZE];
        for (int j = 0;j < view.length;j++) {
            view[j] = j + 1;
            view2[j] = j + 1 + SIZE;
        }
        ddc.add(view);
        ddc.add(view2);
        double[] last = ddc.lastblock(40);
        double[] last2 = ddc.lastblock(4);
        System.out.println();
    }
}
