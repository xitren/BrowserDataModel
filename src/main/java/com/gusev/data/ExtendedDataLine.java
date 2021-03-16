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
    protected final Set<WindowDynamicParser> parsers = new HashSet<>();
    protected final Set<Mode> mode = new HashSet<>();
    protected final Map<WindowSource, double[][]> modes = new HashMap<>();
    protected final double[][] filterView = new double[2][OVERVIEW_SIZE];
    protected final double[][] dctFilterView = new double[2][OVERVIEW_SIZE];
    protected final double[][] dctView = new double[2][OVERVIEW_SIZE];
    protected final double[][] rmsView = new double[2][OVERVIEW_SIZE];
    protected int rmsWindow = 30;
    protected FIR filter = null;
    protected DataContainer dataArrayFiltered;

    public ExtendedDataLine(@NotNull T _data) {
        super(_data);
        mode.add(Mode.USUAL);
        setFilter(new FIR(new double[]{1.}));
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
            double[] ff = new double[datum.length];
            for (int i = 0;i < datum.length;i++)
                ff[i] = filter.process(datum[i]);
            dataArrayFiltered.add(ff);
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

    public void addMode(@NotNull Mode def) {
        this.mode.add(def);
    }

    public void removeMode(@NotNull Mode def) {
        this.mode.remove(def);
    }

    public void clearModes() {
        this.mode.clear();
    }

    public Set<Mode> getModes() {
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
    }

    @Override
    protected void calculateSimpleView() {
        if (modes.containsKey(WindowSource.RAW))
            return;
        super.calculateSimpleView();
        modes.put(WindowSource.RAW, usualView);
    }

    protected void calculateSimpleFilterView() {
        if (modes.containsKey(WindowSource.FILTERED))
            return;
        int ss = (view[1] - view[0]);
        DataContainer.datacopy(dataArrayFiltered, view[0], filterView[0], 0, ss);
        for (int i = 0; i < ss; i++) {
            filterView[1][i] = (view[0] + i);
        }
        modes.put(WindowSource.FILTERED, filterView);
    }

    protected void calculateSimpleFourierView() {
        if (modes.containsKey(WindowSource.FREQUENCIES))
            return;
        calculateSimpleView();
        DoubleDCT_1D dct = new DoubleDCT_1D(this.usualView[0].length);
        System.arraycopy(usualView[0], 0, dctView[0], 0, view[1] - view[0]);
        for (int i = view[1] - view[0]; i < OVERVIEW_SIZE; i++) {
            dctView[0][i] = 0;
        }
        dct.forward(dctView[0], true);
        double ss2 = ((double) (view[1] - view[0]) * 2);
        dctView[0][0] = 0;
        for (int i = 0; i < dctView[0].length; i++) {
            dctView[0][i] = Math.abs(dctView[0][i]) / 1;
            dctView[1][i] = ((double)i * discretisation) / ss2;
        }
        modes.put(WindowSource.FREQUENCIES, dctView);
    }

    protected void calculateSimpleFilteredFourierView() {
        if (modes.containsKey(WindowSource.FILTERED_FREQUENCIES))
            return;
        calculateSimpleFilterView();
        DoubleDCT_1D dct = new DoubleDCT_1D(this.filterView[0].length);
        System.arraycopy(filterView[0], 0, dctFilterView[0], 0, view[1] - view[0]);
        dct.forward(dctFilterView[0], true);
        double ss2 = ((double) (view[1] - view[0]) * 2);
        dctFilterView[0][0] = 0;
        for (int i = 0; i < dctFilterView[0].length; i++) {
            dctFilterView[0][i] = Math.abs(dctFilterView[0][i]) / 1;
            dctFilterView[1][i] = ((double)i * discretisation) / ss2;
        }
        modes.put(WindowSource.FILTERED_FREQUENCIES, dctFilterView);
    }

    protected void calculateSimpleRMSView() {
        if (modes.containsKey(WindowSource.POW))
            return;
        calculateSimpleView();
        int ss = (view[1] - view[0]);
        double multer = OVERVIEW_SIZE / ((double) (ss));
        for (int i = 0; i < ss; i++) {
            rmsView[0][i] = 0;
        }
        for (int i = 0; i < ss; i++) {
            int cnt = 0;
            for (int k = -rmsWindow + 1; k < rmsWindow; k++) {
                if ( (0 <= (i + k)) && ((i + k) < usualView[0].length) ) {
                    rmsView[0][i] += usualView[0][(i + k)] * usualView[0][(i + k)];
                    cnt++;
                }
            }
            rmsView[0][i] = Math.sqrt(rmsView[0][i] / cnt);
        }
        for (int i = 0; i < ss; i++) {
            rmsView[1][i] = (view[0] + (i) / multer);
        }
        modes.put(WindowSource.POW, rmsView);
    }

    protected void calculateSimpleView(int start, int end) {
        for (Mode m : mode) {
            switch (m) {
                case POWER:
                    calculateSimpleRMSView();
                    break;
                case USUAL:
                    calculateSimpleView();
                    break;
                case FOURIER:
                    calculateSimpleFourierView();
                    break;
                case FILTER:
                    if (this.filter != null)
                        calculateSimpleFilterView();
                    else
                        calculateSimpleView();
                    break;
                case FILTERED_FOURIER:
                    calculateSimpleFilteredFourierView();
                    break;
                default:
                    calculateSimpleView();
                    break;
            }
        }
        for (WindowDynamicParser wdp : parsers) {
            WindowSource ws = wdp.getTypeOfSource();
            if (!modes.containsKey(ws)) {
                switch (ws) {
                    case POW:
                        calculateSimpleRMSView();
                        break;
                    case RAW:
                        calculateSimpleView();
                        break;
                    case FREQUENCIES:
                        calculateSimpleFourierView();
                        break;
                    case FILTERED:
                        if (this.filter != null)
                            calculateSimpleFilterView();
                        else
                            calculateSimpleView();
                        break;
                    case FILTERED_FREQUENCIES:
                        calculateSimpleFilteredFourierView();
                        break;
                    default:
                        calculateSimpleView();
                        break;
                }
            }
            double[][] r = modes.get(wdp.getTypeOfSource());
            wdp.setData(r[0], r[1]);
        }
        discretisationView = discretisation;
        activeView = view[1] - view[0];
        viewActual = true;
    }

    @Override
    protected void calculateReducedView() {
        if (modes.containsKey(WindowSource.RAW))
            return;
        super.calculateReducedView();
        modes.put(WindowSource.RAW, usualView);
    }

    protected void calculateReducedFilterView() {
        if (modes.containsKey(WindowSource.FILTERED))
            return;
        double multer = OVERVIEW_SIZE / ((double) (view[1] - view[0]));
        discretisationView = discretisation * multer;
        double timeMultiplicand = DataContainer.reduce(dataViewPrep, dataArray, view[0], view[1] - view[0]);
        System.arraycopy(dataViewPrep, dataViewPrep.length - OVERVIEW_SIZE, usualView[0], 0, OVERVIEW_SIZE);
        for (int i = 0; i < usualView[1].length; i++) {
            usualView[1][i] = (view[0] + (i) / multer);
        }
        modes.put(WindowSource.FILTERED, usualView);
    }

    protected void calculateReducedFourierView() {
        if (modes.containsKey(WindowSource.FREQUENCIES))
            return;
        calculateReducedView();
        DoubleDCT_1D dct = new DoubleDCT_1D(this.usualView[0].length);
        System.arraycopy(usualView[0], 0, dctView[0], 0, usualView[0].length);
        dct.forward(dctView[0], true);
        double ss2 = ((double) (view[1] - view[0]) * 2);
        dctView[0][0] = 0;
        for (int i = 0; i < dctView[0].length; i++) {
            dctView[0][i] = Math.abs(dctView[0][i]) / 1;
            dctView[1][i] = ((double)i * discretisation) / ss2;
        }
        modes.put(WindowSource.FREQUENCIES, dctView);
    }

    protected void calculateReducedFilteredFourierView() {
        if (modes.containsKey(WindowSource.FREQUENCIES))
            return;
        calculateReducedFilterView();
        DoubleDCT_1D dct = new DoubleDCT_1D(this.filterView[0].length);
        System.arraycopy(filterView[0], 0, dctFilterView[0], 0, filterView[0].length);
        dct.forward(dctFilterView[0], true);
        double ss2 = ((double) (view[1] - view[0]) * 2);
        dctFilterView[0][0] = 0;
        for (int i = 0; i < dctFilterView[0].length; i++) {
            dctFilterView[0][i] = Math.abs(dctFilterView[0][i]) / 1;
            dctFilterView[1][i] = ((double)i * discretisation) / ss2;
        }
        modes.put(WindowSource.FREQUENCIES, dctFilterView);
    }

    protected void calculateReducedRMSView() {
        if (modes.containsKey(WindowSource.POW))
            return;
        calculateReducedView();
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
        for (int i = 0; i < rmsView[1].length; i++) {
            rmsView[1][i] = (view[0] + (i) / multer);
        }
        modes.put(WindowSource.POW, rmsView);
    }

    protected void calculateReducedView(int start, int end) {
        for (Mode m : mode) {
            switch (m) {
                case POWER:
                    calculateReducedRMSView();
                    break;
                case USUAL:
                    calculateReducedView();
                    break;
                case FOURIER:
                    calculateReducedFourierView();
                    break;
                case FILTER:
                    if (this.filter != null)
                        calculateReducedFilterView();
                    else
                        calculateReducedView();
                    break;
                case FILTERED_FOURIER:
                    calculateReducedFilteredFourierView();
                    break;
                default:
                    calculateReducedView();
                    break;
            }
        }
        for (WindowDynamicParser wdp : parsers) {
            WindowSource ws = wdp.getTypeOfSource();
            if (!modes.containsKey(ws)) {
                switch (ws) {
                    case POW:
                        calculateReducedRMSView();
                        break;
                    case RAW:
                        calculateReducedView();
                        break;
                    case FREQUENCIES:
                        calculateReducedFourierView();
                        break;
                    case FILTERED:
                        if (this.filter != null)
                            calculateReducedFilterView();
                        else
                            calculateReducedView();
                        break;
                    case FILTERED_FREQUENCIES:
                        calculateReducedFilteredFourierView();
                        break;
                    default:
                        calculateReducedView();
                        break;
                }
            }
            double[][] r = modes.get(wdp.getTypeOfSource());
            wdp.setData(r[0], r[1]);
        }
        discretisationView = discretisation;
        activeView = OVERVIEW_SIZE;
        viewActual = true;
    }

    @Override
    protected void calculateView(int start, int end) {
        if (modes == null)
            return;
        checkView(start, end);
        modes.clear();
        int ss = (view[1] - view[0]);
        if (ss >= (OVERVIEW_SIZE)) {
            calculateReducedView(start, end);
        } else {
            calculateSimpleView(start, end);
        }
    }

    public double[] getDataView(Mode m){
        switch (m) {
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

    public double[] getTimeView(Mode m){
        switch (m) {
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

    public int getActiveView(Mode m) {
        switch (m) {
            case FOURIER:
            case FILTERED_FOURIER:
                return OVERVIEW_SIZE / 2;
            case POWER:
            case FILTER:
            case USUAL:
            default:
                return activeView;
        }
    }

}
