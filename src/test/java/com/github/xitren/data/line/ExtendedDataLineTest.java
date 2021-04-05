package com.github.xitren.data.line;

import com.github.xitren.data.FIR;
import com.github.xitren.data.container.DynamicDataContainer;
import com.github.xitren.data.container.StaticDataContainer;
import com.github.xitren.data.window.WindowDynamicParser;
import com.github.xitren.data.window.WindowSource;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExtendedDataLineTest {

    @Test
    void calculateSimpleView() {
        double[] array = new double[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        ExtendedDataLine dl = new ExtendedDataLine(sdc, str);
        //dl.checkView(0,500);
        dl.calculateSimpleView();

    }

    @Test
    void clearParsers() {
        WindowDynamicParser wdp = new WindowDynamicParser(WindowSource.FILTERED){
            @Override
            public void setData(double[] data, double[] timeline) {
            }
        };
        double[] array = new double[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        ExtendedDataLine dl = new ExtendedDataLine(sdc, str);
        Set<WindowDynamicParser> view = new HashSet<>();
        dl.addParser(wdp);
        dl.clearParsers();
        assertEquals(view, dl.parsers);
    }

    @Test
    void addParser() {
        WindowDynamicParser wdp = new WindowDynamicParser(WindowSource.FILTERED){
            @Override
            public void setData(double[] data, double[] timeline) {
            }
        };
        double[] array = new double[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        ExtendedDataLine dl = new ExtendedDataLine(sdc, str);
        Set<WindowDynamicParser> view = new HashSet<>();
        dl.addParser(wdp);
        view.add(wdp);
        assertEquals(view, dl.parsers);
    }

    @Test
    void removeParser() {
        WindowDynamicParser wdp = new WindowDynamicParser(WindowSource.FILTERED){
            @Override
            public void setData(double[] data, double[] timeline) {
            }
        };
        WindowDynamicParser wdp1 = new WindowDynamicParser(WindowSource.FILTERED){
            @Override
            public void setData(double[] data, double[] timeline) {
            }
        };
        double[] array = new double[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        StaticDataContainer sdc = new StaticDataContainer(array);
        String str = " name ";
        ExtendedDataLine dl = new ExtendedDataLine(sdc, str);
        Set<WindowDynamicParser> view = new HashSet<>();
        dl.addParser(wdp);
        view.add(wdp);
        dl.addParser(wdp1);
        view.add(wdp1);
        dl.removeParser(wdp1);
        view.remove(wdp1);
        assertEquals(view, dl.parsers);
    }

    @Test
    void add() {
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        double[] view = new double[1000];
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        String str = " name ";
        ExtendedDataLine dl = new ExtendedDataLine(ddc, str);
        dl.add(view);
        for (int i = 0; i<array.length; i++) {
            System.out.println(array[i]);
            System.out.println(view[i]);
        }
    }

    @Test
    void testAdd() {
        FIR filter = new FIR(new double[]{2});
        double[] array = new double[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        double[] view = new double[1000];
        DynamicDataContainer ddc = new DynamicDataContainer();
        ddc.add(array);
        String str = " name ";
        ExtendedDataLine dl = new ExtendedDataLine(ddc, str);
        dl.setFilter(filter);


    }

    @Test
    void addMode() {
    }

    @Test
    void removeMode() {
    }

    @Test
    void clearModes() {
    }

    @Test
    void getModes() {
    }
}