package io.github.xitren.data.manager;

import io.github.xitren.data.container.DataContainer;
import io.github.xitren.data.line.OnlineDataLine;
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
