package com.github.xitren.data.line;

import com.github.xitren.data.container.StaticDataContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataLineTest {

    @Test
    void getName() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = new String();
        str =" name ";
        DataLine dl =  new DataLine(sdc, str);
        String name = new String();
        name = dl.getName();
        assertEquals(str, name);
    }

    @Test
    void isOverviewActual() {
        double[] array = new double[100];
        for (int i = 0; i< array.length;i++){
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = new String();
        str =" name ";
        DataLine dl =  new DataLine(sdc, str);
        boolean view1 = dl.isOverviewActual();
        boolean view2 = true;
        assertEquals(view2, view1);
    }
    @Test
    void isOverviewActual2() {
        double[] array = new double[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = new String();
        str = " name ";
        DataLine dl = new DataLine(sdc, str);
        dl.cut(1,2);
        boolean view1 = dl.isOverviewActual();
        boolean view2 = false;
        assertEquals(view2, view1);
    }

    @Test
    void checkView() {
        double[] array = new double[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        int start = -1;
        int end = -2;
        dl.checkView(start, end);
        assertEquals(dl.view[1], sdc.length()-1);
        assertEquals(dl.view[0], 0);
    }

    @Test
    void checkView1() {
        double[] array = new double[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        int start = 10;
        int end = 20;
        dl.checkView(start, end);
        assertEquals(dl.view[1], end);
        assertEquals(dl.view[0], start);
    }

    @Test
    void calculateReducedView() {
        double[] array = new double[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        dl.checkView(0, array.length);
        dl.calculateReducedView();
//        for (int i = 0; i<dl.usualView[1].length; i++){
//            System.out.println(dl.usualView[0][i]);
//            System.out.println(dl.usualView[1][i]);
//            System.out.println(dl.usualView[2][i]);
        //}
    }

    @Test
    void calculateSimpleView() {
            double[] array = new double[100];
            for (int i = 0; i < array.length; i++) {
                array[i] = i;
            }
            StaticDataContainer sdc = new StaticDataContainer(array);
            String str = " name ";
            DataLine dl = new DataLine(sdc, str);
            int start = 5;
            int end = 10;
            dl.checkView(start, end);
            dl.calculateSimpleView();
            for (int i = 0; i<5; i++){
                System.out.println(dl.usualView[0][i]);
            }

    }

    @Test
    void calculateView() {
        double[] array = new double[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        DataLine dl1 = new DataLine(sdc, str);
        dl.checkView(0, array.length);
        dl1.checkView(0, array.length);
        dl.calculateSimpleView();
        double[][] view1 = new double[3][2048];
        for (int i = 0; i < array.length; i++) {
            view1[0][i] = dl.usualView[0][i];
            view1[1][i] = dl.usualView[1][i];
            view1[2][i] = dl.usualView[2][i];
        }
        double[][] view2 = new double[3][2048];
        dl1.calculateView(1, 10);
        for (int i = 0; i < array.length; i++) {
            view2[0][i] = dl1.usualView[0][i];
            view2[1][i] = dl1.usualView[1][i];
            view2[2][i] = dl1.usualView[2][i];
            System.out.println(dl1.usualView[0][i]);
        }
        for (int i = 0; i < array.length; i++) {
            //assertEquals(view2[1][i], view1[1][i]);
        }
    }

    @Test
    void calculateView1() {
        double[] array = new double[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        DataLine dl1 = new DataLine(sdc, str);
        dl.checkView(0, array.length);
        dl1.checkView(0, array.length);
        dl.calculateReducedView();
        dl1.calculateView(0, 10000);
        for (int i = 0; i<2048; i++){
            assertEquals(dl.usualView[1][i], dl1.usualView[1][i]);
        }
    }

    @Test
    void calculateOverview() {
        double[] array = new double[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        dl.calculateOverview();
        for(int i = 0; i<array.length; i++) {
            assertEquals(dl.overview[0][i], array[i]);
        }
    }

    @Test
    void calculateOverview1() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        StaticDataContainer sdc1 = new StaticDataContainer(array);
        String str = " name ";
        double[] view = new double[1000];
        sdc1.reduce_pow(view, sdc1);
        DataLine dl = new DataLine(sdc, str);
        dl.calculateOverview();
        for(int i = 0; i<array.length; i++) {
            assertEquals(dl.overview[0][i], view[i]);
        }
    }

    @Test
    void setView() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        int start = 10;
        int end = 500;
        dl.setView(start, end);
        for (int i = 0; i< (end-start); i++){
            assertEquals(dl.usualView[0][i], array[i+start]);
        }

    }

    @Test
    void getMaxView() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        assertEquals(dl.getMaxView(), sdc.length());
    }

    @Test
    void setDiscretisation() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        double disc = 200;
        dl.setDiscretisation(disc);
        assertEquals(dl.discretisation, disc);
    }

    @Test
    void toArray() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        for (int i = 0; i<array.length; i++){
            assertEquals(dl.toArray()[i], array[i]);
        }

    }

    @Test
    void getDataView() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        dl.calculateView(0, array.length);
        assertEquals(dl.getDataView(), dl.usualView[0]);
    }

    @Test
    void getTimeView() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        dl.calculateView(0, array.length);
        assertEquals(dl.getTimeView(), dl.usualView[1]);
    }


    @Test
    void getSecondsView() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        dl.calculateView(0, array.length);
        assertEquals(dl.getSecondsView(), dl.usualView[2]);
    }


    @Test
    void getDataOverview() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        dl.calculateOverview();
        assertEquals(dl.getDataOverview(), dl.overview[0]);
    }


    @Test
    void getTimeOverview() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        dl.calculateOverview();
        assertEquals(dl.getTimeOverview(), dl.overview[1]);
    }


    @Test
    void getSecondsOverview() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        dl.calculateOverview();
        assertEquals(dl.getSecondsOverview(), dl.overview[2]);
    }


    @Test
    void getActiveView() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        dl.calculateOverview();
        assertEquals(dl.getActiveView(), dl.activeView);
    }


    @Test
    void cut() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        DataLine dl = new DataLine(sdc, str);
        int start = 5;
        int size =5;
        dl.cut(start, size);
        for (int i = 0; i<size; i++){
            System.out.println(dl.usualView[0][i]);
            System.out.println(array[i+start]);
            System.out.println(sdc.get(i));
        }

    }
}