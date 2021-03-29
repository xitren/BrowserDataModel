package io.github.xitren.data.container;

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
    public void lastblock(double[] doubles, int size) {
        System.arraycopy(dataArray, dataArray.length - size, doubles, 0, size);
    }

    @Override
    public double[] lastblock(int size) {
        double[] doubles = new double[size];
        System.arraycopy(dataArray, dataArray.length - size, doubles, 0, size);
        return doubles;
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
