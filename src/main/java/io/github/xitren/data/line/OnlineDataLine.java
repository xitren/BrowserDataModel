package io.github.xitren.data.line;

import io.github.xitren.data.container.DataContainer;
import io.github.xitren.data.window.WindowDynamicParser;
import io.github.xitren.data.window.WindowSource;
import edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D;
import org.jetbrains.annotations.NotNull;

public class OnlineDataLine<T extends DataContainer> extends ExtendedDataLine<DataContainer> {
    private boolean online;

    public OnlineDataLine(@NotNull DataContainer _data) {
        super(_data);
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    protected void calculateOnlineFilterView() {
        if (modes.containsKey(WindowSource.FILTERED))
            return;
        dataArrayFiltered.lastblock(filterView[0], OVERVIEW_SIZE);
        for (int i = 0; i < (view[1] - view[0]); i++) {
            filterView[1][i] = (view[0] + i);
        }
        modes.put(WindowSource.FILTERED, filterView);
    }

    protected void calculateOnlineView() {
        if (modes.containsKey(WindowSource.RAW))
            return;
        dataArray.lastblock(usualView[0], OVERVIEW_SIZE);
        for (int i = 0; i < (view[1] - view[0]); i++) {
            usualView[1][i] = (view[0] + i);
        }
        modes.put(WindowSource.RAW, usualView);
    }

    protected void calculateOnlineFourierView() {
        if (modes.containsKey(WindowSource.FREQUENCIES))
            return;
        calculateOnlineView();
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

    protected void calculateOnlineFilteredFourierView() {
        if (modes.containsKey(WindowSource.FILTERED_FREQUENCIES))
            return;
        calculateOnlineFilterView();
        DoubleDCT_1D dct = new DoubleDCT_1D(this.filterView[0].length);
        System.arraycopy(filterView[0], 0, dctFilterView[0], 0, filterView[0].length);
        dct.forward(dctFilterView[0], true);
        double ss2 = ((double) (view[1] - view[0]) * 2);
        dctFilterView[0][0] = 0;
        for (int i = 0; i < dctFilterView[0].length; i++) {
            dctFilterView[0][i] = Math.abs(dctFilterView[0][i]) / 1;
            dctFilterView[1][i] = ((double)i * discretisation) / ss2;
        }
        modes.put(WindowSource.FILTERED_FREQUENCIES, dctFilterView);
    }

    protected void calculateOnlineRMSView() {
        if (modes.containsKey(WindowSource.POW))
            return;
        calculateOnlineView();
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

    @Override
    protected void calculateView(int start, int end) {
        if (modes == null)
            return;
        if (online == false) {
            super.calculateView(start, end);
            return;
        }
        checkView(start, end);
        modes.clear();
        discretisationView = discretisation;
        activeView = OVERVIEW_SIZE;
        view[1] = getMaxView();
        view[0] = view[1] - OVERVIEW_SIZE;
        for (DataLineMode m : mode) {
            switch (m) {
                case POWER:
                    calculateOnlineRMSView();
                    break;
                case USUAL:
                    calculateOnlineView();
                    break;
                case FOURIER:
                    calculateOnlineFourierView();
                    break;
                case FILTER:
                    if (this.filter != null)
                        calculateOnlineFilterView();
                    else
                        calculateOnlineView();
                    break;
                case FILTERED_FOURIER:
                    calculateOnlineFilteredFourierView();
                    break;
                default:
                    calculateOnlineView();
                    break;
            }
        }
        for (WindowDynamicParser wdp : parsers) {
            WindowSource ws = wdp.getTypeOfSource();
            if (!modes.containsKey(ws)) {
                switch (ws) {
                    case POW:
                        calculateOnlineRMSView();
                        break;
                    case RAW:
                        calculateOnlineView();
                        break;
                    case FREQUENCIES:
                        calculateOnlineFourierView();
                        break;
                    case FILTERED:
                        if (this.filter != null)
                            calculateOnlineFilterView();
                        else
                            calculateOnlineView();
                        break;
                    case FILTERED_FREQUENCIES:
                        calculateOnlineFilteredFourierView();
                        break;
                    default:
                        calculateOnlineView();
                        break;
                }
            }
            double[][] r = modes.get(wdp.getTypeOfSource());
            wdp.setData(r[0], r[1]);
        }
        viewActual = true;
    }

    public void unsetOverview() {
        overviewActual = false;
    }
}
