package com.gusev.data;

import org.jetbrains.annotations.NotNull;

public abstract class DataContainer {
    public abstract int length();
    public abstract double get(int i);
    public abstract double[] lastblock();
    public abstract void add(@NotNull double[] data);
    public abstract void add(@NotNull int[] data);
    public abstract void add(@NotNull long[] data);
    public abstract void datacopy(int srcPos, @NotNull double[] dest, int destPos, int size);
    public abstract void cut(int start, int size);
    public abstract DataContainer clone();

    @NotNull
    public static double[] toArray(@NotNull DataContainer src) {
        double[] fill = new double[src.length()];
        src.datacopy(0, fill, 0, src.length());
        return fill;
    }

    public static void datacopy(@NotNull DataContainer src, int srcPos, @NotNull double[] dest, int destPos, int size) {
        src.datacopy(srcPos, dest, destPos, size);
    }

    public static double reduce(@NotNull double[] dst, @NotNull DataContainer src, int start, int size) {
        return reduce_pow(dst, src, start, size);
//        double over = size / (double)dst.length;
//        for (int i = 0; i < dst.length; i++) {
//            dst[i] = src.get(start + (int)Math.round((double)i * over));
//        }
//        return over;
    }

    public static double reduce(@NotNull double[] dst, @NotNull DataContainer src) {
        return reduce_pow(dst, src);
//        double over = (double)src.length() / (double)dst.length;
//        for(int i=0;i < dst.length;i++) {
//            dst[i] = src.get((int)Math.round((double)i * over));
//        }
//        return over;
    }

    public static double reduce_pow(@NotNull double[] dst, DataContainer src, int start, int size) {
        double over = size / (double)dst.length;
        for (int i = 1; i < dst.length; i+=2) {
            int x2 = start + (int)Math.round((double)i * over);
            int x1 = start + (int)Math.round((double)(i - 1) * over);
            powerate(dst, src, i - 1, i, x1, x2);
        }
        return over;
    }

    public static double reduce_pow(@NotNull double[] dst, @NotNull DataContainer src) {
        double over = (double)src.length() / (double)dst.length;
        for (int i = 1; i < dst.length; i+=2) {
            int x2 = (int)Math.round((double)i * over);
            int x1 = (int)Math.round((double)(i - 1) * over);
            powerate(dst, src, i - 1, i, x1, x2);
        }
        return over;
    }

    public static void powerate(@NotNull double[] dst, @NotNull DataContainer src, int i1, int i2, int x1, int x2) {
        double st = src.get(x1);
        double end = src.get(x2);
        double min = st;
        double max = st;
        for (int i = x1; i <= x2; i++) {
            double ii = src.get(i);
            min = Math.min(ii, min);
            max = Math.max(ii, max);
        }
        if (end < st) {
            dst[i1] = max;
            dst[i2] = min;
        } else {
            dst[i1] = min;
            dst[i2] = max;
        }
    }
}
