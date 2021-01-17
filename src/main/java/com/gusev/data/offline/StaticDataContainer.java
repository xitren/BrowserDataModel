package com.gusev.data.offline;

import com.gusev.data.DataContainer;
import org.jetbrains.annotations.NotNull;

public class StaticDataContainer extends DataContainer {
    private double[] dataArray;

    public StaticDataContainer() {
        this.dataArray = new double[0];
    }

    public StaticDataContainer(@NotNull double[] dataArray) {
        this.dataArray = dataArray;
    }

    @Override
    public int length() {
        return dataArray.length;
    }

    @Override
    public double get(int i) {
        return dataArray[i];
    }

    @Override
    public double[] lastblock() {
        return dataArray;
    }

    @Override
    public void add(double[] data) {
        this.dataArray = data;
    }

    @Override
    public void add(@NotNull int[] data) {
        double[] doubles = new double[data.length];
        for(int i=0;i < data.length;i++) {
            doubles[i] = data[i];
        }
        add(doubles);
    }

    @Override
    public void add(@NotNull long[] data) {
        double[] doubles = new double[data.length];
        for(int i=0;i < data.length;i++) {
            doubles[i] = data[i];
        }
        add(doubles);
    }

    @Override
    public void datacopy(int srcPos, double[] dest, int destPos, int size) {
        System.arraycopy(dataArray, srcPos, dest, destPos, size);
    }

    @Override
    public void cut(int start, int size) {
        double[] replace = new double[size];
        System.arraycopy(dataArray, start, replace, 0, size);
        this.dataArray = replace;
    }

    @Override
    public DataContainer clone() {
        return new StaticDataContainer();
    }
}
