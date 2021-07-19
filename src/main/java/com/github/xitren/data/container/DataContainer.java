package com.github.xitren.data.container;

import org.jetbrains.annotations.NotNull;

public abstract class DataContainer {
    public abstract int length();
    public abstract double get(int i);
    public abstract void lastblock(double[] data, int size);
    public abstract double[] lastblock(int size);
    public abstract void add(@NotNull double[] data);
    public abstract void add(@NotNull int[] data);
    public abstract void add(@NotNull long[] data);
    public abstract void datacopy(int srcPos, @NotNull double[] dest, int destPos, int size);
    public abstract void cut(int start, int size);
    public abstract DataContainer clone();

    /**
     * Функция создания массива по данным из {@link DataContainer}
     * @return возвращает полученный массив данных
     */
    @NotNull
    public static double[] toArray(@NotNull DataContainer src) {
        double[] fill = new double[src.length()];
        src.datacopy(0, fill, 0, fill.length);
        return fill;
    }

    /**
     * Функция создания массива по отрезку данных из {@link DataContainer}
     */
    public static void datacopy(@NotNull DataContainer src, int srcPos, @NotNull double[] dest, int destPos, int size) {
        src.datacopy(srcPos, dest, destPos, size);
    }

    /**
     * Функция разбиения отрезка контейнера {@link DataContainer}  на усреднённые отрезки
     * @param dst - массив для записи максимального и минимального значений
     * @param src - контейнер {@link DataContainer} в котором происходит поиск
     * @param start - номер начального элемента разбиваемого отрезка
     * @param size - размер разбиваемого отрезка1
     * @return вызывет метод reduce_pow для отрезка контейнера
     */
    public static double reduce(@NotNull double[] dst, @NotNull DataContainer src, int start, int size) {
        return reduce_pow(dst, src, start, size);
    }

    /**
     * Функция разбиения контейнера {@link DataContainer} на усреднённые отрезки
     * @param dst - массив для записи максимального и минимального значений
     * @param src - контейнер {@link DataContainer} в котором происходит поиск
     * @return вызывет метод reduce_pow для всего контейнера
     */
    public static double reduce(@NotNull double[] dst, @NotNull DataContainer src) {
        return reduce_pow(dst, src);
    }

    /**
     * Функция разбиения контейнера {@link DataContainer} на усреднённые отрезки
     * @param dst - массив для записи максимального и минимального значений
     * @param src - массив в котором происходит поиск
     * @return вызывет метод reduce_pow для всего контейнера
     */
    public static double reduce(@NotNull double[] dst, @NotNull double[] src) {
        return reduce_pow(dst, src);
    }

    /**
     * Функция разбиения отрезка контейнера {@link DataContainer}  на усреднённые отрезки
     * @param dst - массив для записи максимального и минимального значений
     * @param src - контейнер {@link DataContainer} в котором происходит поиск
     * @param start - номер начального элемента разбиваемого отрезка
     * @param size - размер разбиваемого отрезка1
     * @return возвращает примерное количество получившихся отрезков
     */
    public static double reduce_pow(@NotNull double[] dst, DataContainer src, int start, int size) {
        double over = size / (double)dst.length;
        for (int i = 1; i < dst.length; i+=2) {
            int x2 = start + (int)Math.round((double)i * over);
            int x1 = start + (int)Math.round((double)(i - 1) * over);
            powerate(dst, src, i - 1, i, x1, x2);
        }
        return over;
    }

    /**
     * Функция разбиения контейнера {@link DataContainer} на усреднённые отрезки
     * @param dst - массив для записи максимального и минимального значений
     * @param src - контейнер {@link DataContainer} в котором происходит поиск
     * @return возвращает примерное количество получившихся отрезков
     */
    public static double reduce_pow(@NotNull double[] dst, @NotNull DataContainer src) {
        double over = (double)src.length() / (double)dst.length;
        for (int i = 1; i < dst.length; i+=2) {
            int x2 = (int)Math.round((double)i * over);
            int x1 = (int)Math.round((double)(i - 1) * over);
            powerate(dst, src, i - 1, i, x1, x2);
        }
        return over;
    }

    /**
     * Функция разбиения контейнера {@link DataContainer} на усреднённые отрезки
     * @param dst - массив для записи максимального и минимального значений
     * @param src - массив в котором происходит поиск
     * @return возвращает примерное количество получившихся отрезков
     */
    public static double reduce_pow(@NotNull double[] dst, @NotNull double[] src) {
        double over = (double)src.length / (double)dst.length;
        for (int i = 1; i < dst.length; i+=2) {
            int x2 = (int)Math.round((double)i * over);
            int x1 = (int)Math.round((double)(i - 1) * over);
            powerate(dst, src, i - 1, i, x1, x2);
        }
        return over;
    }

    /**
     * Функция поиска максимального и минимального значения в {@link DataContainer} от элемента с номером х1 до элемента с номером х2
     * @param dst - массив для записи максимального и минимального значений
     * @param src - массив в котором происходит поиск
     * @param i1 - номер элемента массива src куда будет помещено одно из значений
     * @param i2 - номер элемента массива src куда будет помещено одно из значений
     * @param x1 - начальный номер элемента массива, обозначающий начало отрезка перебора
     * @param x2 - начальный номер элемента массива, обозначающий конец отрезка перебора
     */
    public static void powerate(@NotNull double[] dst, @NotNull double[] src, int i1, int i2, int x1, int x2) {
        double st = src[x1];
        double end = src[x2];
        double min = st;
        double max = st;
        for (int i = x1; i <= x2; i++) {
            double ii = src[i];
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

    /**
     * Функция поиска максимального и минимального значения в {@link DataContainer} от элемента с номером х1 до элемента с номером х2
     * @param dst - массив для записи максимального и минимального значений
     * @param src - контейнер {@link DataContainer} в котором происходит поиск
     * @param i1 - номер элемента массива src куда будет помещено одно из значений
     * @param i2 - номер элемента массива src куда будет помещено одно из значений
     * @param x1 - начальный номер элемента контейнера {@link DataContainer}, обозначающий начало отрезка перебора
     * @param x2 - начальный номер элемента контейнера {@link DataContainer}, обозначающий конец отрезка перебора
     */
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
