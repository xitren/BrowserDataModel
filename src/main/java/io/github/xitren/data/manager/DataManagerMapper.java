package io.github.xitren.data.manager;

import io.github.xitren.data.container.DataContainer;
import io.github.xitren.data.line.OnlineDataLine;
import org.jetbrains.annotations.NotNull;

public class DataManagerMapper<V extends OnlineDataLine<T>, T extends DataContainer> extends DataManagerUpdater<V, T> {

    public DataManagerMapper(V[] edl) {
        super(edl);
    }

    public void addDataMap(@NotNull double[][] data, int[] src, int[] map) {
        if (stop)
            return;
        synchronized (this) {
            for (int i=0;i < map.length && i < src.length;i++) {
                dataLines.get(map[i]).add(data[src[i]]);
            }
            needUpdateOverview = true;
            needUpdateView = true;
        }
    }

    public void addDataMap(@NotNull long[][] data, int[] src, int[] map) {
        if (stop)
            return;
        synchronized (this) {
            for (int i=0;i < map.length && i < src.length;i++) {
                dataLines.get(map[i]).add(data[src[i]]);
            }
            needUpdateOverview = true;
            needUpdateView = true;
        }
    }
}
