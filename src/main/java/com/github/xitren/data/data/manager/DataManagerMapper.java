package com.github.xitren.data.data.manager;

import com.github.xitren.data.data.line.OnlineDataLine;
import com.github.xitren.data.data.container.DataContainer;
import org.jetbrains.annotations.NotNull;

public abstract class DataManagerMapper<V extends OnlineDataLine<T>, T extends DataContainer> extends DataManagerUpdater<V, T> {

    public DataManagerMapper(V[] edl) {
        super(edl);
    }

    public void addDataMap(@NotNull double[][] data, int[] src, int[] map) {
        if (stop)
            return;
        synchronized (dataLines) {
            for (int i=0;i < map.length && i < src.length;i++) {
                dataLines[map[i]].add(data[src[i]]);
            }
        }
        callOverviewUpdate();
        callViewUpdate();
    }

    public void addDataMap(@NotNull long[][] data, int[] src, int[] map) {
        if (stop)
            return;
        synchronized (dataLines) {
            for (int i=0;i < map.length && i < src.length;i++) {
                dataLines[map[i]].add(data[src[i]]);
            }
        }
        callOverviewUpdate();
        callViewUpdate();
    }
}
