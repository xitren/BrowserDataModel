package io.github.xitren.data.line;

import io.github.xitren.data.container.DataContainer;
import org.jetbrains.annotations.NotNull;

public class DataLine<T extends DataContainer> {
    protected final static int VIEW_PREP_SIZE = 100;
    public final static int OVERVIEW_SIZE = 2048;
    protected final static int FILTER_ORDER = 30;
    protected final T dataArray;
    protected final double[][] overview = new double[2][OVERVIEW_SIZE];
    protected final double[][] usualView = new double[2][OVERVIEW_SIZE];
    protected final double[] dataViewPrep = new double[OVERVIEW_SIZE + FILTER_ORDER];
    protected final int[] view = new int[2];
    protected boolean overviewActual = false;
    protected boolean viewActual = false;
    protected double discretisation = 250;
    protected double discretisationView = 250;
    protected int activeView = OVERVIEW_SIZE;

    public DataLine(@NotNull T _data) {
        dataArray = _data;
        calculateOverview();
        calculateView(0, dataArray.length());
    }

    public boolean isOverviewActual() {
        return overviewActual;
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
        double multer = OVERVIEW_SIZE / ((double) (view[1] - view[0]));
        discretisationView = discretisation * multer * 2;
        double timeMultiplicand = DataContainer.reduce(dataViewPrep, dataArray, view[0], view[1] - view[0]);
        System.arraycopy(dataViewPrep, dataViewPrep.length - OVERVIEW_SIZE, usualView[0], 0, OVERVIEW_SIZE);
        for (int i = 0; i < usualView[1].length; i++) {
            usualView[1][i] = (view[0] + (i) / multer);
        }
    }

    protected void calculateSimpleView() {
        discretisationView = discretisation;
        DataContainer.datacopy(dataArray, view[0], usualView[0], 0, view[1] - view[0]);
        for (int i = 0; i < (view[1] - view[0]); i++) {
            usualView[1][i] = (view[0] + i);
        }
    }

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

    public void calculateOverview() {
        if (this.overviewActual == true)
            return;
        if (dataArray.length() >= OVERVIEW_SIZE) {
            double timeMultiplicand = DataContainer.reduce_pow(overview[0], dataArray);
            for (int i = 0; i < overview[1].length; i++) {
                overview[1][i] = (i * timeMultiplicand);
            }
        } else {
            DataContainer.datacopy(dataArray, 0, overview[0], 0, dataArray.length());
            for (int i = 0; i < overview[1].length; i++) {
                overview[1][i] = (i);
            }
        }
        this.overviewActual = true;
    }

    public void setView(int start, int end) {
        try {
            calculateView(start, end);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println(ex);
        }
    }

    public int getMaxView() {
        return dataArray.length();
    }

    public void setDiscretisation(double disc) {
        this.discretisation = disc;
    }

    public double[] toArray() {return DataContainer.toArray(this.dataArray);}

    public double[] getDataView(){
        return usualView[0];
    }

    public double[] getTimeView(){
        return usualView[1];
    }

    public double[] getDataOverview(){
        return overview[0];
    }

    public double[] getTimeOverview() {
        return overview[1];
    }

    public int getActiveView() {
        return activeView;
    }

    public void cut(int start, int size) {
        dataArray.cut(start, size);
        calculateView(0, size);
        this.overviewActual = false;
    }
}
