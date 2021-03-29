package io.github.xitren.data.container;
import org.junit.jupiter.api.Assertions;
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
