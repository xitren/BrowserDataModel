package com.gusev.data;

import org.jetbrains.annotations.NotNull;

public class DataLine<T extends DataContainer> {
    protected final static int VIEW_PREP_SIZE = 100;
    public final static int OVERVIEW_SIZE = 2048;
    protected final static int FILTER_ORDER = 30;
    protected final T dataArray;
    private final double[] dataOverview = new double[OVERVIEW_SIZE];
    private final double[] timeOverview = new double[OVERVIEW_SIZE];
    protected final double[] dataView = new double[OVERVIEW_SIZE];
    protected final double[] timeView = new double[OVERVIEW_SIZE];
    protected final double[] dataViewPrep = new double[OVERVIEW_SIZE + FILTER_ORDER];
    protected final int[] view = new int[2];
    private boolean overviewActual = false;
    private boolean viewActual = false;
    protected double discretisation = 250;
    protected double discretisationView = 250;
    protected double timePeriod = 0.004;
    protected double timePeriodView = 0.004;
    protected int activeView = OVERVIEW_SIZE;

    public DataLine(@NotNull T _data) {
        dataArray = _data;
        calculateOverview();
        calculateView(0, dataArray.length());
    }

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
        if (view[1] >= dataArray.length())
            view[1] = dataArray.length() - 1;
    }

    protected void calculateReducedView() {
        activeView = OVERVIEW_SIZE;
        double multer = OVERVIEW_SIZE / ((double) (view[1] - view[0]));
        int start_d = view[0] - VIEW_PREP_SIZE;
        if (start_d < 0)
            start_d = 0;
        int size_d = (int) ((view[1] - start_d) * multer);
        if (size_d < OVERVIEW_SIZE)
            size_d = OVERVIEW_SIZE;
        discretisationView = discretisation * multer;
        double timeMultiplicand = DataContainer.reduce(dataViewPrep, dataArray, start_d, view[1] - start_d);
        System.arraycopy(dataViewPrep, dataViewPrep.length - OVERVIEW_SIZE, dataView, 0, OVERVIEW_SIZE);
        for (int i = 0; i < timeView.length; i++) {
            timeView[i] = (view[0] + (i) / multer) * timePeriodView;
        }
    }

    protected void calculateSimpleView() {
        discretisationView = discretisation;
        activeView = view[1] - view[0];
        DataContainer.datacopy(dataArray, view[0], dataView, 0, view[1] - view[0]);
        for (int i = 0; i < (view[1] - view[0]); i++) {
            timeView[i] = (view[0] + i) * timePeriodView;
        }
    }

    protected void calculateView(int start, int end) {
        checkView(start, end);
        if ((view[1] - view[0]) >= OVERVIEW_SIZE) {
            calculateReducedView();
        } else {
            calculateSimpleView();
        }
        viewActual = true;
    }

    protected void calculateOverview() {
        if (dataArray.length() >= OVERVIEW_SIZE) {
            double timeMultiplicand = DataContainer.reduce_pow(dataOverview, dataArray);
            for (int i = 0; i < timeOverview.length; i++) {
                timeOverview[i] = (i * timeMultiplicand) * timePeriod;
            }
        } else {
            DataContainer.datacopy(dataArray, 0, dataOverview, 0, dataArray.length());
            for (int i = 0; i < timeOverview.length; i++) {
                timeOverview[i] = (i) * timePeriod;
            }
        }
        overviewActual = true;
    }

    public void setView(int start, int end) {
        calculateView(start, end);
    }

    public void setDiscretisation(double disc) {
        this.discretisation = disc;
    }

    public DataContainer getDataArray(){
        return dataArray;
    }

    public double[] getDataView(){
        return dataView;
    }

    public double[] getTimeView(){
        return timeView;
    }

    public double[] getDataOverview(){
        return dataOverview;
    }

    public double[] getTimeOverview() {
        return timeOverview;
    }

    public int getActiveView() {
        return activeView;
    }

    public void cut(int start, int size) {
        dataArray.cut(start, size);
    }
}
