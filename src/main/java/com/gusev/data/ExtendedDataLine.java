package com.gusev.data;

import com.gusev.data.offline.StaticDataContainer;
import com.gusev.data.window.op.WindowDynamicParser;
import com.gusev.data.window.op.WindowSource;
import edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExtendedDataLine<T extends DataContainer> extends DataLine<T> {
    private final Set<WindowDynamicParser> parsers = new HashSet<>();
    private final Map<WindowSource, double[][]> modes = new HashMap<>();
    protected final double[][] filterView = new double[2][OVERVIEW_SIZE];
    protected final double[][] dctFilterView = new double[2][OVERVIEW_SIZE];
    protected final double[][] dctView = new double[2][OVERVIEW_SIZE];
    protected final double[][] rmsView = new double[2][OVERVIEW_SIZE];
    protected int rmsWindow = 30;
    protected FIR filter = null;
    protected DataContainer dataArrayFiltered;
    private Mode mode;

    public ExtendedDataLine(@NotNull T _data) {
        super(_data);
        mode = Mode.USUAL;
    }

    public void clearParsers() {
        parsers.clear();
    }

    public void addParser(WindowDynamicParser pars) {
        parsers.add(pars);
    }

    public void removeParser(WindowDynamicParser pars) {
        parsers.remove(pars);
    }

    public void add(@NotNull double[] datum) {
        if (dataArray instanceof StaticDataContainer)
            return;
        dataArray.add(datum);
        if (filter != null) {
            dataArrayFiltered.add(filter.process(datum));
        }
        this.overviewActual = false;
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
        USUAL, FOURIER, FILTER, FILTERED_FOURIER, POWER
    }

    public void setMode(@NotNull Mode def) {
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

    protected void calculateFilteredFourierView() {
        if (modes.containsKey(WindowSource.FILTERED_FREQUENCIES))
            return;
        calculateReducedFilterView();
        double multer = OVERVIEW_SIZE / ((double) (view[1] - view[0]));
        DoubleDCT_1D dct = new DoubleDCT_1D(this.filterView[0].length);
        System.arraycopy(filterView[0], 0, dctFilterView[0], 0, filterView[0].length);
        dct.forward(dctFilterView[0], true);
        dctFilterView[0][0] = 0;
        for (int i = 0; i < dctFilterView[0].length; i++) {
            dctFilterView[0][i] = Math.abs(dctFilterView[0][i]) / 1;
            dctFilterView[1][i] = ( ((double)i * discretisation)) / ((double) (view[1] - view[0]) * 2);
        }
        modes.put(WindowSource.FILTERED_FREQUENCIES, dctFilterView);
    }

    protected void calculateFourierView() {
        if (modes.containsKey(WindowSource.FREQUENCIES))
            return;
        calculateReducedView();
        double multer = OVERVIEW_SIZE / ((double) (view[1] - view[0]));
        DoubleDCT_1D dct = new DoubleDCT_1D(this.usualView[0].length);
        System.arraycopy(usualView[0], 0, dctView[0], 0, usualView[0].length);
        dct.forward(dctView[0], true);
        dctView[0][0] = 0;
        for (int i = 0; i < dctView[0].length; i++) {
            dctView[0][i] = Math.abs(dctView[0][i]) / 1;
            dctView[1][i] = ( ((double)i * discretisation)) / ((double) (view[1] - view[0]) * 2);
        }
        modes.put(WindowSource.FREQUENCIES, dctView);
    }

    protected void calculateReducedFilterView() {
        if (modes.containsKey(WindowSource.FILTERED))
            return;
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
        System.arraycopy(dataViewPrep, dataViewPrep.length - OVERVIEW_SIZE, filterView[0], 0, OVERVIEW_SIZE);
        for (int i = 0; i < filterView[1].length; i++) {
            filterView[1][i] = (view[0] + (i) / multer);
        }
        modes.put(WindowSource.FILTERED, filterView);
    }

    protected void calculateRMSView() {
        if (modes.containsKey(WindowSource.POW))
            return;
        calculateReducedView();
        activeView = OVERVIEW_SIZE;
        double multer = OVERVIEW_SIZE / ((double) (view[1] - view[0]));
        for (int i = 0; i < rmsView[0].length; i++) {
            rmsView[0][i] = 0;
        }
        for (int i = 0; i < rmsView[0].length; i++) {
            int cnt = 0;
            for (int k = -rmsWindow + 1; k < rmsWindow; k++) {
                if ( (0 <= (i + k)) && ((i + k) < usualView[0].length) ) {
                    rmsView[0][i] += usualView[0][(i + k)] * usualView[0][(i + k)];
                    cnt++;
                }
            }
            rmsView[0][i] = Math.sqrt(rmsView[0][i] / cnt);
        }
        for (int i = 0; i < filterView[1].length; i++) {
            rmsView[1][i] = (view[0] + (i) / multer);
        }
        modes.put(WindowSource.POW, rmsView);
    }

    protected void calculateSimpleView(int start, int end) {
        switch (mode) {
            case FILTERED_FOURIER:
                calculateFilteredFourierView();
                break;
            case FOURIER:
                calculateFourierView();
                break;
            case FILTER:
                if (this.filter != null)
                    calculateSimpleFilterView();
                else
                    calculateSimpleView();
                break;
            case POWER:
            case USUAL:
            default:
                calculateSimpleView();
                break;
        }
    }

    protected void calculateSimpleView() {
        if (modes.containsKey(WindowSource.RAW))
            return;
        super.calculateSimpleView();
        modes.put(WindowSource.RAW, usualView);
    }

    protected void calculateReducedView() {
        if (modes.containsKey(WindowSource.RAW))
            return;
        super.calculateReducedView();
        modes.put(WindowSource.RAW, usualView);
    }

    @Override
    protected void calculateView(int start, int end) {
        if (modes == null)
            return;
        checkView(start, end);
        modes.clear();
//        calculateOverview();
        int ss = (view[1] - view[0]);
        if (ss >= (OVERVIEW_SIZE)) {
            switch (mode) {
                case POWER:
                    calculateRMSView();
                    break;
                case USUAL:
                    calculateReducedView();
                    break;
                case FOURIER:
                    calculateFourierView();
                    break;
                case FILTER:
                    if (this.filter != null)
                        calculateReducedFilterView();
                    else
                        calculateReducedView();
                    break;
                case FILTERED_FOURIER:
                    calculateFilteredFourierView();
                    break;
                default:
                    calculateReducedView();
                    break;
            }
        } else {
            calculateSimpleView(start, end);
        }
        for (WindowDynamicParser wdp : parsers) {
            WindowSource ws = wdp.getTypeOfSource();
            if (!modes.containsKey(ws)) {
                switch (ws) {
                    case POW:
                        calculateRMSView();
                        break;
                    case RAW:
                        calculateReducedView();
                        break;
                    case FREQUENCIES:
                        calculateFourierView();
                        break;
                    case FILTERED:
                        if (this.filter != null)
                            calculateReducedFilterView();
                        else
                            calculateReducedView();
                        break;
                    case FILTERED_FREQUENCIES:
                        calculateFilteredFourierView();
                        break;
                    default:
                        calculateReducedView();
                        break;
                }
            }
            double[][] r = modes.get(wdp.getTypeOfSource());
            wdp.setData(r[0], r[1]);
        }
    }

    protected void calculateSimpleFilterView() {
        discretisationView = discretisation;
        activeView = view[1] - view[0];
        DataContainer.datacopy(dataArrayFiltered, view[0], usualView[0], 0, view[1] - view[0]);
        for (int i = 0; i < (view[1] - view[0]); i++) {
            usualView[1][i] = view[0] + i;
        }
    }

    @Override
    public double[] getDataView(){
        switch (mode) {
            case POWER:
                return rmsView[0];
            case FOURIER:
                return dctView[0];
            case FILTER:
                return filterView[0];
            case FILTERED_FOURIER:
                return dctFilterView[0];
            case USUAL:
            default:
                return usualView[0];
        }
    }

    @Override
    public double[] getTimeView(){
        switch (mode) {
            case POWER:
                return rmsView[1];
            case FOURIER:
                return dctView[1];
            case FILTER:
                return filterView[1];
            case FILTERED_FOURIER:
                return dctFilterView[1];
            case USUAL:
            default:
                return usualView[1];
        }
    }

    @Override
    public int getActiveView() {
        switch (mode) {
            case FOURIER:
            case FILTERED_FOURIER:
                return activeView / 2;
            case POWER:
            case FILTER:
            case USUAL:
            default:
                return activeView;
        }
    }

}
