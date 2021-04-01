package com.github.xitren.data.data.container;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class DynamicDataContainer extends DataContainer {
    final private ArrayList<double[]> dataArray;

    private int overall_size = 0;
    private Iterator<double[]> ins = null;
    private int start_block = -1;
    private int end_block = -1;
    private double[] block = null;

    /**
     * Конструктор - создание нового объекта и присваивание полю {@link dataArray} массив длинной 16
     */
    public DynamicDataContainer() {
        dataArray = new ArrayList<>();
        add(new double[16]);
        end_block = start_block = 0;
    }

    /**
     * Функция получения длины поля {@link dataArray}
     * @return возвращает длину записанную в поле {@link overall_size}
     */
    @Override
    public int length() {
        return overall_size;
    }

    @Override
    public double get(int i) {
        if (i >= overall_size)
            throw new ArrayIndexOutOfBoundsException();
        int s;
        Double gh = null;
        synchronized (this) {
            if ((i < start_block) || (ins == null)) {
                ins = dataArray.listIterator();
                end_block = start_block = -1;
            }
            if (end_block <= i) {
                for (int j = 0; ins.hasNext(); j++) {
                    block = ins.next();
                    if (end_block == -1)
                        start_block = 0;
                    else
                        start_block = end_block;
                    end_block = start_block + block.length;
                    if (((start_block <= i) && (i < end_block))) {
                        try {
                            gh = block[i - start_block];
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            System.out.println(ex);
                        }
                        break;
                    }
                }
            } else {
                try {
                    gh = block[i - start_block];
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.out.println(ex);
                }
            }
            s = i - start_block;
        }
        if (gh == null)
            System.out.println("ex");
        return gh;
    }

    /**
     * Функция записи последнего блока данных размером size в массив
     * @param doubles - массив в который будет происходить копирование
     * @param size - необходимый размер блока
     */
    @Override
    public void lastblock(double[] doubles, int size) {
        synchronized (this) {
            if (dataArray.size() > 0) {
                int i = dataArray.size() - 1;
                while (size > 0 && i > 0) {
                    double[] iter = dataArray.get(i--);
                    int k = iter.length - 1;
                    while (k >= 0 && size > 0) {
                        doubles[--size] = iter[k--];
                    }
                }
            }
        }
    }

    /**
     * Функция получения последнего блока данных размером size
     * @param size - необходимый размер блока
     * @return возвращает полученный блок в виде массива
     */
    @Override
    public double[] lastblock(int size) {
        synchronized (this) {
            if (dataArray.size() > 0) {
                int i = dataArray.size() - 1;
                double[] doubles = new double[size];
                while (size > 0 && i > 0) {
                    double[] iter = dataArray.get(i--);
                    int k = iter.length - 1;
                    while (k >= 0 && size > 0) {
                        doubles[--size] = iter[k--];
                    }
                }
                return doubles;
            } else
                return null;
        }
    }

    /**
     * Функция добавления данных в поле {@link dataArray}
     * @param data - массив данных, помещаемый в поле
     */
    @Override
    public void add(@NotNull double[] data) {
        synchronized (this) {
            ins = null;
            dataArray.add(data);
            overall_size += data.length;
        }
    }

    /**
     * Функция добавления данных в поле {@link dataArray} массивом типа int[]
     * @param data - массив данных, помещаемый в поле
     */
    @Override
    public void add(@NotNull int[] data) {
        double[] doubles = new double[data.length];
        for(int i=0;i < data.length;i++) {
            doubles[i] = data[i];
        }
        add(doubles);
    }

    /**
     * Функция добавления данных в поле {@link dataArray} массивом типа long[]
     * @param data - массив данных, помещаемый в поле
     */
    @Override
    public void add(@NotNull long[] data) {
        double[] doubles = new double[data.length];
        for(int i=0;i < data.length;i++) {
            doubles[i] = data[i];
        }
        add(doubles);
    }

    /**
     * Функция копирования данных из поля {@link dataArray} в массив dest
     * @param srcPos - начальная позиция копируемого блока данных
     * @param dest - массив принимающий скопированные данные
     * @param destPos - начальная позиция для вставки копируемого блока данных
     * @param size - необходимый размер блока
     */
    @Override
    public void datacopy(int srcPos, double[] dest, int destPos, int size) {
        if (srcPos >= overall_size && (destPos + size <= dest.length))
            throw new ArrayIndexOutOfBoundsException();
        synchronized (this) {
            Iterator<double[]> ins = dataArray.iterator();
            final int srcPosEnd = srcPos + size - 1;
            int start_block = 0;
            int end_block = 0;
            double[] block;
            int i = 0;
            do {
                if (ins.hasNext()) {
                    start_block = end_block;
                    block = ins.next();
                    end_block = start_block + block.length;
                    if ((start_block <= srcPos) && (srcPos < end_block)) {
                        int srcPosRel = srcPos - start_block;
                        if ((block.length - srcPosRel) < size) {
                            System.arraycopy(block, srcPosRel, dest, 0, block.length - srcPosRel);
                            i += block.length - srcPosRel;
                        } else {
                            System.arraycopy(block, srcPosRel, dest, 0, size);
                            i += size;
                        }
                    } else if ((srcPos < start_block) && (end_block <= srcPosEnd)) {
                        System.arraycopy(block, 0, dest, i, block.length);
                        i += block.length;
                    } else if ((srcPos < start_block) &&
                            (start_block <= srcPosEnd) && (srcPosEnd < end_block)) {
                        int srcPosRel = srcPosEnd - start_block;
                        System.arraycopy(block, 0, dest, i, srcPosRel + 1);
                        i += srcPosRel + 1;
                    }
                } else
                    throw new ArrayIndexOutOfBoundsException();
            } while (i < size);
        }
    }

    /**
     * Функция пересчёта длины поля {@link dataArray} и пререзаписи значения в поле {@link overall_size}
     * @return возвращает длину записанную в поле {@link overall_size}
     */
    private int allLength() {
        Iterator<double[]> ins = dataArray.iterator();
        overall_size = 0;
        while (ins.hasNext()) {
            block = ins.next();
            overall_size += block.length;
        }
        return overall_size;
    }

    /**
     * Функция вырезает блоки, не входящие в промежуток от start до (start + size)
     * @param start - номер элемента с которого начинается вырезание
     * @param size - размер вырезаемого блока
     */
    @Override
    public void cut(int start, int size) {
        synchronized (this) {
            int start_block = 0;
            this.ins = null;
            int end_block = 0;
            int end = start + size;
            double[] block;
            Iterator<double[]> ins = dataArray.iterator();
            ArrayList<double[]> dataToDelete = new ArrayList<>();
            while (ins.hasNext()) {
                start_block = end_block;
                block = ins.next();
                end_block = start_block + block.length;
                if (!((start <= start_block) && (end_block <= end))) {
                    dataToDelete.add(block);
                }
            }
            dataArray.removeAll(dataToDelete);
            allLength();
        }
    }

    /**
     * Функция создаёт новый пустой объект {@link DynamicDataContainer}
     */
    @Override
    public DataContainer clone() {
        return new DynamicDataContainer();
    }
}
