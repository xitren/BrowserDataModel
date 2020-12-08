package com.gusev.data;

import com.gusev.data.offline.StaticDataContainer;
import edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D;
import org.jetbrains.annotations.NotNull;

public class ExtendedDataLine<T extends DataContainer> extends DataLine<T> {
    protected final double[] dctView = new double[OVERVIEW_SIZE];
    protected final double[] rmsView = new double[OVERVIEW_SIZE];
    protected final double[] filterDataViewPrep = new double[OVERVIEW_SIZE + FILTER_ORDER];
    protected int rmsWindow = 30;
    protected FIR filter = null;
    protected DataContainer dataArrayFiltered;
    private Mode mode;

    public ExtendedDataLine(@NotNull T _data) {
        super(_data);
        mode = Mode.USUAL;
    }

    public void add(@NotNull double[] datum) {
        if (dataArray instanceof StaticDataContainer)
            return;
        dataArray.add(datum);
        if (filter != null) {
            dataArrayFiltered.add(filter.process(datum));
        }
        calculateOverview();
    }

    public void add(@NotNull int[] datum) {
        double[] doubles = new double[datum.length];
        for(int i=0;i < datum.length;i++) {
            doubles[i] = datum[i];
        }
        add(doubles);
    }

    public void add(@NotNull long[] datum) {
        double[] doubles = new double[datum.length];
        for(int i=0;i < datum.length;i++) {
            doubles[i] = datum[i];
        }
        add(doubles);
    }

    public enum Mode {
        USUAL, FOURIER, FILTER, POWER
    }

    public void setMode(Mode def) {
        this.mode = def;
    }

    public Mode getMode() {
        return mode;
    }

    public void setRmsWindow(int rmsWindow) {
        this.rmsWindow = rmsWindow;
    }

    public int getRmsWindow() {
        return rmsWindow;
    }

    public FIR getFilter() {
        return filter;
    }

    public void setFilter(@NotNull FIR filter) {
        if (filter == null)
            return;
        this.filter = filter;
        dataArrayFiltered = dataArray.clone();
        double[] fill = DataContainer.toArray(dataArray);
        fill = this.filter.process(fill);
        dataArrayFiltered.add(fill);
        calculateView(view[0], view[1]);
    }

    protected void calculateFourierView() {
        calculateReducedView();
        double multer = OVERVIEW_SIZE / ((double) (view[1] - view[0]));
        DoubleDCT_1D dct = new DoubleDCT_1D(this.dataView.length);
        System.arraycopy(dataView, 0, dctView, 0, dataView.length);
        dct.forward(dctView, true);
        dctView[0] = 0;
        for (int i = 0; i < dctView.length; i++) {
            dataView[i] = Math.abs(dctView[i]) / 1;
            timeView[i] = ( ((double)i * discretisation)) / ((double) (view[1] - view[0]) * 2);
            timeView[i] = timeView[i];
        }
        activeView = dctView.length / 2;
    }

    protected void calculateReducedFilterView() {
        if (filter == null) {
            calculateReducedView();
            return;
        }
        activeView = OVERVIEW_SIZE;
        double multer = OVERVIEW_SIZE / ((double) (view[1] - view[0]));
        int start_d = view[0] - VIEW_PREP_SIZE;
        if (start_d < 0)
            start_d = 0;
        int size_d = (int) ((view[1] - start_d) * multer);
        if (size_d < OVERVIEW_SIZE)
            size_d = OVERVIEW_SIZE;
        discretisationView = discretisation * multer;
        double timeMultiplicand = DataContainer.reduce(dataViewPrep, dataArrayFiltered, start_d, view[1] - start_d);
        System.arraycopy(dataViewPrep, dataViewPrep.length - OVERVIEW_SIZE, dataView, 0, OVERVIEW_SIZE);
        for (int i = 0; i < timeView.length; i++) {
            timeView[i] = (view[0] + (i) / multer);
        }
    }

    protected void calculateRMSView() {
        calculateReducedView();
        for (int i = 0; i < rmsView.length; i++) {
            rmsView[i] = 0;
        }
        for (int i = 0; i < rmsView.length; i++) {
            int cnt = 0;
            for (int k = -rmsWindow + 1; k < rmsWindow; k++) {
                if ( (0 <= (i + k)) && ((i + k) < dataView.length) ) {
                    rmsView[i] += dataView[(i + k)] * dataView[(i + k)];
                    cnt++;
                }
            }
            rmsView[i] = Math.sqrt(rmsView[i] / cnt);
        }
        System.arraycopy(rmsView, 0, dataView, 0, rmsView.length);
    }

    @Override
    protected void calculateView(int start, int end) {
        checkView(start, end);
        int ss = (view[1] - view[0]);
        if (ss >= (OVERVIEW_SIZE)) {
            if (mode != null) {
                switch (mode) {
                    case FOURIER:
                        calculateFourierView();
                        break;
                    case FILTER:
                        calculateReducedFilterView();
                        break;
                    case POWER:
                        calculateRMSView();
                        break;
                    case USUAL:
                    default:
                        calculateReducedView();
                        break;
                }
            } else
                calculateReducedView();
        } else {
            if (mode != null) {
                switch (mode) {
                    case FILTER:
                        calculateSimpleFilterView();
                        break;
                    case FOURIER:
                    case POWER:
                    case USUAL:
                    default:
                        calculateSimpleView();
                        break;
                }
            } else
                calculateSimpleView();
        }
    }

    protected void calculateSimpleFilterView() {
        discretisationView = discretisation;
        activeView = view[1] - view[0];
        DataContainer.datacopy(dataArrayFiltered, view[0], dataView, 0, view[1] - view[0]);
        for (int i = 0; i < (view[1] - view[0]); i++) {
            timeView[i] = view[0] + i;
        }
    }
}
