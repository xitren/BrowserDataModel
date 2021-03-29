package io.github.xitren.data.container;

import org.jetbrains.annotations.NotNull;

public class StaticDataContainer extends DataContainer {
    private double[] dataArray;

    /**
     * Конструктор - создание нового объекта
     */
    public StaticDataContainer() {
        this.dataArray = new double[0];
    }

    /**
     * Конструктор - создание нового объекта
     * @param dataArray - массив данных
     */
    public StaticDataContainer(@NotNull double[] dataArray) {
        this.dataArray = dataArray;
    }

    /**
     * Функция получения длины массива данных из поля {@link dataArray}
     * @return возвращает длину массива в виде int
     */
    @Override
    public int length() {
        return dataArray.length;
    }

    /**
     * Функция получения элемента массива данных из поля {@link dataArray}
     * @param i - номер искомого элемента массива
     * @return возвращает значение элемента массива
     */
    @Override
    public double get(int i) {
        return dataArray[i];
    }

    /**
     * Функция записи последнего блока массива данных размером size в другой массив
     * @param doubles - массив в который будет происходить копирование
     * @param size - необходимый размер блока
     */
    @Override
    public void lastblock(double[] doubles, int size) {
        System.arraycopy(dataArray, dataArray.length - size, doubles, 0, size);
    }

    /**
     * Функция получения последнего блока массива данных размером size
     * @param size - необходимый размер блока
     * @return возвращает полученный блок в виде массива
     */
    @Override
    public double[] lastblock(int size) {
        double[] doubles = new double[size];
        System.arraycopy(dataArray, dataArray.length - size, doubles, 0, size);
        return doubles;
    }

    /**
     * Функция наполнения поля {@link dataArray}
     * @param data - массив данных, помещаемый в поле
     */
    @Override
    public void add(double[] data) {
        this.dataArray = data;
    }

    /**
     * Функция наполнения поля {@link dataArray} массивом типа int[]
     * @param data - массив данных тип int[], помещаемый в поле
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
     * Функция наполнения поля {@link dataArray} массивом типа long[]
     * @param data - массив данных тип long[], помещаемый в поле
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
        System.arraycopy(dataArray, srcPos, dest, destPos, size);
    }

    /**
     * Функция вырезания данных из поля {@link dataArray} и замены значения данного поля на вырезанный блок
     * @param start - начальная позиция вырезания блока данных
     * @param size - необходимый размер блока
     */
    @Override
    public void cut(int start, int size) {
        double[] replace = new double[size];
        System.arraycopy(dataArray, start, replace, 0, size);
        this.dataArray = replace;
    }

    /**
     * Функция создаёт новый пустой объект {@link StaticDataContainer}
     */
    @Override
    public DataContainer clone() {
        return new StaticDataContainer();
    }
}
