package com.gusev.data.online;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DynamicDataContainerTest {
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
        assertArrayEquals(view, view2);
    }
}
