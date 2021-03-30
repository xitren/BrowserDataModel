package io.github.xitren.data.manager;

import io.github.xitren.data.container.DataContainer;
import io.github.xitren.data.container.DynamicDataContainer;
import io.github.xitren.data.container.StaticDataContainer;
import io.github.xitren.data.line.DataLine;
import io.github.xitren.data.line.DataLineMode;
import io.github.xitren.data.line.ExtendedDataLine;
import io.github.xitren.data.line.OnlineDataLine;
import org.jetbrains.annotations.NotNull;

public class DataManager<V extends DataLine<T>, T extends DataContainer> extends MarkManager {
    protected final V[] dataLines;

    protected DataLineMode[] modes;
    protected Integer[] swapper;

    public DataManager(V[] edl) {
        swapper = new Integer[edl.length];
        dataLines = edl;
        for (int i = 0;i < dataLines.length;i++) {
            swapper[i] = i;
        }
        modes = new DataLineMode[swapper.length];
        synchronized (modes) {
            for (int i = 0; i < modes.length; i++) {
                modes[i] = DataLineMode.USUAL;
            }
        }
    }

    public static DataManager<OnlineDataLine<DynamicDataContainer>, DynamicDataContainer> DataManagerFactory(int n) {
        OnlineDataLine[] odl = new OnlineDataLine[n];
        for (int i = 0;i < odl.length;i++) {
            odl[i] = new OnlineDataLine(new DynamicDataContainer(), "Channel " + i);
        }
        return new DataManager(odl);
    }

    public static DataManager<ExtendedDataLine<StaticDataContainer>, StaticDataContainer> DataManagerFactory(
            @NotNull double[] ... data) {
        ExtendedDataLine[] odl = new ExtendedDataLine[data.length];
        for (int i = 0;i < odl.length;i++) {
            odl[i] = new ExtendedDataLine(new StaticDataContainer(data[i]), "Channel " + i);
        }
        return new DataManager(odl);
    }

    public void setSwapper(@NotNull Integer[] swapper) {
        modes = new DataLineMode[swapper.length];
        synchronized (modes) {
            for (int i = 0; i < modes.length; i++) {
                modes[i] = DataLineMode.USUAL;
            }
        }
        synchronized (this.swapper) {
            for (Integer sw : swapper) {
                if (!((0 <= sw) && (sw < dataLines.length))) {
                    throw new IndexOutOfBoundsException("Wrong index!");
                }
            }
            this.swapper = swapper;
        }
    }

    public Integer[] getSwapper() {
        return swapper;
    }

    public int size() {
        return dataLines.length;
    }

    private V getFromSwapper(int i){
        return dataLines[swapper[i]];
    }

    public String[] getDataLabel() {
        String[] labels = new String[dataLines.length];
        for (int i = 0;i < dataLines.length;i++) {
            labels[i] = dataLines[i].getName();
        }
        return labels;
    }

    public String[] getSwapperLabels() {
        String[] labels = new String[swapper.length];
        for (int i = 0;i < swapper.length;i++) {
            labels[i] = dataLines[swapper[i]].getName();
        }
        return labels;
    }

}
