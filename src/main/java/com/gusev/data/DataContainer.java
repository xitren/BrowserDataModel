package com.gusev.data;

public abstract class DataContainer {
    public abstract int length();
    public abstract double get(int i);
    public abstract void add(double[] data);
    public abstract void datacopy(int srcPos, double[] dest, int destPos, int size);
    public abstract void cut(int start, int size);
    public abstract DataContainer clone();

    public static double[] toArray(DataContainer src) {
        double[] fill = new double[src.length()];
        src.datacopy(0, fill, 0, src.length());
        return fill;
    }

    public static void datacopy(DataContainer src, int srcPos, double[] dest, int destPos, int size) {
        src.datacopy(srcPos, dest, destPos, size);
    }

    public static double reduce(double[] dst, DataContainer src, int start, int size) {
        double over = size / (double)dst.length;
        for (int i = 0; i < dst.length; i++) {
            dst[i] = src.get(start + (int)Math.round((double)i * over));
        }
        return over;
    }

    public static double reduce_pow(double[] dst, DataContainer src) {
        double over = (double)src.length() / (double)dst.length;
        for (int i = 1; i < dst.length; i+=2) {
            int x2 = (int)Math.round((double)i * over);
            int x1 = (int)Math.round((double)(i - 1) * over);
            powerate(dst, src, i - 1, i, x1, x2);
        }
        return over;
    }

    public static void powerate(double[] dst, DataContainer src, int i1, int i2, int x1, int x2) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int i = x1; i <= x2; i++) {
            min = Math.min(src.get(i), min);
            max = Math.max(src.get(i), max);
        }
        dst[i1] = max;
        dst[i2] = min;
    }

    public static double reduce(double[] dst, DataContainer src) {
        double over = (double)src.length() / (double)dst.length;
        for(int i=0;i < dst.length;i++) {
            dst[i] = src.get((int)Math.round((double)i * over));
        }
        return over;
    }
}
