package com.github.xitren.data.line;

import com.github.xitren.data.container.DataContainer;
import org.jetbrains.annotations.NotNull;

public class DataLine<T extends DataContainer> {
    private final String name;
    public final static int OVERVIEW_SIZE = 2048;
    protected final static int VIEW_PREP_SIZE = 100;
    protected final static int FILTER_ORDER = 30;
    protected final T dataArray;
    protected final double[][] overview = new double[3][OVERVIEW_SIZE];
    protected final double[][] usualView = new double[3][OVERVIEW_SIZE];
    protected final double[] dataViewPrep = new double[OVERVIEW_SIZE + FILTER_ORDER];
    protected final int[] view = new int[2];
    protected boolean overviewActual = false;
    protected boolean viewActual = false;
    protected double discretisation = 250;
    protected double discretisationView = 250;
    protected int activeView = OVERVIEW_SIZE;

    public DataLine(@NotNull T data, String name) {
        dataArray = data;
        this.name = name;
        calculateOverview();
        calculateView(0, dataArray.length());
    }

    /**
     * Функция получения именни
     * @return имя {@link DataLine}
     */
    public String getName() {
        return name;
    }

    /**
     * Функция получения значения viewActual в зависимсоти от занятости view
     * @return viewActual {@link DataLine}
     */
    public boolean isOverviewActual() {
        return overviewActual;
    }

    /**
     * Функция заполнения массива номерами первого и последнего элементов отрезка
     */
    protected void checkView(int start, int end) {
        if ((end - start) < 0) {
            view[0] = end;
            view[1] = start;
        } else {
            view[0] = start;
            view[1] = end;
        }
        if (view[0] < 0)
            view[0] = 0;
        if (view[1] > dataArray.length())
            view[1] = dataArray.length();
    }

    /**
     * Функция считает view для отрезка большего чем OVERVIEW_SIZE и опмещает в usualView[0] значеня, в usualView[1] номера элеиентов, в usualView[2] время
     */
    protected void calculateReducedView() {
        double multer = OVERVIEW_SIZE / ((double) (view[1] - view[0]));
        discretisationView = discretisation * multer * 2;
        double timeMultiplicand = DataContainer.reduce(dataViewPrep, dataArray, view[0], view[1] - view[0]);
        System.arraycopy(dataViewPrep, dataViewPrep.length - OVERVIEW_SIZE, usualView[0], 0, OVERVIEW_SIZE);
        for (int i = 0; i < usualView[1].length; i++) {
            usualView[1][i] = (view[0] + (i) / multer);
            usualView[2][i] = (view[0] + (i) / multer) / discretisation;
        }
    }

    /**
     * Функция считает view для отрезка меньшего чем OVERVIEW_SIZE и помещает в usualView[0] значеня, в usualView[1] номера элеиентов, в usualView[2] время
     */
    protected void calculateSimpleView() {
        discretisationView = discretisation;
        DataContainer.datacopy(dataArray, view[0], usualView[0], 0, view[1] - view[0]);
        int ss = (view[1] - view[0]);
        for (int i = 0; i < ss; i++) {
            usualView[1][i] = (view[0] + i);
            usualView[2][i] = (view[0] + i) / discretisation;
        }
        fillRest(usualView[1], ss);
    }

    public static void fillRest(double[] data, int ss) {
        if (0 < (ss - 1) && ss >= OVERVIEW_SIZE)
            return;
        for (int i = ss; i < OVERVIEW_SIZE; i++) {
            data[i] = data[ss - 1];
        }
    }

    /**
     * Функция выбирает метод подсчёта view в зависимости от входного отрезка
     * @param start - номер начального элемента
     * @param end - номер конечного элемента
     */
    protected void calculateView(int start, int end) {
        checkView(start, end);
        if ((view[1] - view[0]) >= OVERVIEW_SIZE) {
            activeView = OVERVIEW_SIZE;
            calculateReducedView();
        } else {
            activeView = view[1] - view[0];
            calculateSimpleView();
        }
        viewActual = true;
    }

    /**
     * Функция считает view для для всего массива и помещает в overview[0] значеня, в overview[1] номера элеиентов, в overview[2] время
     */
    public synchronized void calculateOverview() {
        if (this.overviewActual == true)
            return;
        if (dataArray.length() >= OVERVIEW_SIZE) {
            double timeMultiplicand = DataContainer.reduce_pow(overview[0], dataArray);
            for (int i = 0; i < overview[1].length; i++) {
                overview[1][i] = (i * timeMultiplicand);
                overview[2][i] = (i * timeMultiplicand) / discretisation;
            }
        } else {
            DataContainer.datacopy(dataArray, 0, overview[0], 0, dataArray.length());
            for (int i = 0; i < overview[1].length; i++) {
                overview[1][i] = (i);
                overview[2][i] = (i) / discretisation;
            }
        }
        this.overviewActual = true;
    }

    /**
     * Функция считает view для отрезка от start до end
     * @param start - номер начального элемента массива
     * @param end - номер конечного элемента массива
     */
    public void setView(int start, int end) {
        calculateView(start, end);
    }

    /**
     * Функция считает длину входящего датаконтейнера
     * @return возвращает длину {@link DataContainer}
     */
    public int getMaxView() {
        return dataArray.length();
    }

    /**
     * Функция позволяет установить свою дискретизацию
     * @param disc - значение задваемое для дискретизации
     */
    public void setDiscretisation(double disc) {
        this.discretisation = disc;
    }

    /**
     * Функция позволяет вернуть даннные в авиде массива
     * @return возвращает макссив данных
     */
    public double[] toArray() {return DataContainer.toArray(this.dataArray);}

    /**
     * Функция позволяет вернуть даннные из usualView
     * @return возвращает макссив данных
     */
    public double[] getDataView(){
        return usualView[0];
    }

    /**
     * Функция позволяет вернуть номера данных из usualView
     * @return возвращает макссив данных
     */
    public double[] getTimeView(){
        return usualView[1];
    }

    /**
     * Функция позволяет вернуть даннные из usualView
     * @return возвращает макссив данных
     */
    public double[] getSecondsView(){
        return usualView[2];
    }

    /**
     * Функция позволяет вернуть время в секундах для каждого элемента из overView
     * @return возвращает макссив данных
     */
    public double[] getDataOverview(){
        return overview[0];
    }

    /**
     * Функция позволяет вернуть номера данных для каждого элемента из overView
     * @return возвращает макссив данных
     */
    public double[] getTimeOverview() {
        return overview[1];
    }

    /**
     * Функция позволяет вернуть время в секундах для каждого элемента из overView
     * @return возвращает макссив данных
     */
    public double[] getSecondsOverview() {
        return overview[2];
    }

    /**
     * Функция позволяет вернуть длину активного View
     * @return возвращает длину используемого view
     */
    public int getActiveView() {
        return activeView;
    }

    /**
     * Функция вырезает отрезок из view от start размером size
     * @param start номер начального элемента отрезка
     * @param size размер вырезаемого отрезка
     */
    public void cut(int start, int size) {
        dataArray.cut(start, size);
        calculateView(0, size);
        this.overviewActual = false;
    }
}
