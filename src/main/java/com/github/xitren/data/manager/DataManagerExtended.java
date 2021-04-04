package com.github.xitren.data.manager;

import com.github.xitren.data.FIR;
import com.github.xitren.data.line.ExtendedDataLine;
import com.github.xitren.data.container.DataContainer;
import org.jetbrains.annotations.NotNull;

public class DataManagerExtended<V extends ExtendedDataLine<T>, T extends DataContainer> extends DataManagerFile<V, T> {
    private String personDate;
    private double discretisation = 250;
    private double timePeriod = 0.004;

    public DataManagerExtended(V[] edl) {
        super(edl);
    }

    public void cut(int start, int size) {
        synchronized (dataLines) {
            for (int i = 0; i < dataLines.length; i++) {
                dataLines[i].cut(start, size);
            }
        }
    }

    public void setFilterGlobal(@NotNull double[] data) {
        synchronized (dataLines) {
            for (int i=0;i < dataLines.length;i++) {
                dataLines[i].setFilter(new FIR(data));
            }
        }
    }

    public void addData(@NotNull double[][] data) {
        if (dataLines.length != data.length)
            throw new IndexOutOfBoundsException("Wrong index!");
        synchronized (dataLines) {
            for (int i = 0;i < dataLines.length;i++) {
                dataLines[i].add(data[i]);
            }
        }
    }

    public void addData(@NotNull long[][] data) {
        if (dataLines.length != data.length)
            throw new IndexOutOfBoundsException("Wrong index!");
        synchronized (dataLines) {
            for (int i = 0;i < dataLines.length;i++) {
                dataLines[i].add(data[i]);
            }
        }
    }

    public double getDiscretization() {
        return discretisation;
    }

    public void setDiscretization(int discretization) {
        this.discretisation = discretization;
        this.timePeriod = 1 / discretization;
    }

    public double getTimePeriod() {
        return timePeriod;
    }
}
