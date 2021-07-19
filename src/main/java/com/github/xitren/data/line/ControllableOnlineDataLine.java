package com.github.xitren.data.line;

import com.github.xitren.data.container.DataContainer;
import com.github.xitren.data.window.WindowSource;
import edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D;
import org.jetbrains.annotations.NotNull;

public class ControllableOnlineDataLine<T extends DataContainer> extends OnlineDataLine<T> {
    private static final double MIN_VALUE = 1.0;
    private static final double MAX_VALUE = 10.0;
    private double time = MIN_VALUE; // seconds
    protected double[][] controllableView = new double[3][(int)(time * OVERVIEW_SIZE)];

    public ControllableOnlineDataLine(@NotNull T data, String name) {
        super(data, name);
    }

    @Override
    protected void calculateOnlineFilterView() {
        if (modes.containsKey(WindowSource.FILTERED))
            return;
        dataArrayFiltered.lastblock(controllableView[0], (int)(time * OVERVIEW_SIZE));
        DataContainer.reduce(filterView[0], controllableView[0]);
        for (int i = filterView[0].length; i >= 0; i--) {
            int s = filterView[0].length - i;
            filterView[1][i] = (view[1] - s);
            filterView[2][i] = (view[1] - s) / discretisation;
        }
        modes.put(WindowSource.FILTERED, filterView);
    }

    @Override
    protected void calculateOnlineView() {
        if (modes.containsKey(WindowSource.RAW))
            return;
        dataArray.lastblock(controllableView[0], (int)(time * OVERVIEW_SIZE));
        DataContainer.reduce(usualView[0], controllableView[0]);
        for (int i = usualView[0].length; i >= 0; i--) {
            int s = usualView[0].length - i;
            usualView[1][i] = (view[1] - s);
            usualView[2][i] = (view[1] - s) / discretisation;
        }
        modes.put(WindowSource.RAW, usualView);
    }

    @Override
    protected void calculateOnlineFourierView() {
        if (modes.containsKey(WindowSource.FREQUENCIES))
            return;
        calculateOnlineView();
        DoubleDCT_1D dct = new DoubleDCT_1D(this.usualView[0].length);
        System.arraycopy(usualView[0], 0, dctView[0], 0, usualView[0].length);
        dct.forward(dctView[0], true);
        double ss2 = ((double) ((int)(time * OVERVIEW_SIZE)) * 2);
        dctView[0][0] = 0;
        for (int i = 0; i < dctView[0].length; i++) {
            dctView[0][i] = Math.abs(dctView[0][i]) / 1;
            dctView[1][i] = ((double)i * discretisation) / ss2;
        }
        modes.put(WindowSource.FREQUENCIES, dctView);
    }

    @Override
    protected void calculateOnlineFilteredFourierView() {
        if (modes.containsKey(WindowSource.FILTERED_FREQUENCIES))
            return;
        calculateOnlineFilterView();
        DoubleDCT_1D dct = new DoubleDCT_1D(this.filterView[0].length);
        System.arraycopy(filterView[0], 0, dctFilterView[0], 0, filterView[0].length);
        dct.forward(dctFilterView[0], true);
        double ss2 = ((double) ((int)(time * OVERVIEW_SIZE)) * 2);
        dctFilterView[0][0] = 0;
        for (int i = 0; i < dctFilterView[0].length; i++) {
            dctFilterView[0][i] = Math.abs(dctFilterView[0][i]) / 1;
            dctFilterView[1][i] = ((double)i * discretisation) / ss2;
        }
        modes.put(WindowSource.FILTERED_FREQUENCIES, dctFilterView);
    }

    public void setTime(double time) {
        if (time < MIN_VALUE) {
            this.time = MIN_VALUE;
            return;
        }
        if (time > MAX_VALUE) {
            this.time = MAX_VALUE;
            return;
        }
    }

    private void checkBufferSize(){
        if (controllableView[0].length != (int)(time * OVERVIEW_SIZE)) {
            controllableView = new double[3][(int)(time * OVERVIEW_SIZE)];
        }
    }
}
