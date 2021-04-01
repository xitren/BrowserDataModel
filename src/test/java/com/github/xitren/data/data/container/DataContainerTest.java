package com.github.xitren.data.data.container;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataContainerTest {

    @Test
    void staticDataContainerToArray() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        DataContainer sdc = new StaticDataContainer(array);
        assertArrayEquals(sdc.toArray(sdc), array);
    }

    @Test
    void dynamicDataContainerToArray() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        double[] view1 = new double[116];
        for (int i =0; i< array.length; i++){
            view1[i+16]=array[i];
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        assertArrayEquals(ddc.toArray(ddc), view1);
    }

    @Test
    void datacopy() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        StaticDataContainer sdr = new StaticDataContainer(array);
        int srcPos = 10;
        int size = 20;
        int destPose = 0;
        double[] dst = new double[size];
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        sdr.datacopy(sdr, srcPos, dst, destPose, size);
        for(int i = 0; i<size; i++){
            view1[i] = array[srcPos+i];
        }
        for(int i = 0; i<size; i++){
            view2[i] = dst[i];
        }
        assertArrayEquals(view1, view2);
    }

    @Test
    void dinamicDatacopy() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        int srcPos = 10;
        int size = 20;
        int destPose = 0;
        double[] dst = new double[size];
        double[] view1 = new double[size];
        double[] view2 = new double[size];
        ddc.datacopy(ddc, srcPos+16, dst, destPose, size);
        for(int i = 0; i<size; i++){
            view1[i] = array[srcPos+i];
        }
        for(int i = 0; i<size; i++){
            view2[i] = dst[i];
        }
        assertArrayEquals(view1, view2);
    }

    @Test
    void reduce() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = i+=100;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        int start = 50;
        int size = 50;
        double[] dst = new double[10];
        double over = sdc.reduce(dst, sdc, start, size);
        double view = 5;
        assertEquals(view, over);
    }

    @Test
    void dynamicReduce() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = i+=100;
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        int start = 50;
        int size = 50;
        double[] dst = new double[10];
        double over = ddc.reduce(dst, ddc, start, size);
        double view = 5;
        assertEquals(view, over);
    }

    @Test
    void reduce_pow() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = i+=100;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        int start = 50;
        int size = 50;
        double[] dst = new double[10];
        double over = sdc.reduce_pow(dst, sdc, start, size);
        double view = 5;
        assertEquals(view, over);

    }

    @Test
    void dynamicReduce_pow() {
        double[] array = new double[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = i += 100;
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        int start = 50;
        int size = 50;
        double[] dst = new double[10];
        double over = ddc.reduce(dst, ddc, start, size);
        double view = 5;
            assertEquals(view, over);
        }

    @Test
    void powerate() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        double[] dst = new double[10];
        int x1 = 0;
        int x2 = 99;
        int i1 = 0;
        int i2 = 1;
        sdc.powerate(dst, sdc, i1, i2, x1, x2);
        double max = array[0];
        double min = array[0];
        for (int i = 0; i< array.length;i++){
            max = Math.max(max, array[i]);
            min = Math.min(min, array[i])   ;
        }
        System.out.println(max + " "+ dst[0]+" "+ min+" "+ dst[1]);

    }

    @Test
    void dynamicPowerate() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = i;
        }
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        double[] dst = new double[10];
        int x1 = 0;
        int x2 = 99;
        int i1 = 0;
        int i2 = 1;
        ddc.powerate(dst, ddc, i1, i2, x1+16, x2+16);
        double max = array[0];
        double min = array[0];
        for (int i = 0; i< array.length;i++){
            max = Math.max(max, array[i]);
            min = Math.min(min, array[i])   ;
        }
        System.out.println(max + " "+ dst[0]+" "+ min+" "+ dst[1]);

    }
}