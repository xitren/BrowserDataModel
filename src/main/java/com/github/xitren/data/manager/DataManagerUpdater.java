package com.github.xitren.data.manager;

import com.github.xitren.data.line.DataLineMode;
import com.github.xitren.data.line.OnlineDataLine;
import com.github.xitren.data.container.DataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class DataManagerUpdater<V extends OnlineDataLine<T>, T extends DataContainer> extends DataManagerView<V, T> {
    private boolean overviewSuppressed = false;
    protected boolean stop = false;
    private boolean online = true;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private volatile Future<Boolean> futureView;
    private volatile Future<Boolean> futureMark;
    private volatile Future<Boolean> futureOverview;

    protected final Callable<Boolean> viewUpdater = () -> {
        updateView();
        updateValues();
        setChanged();
        notifyObservers(DataManagerAction.ViewUpdated);
        return true;
    };
    protected final Callable<Boolean> markUpdater = () -> {
        updateMarks();
        setChanged();
        notifyObservers(DataManagerAction.MarksUpdated);
        return true;
    };
    protected final Callable<Boolean> overviewUpdater = () -> {
        unsetOverview();
        updateOverview();
        updateOverviewValues();
        setChanged();
        notifyObservers(DataManagerAction.OverviewUpdated);
        return true;
    };

    protected abstract void updateMarks();
    protected abstract void updateValues();
    protected abstract void updateOverviewValues();

    public final synchronized void callViewUpdate() {
        if (futureView != null)
            if (!futureView.isDone())
                return;
        futureView = executorService.submit(viewUpdater);
    }

    public final synchronized void callMarkUpdate() {
        if (futureMark != null)
            if (!futureMark.isDone())
                return;
        futureMark = executorService.submit(markUpdater);
    }

    public final synchronized void callOverviewUpdate() {
        if (futureOverview != null)
            if (!futureOverview.isDone() || overviewSuppressed)
                return;
        futureOverview = executorService.submit(overviewUpdater);
    }

    public DataManagerUpdater(V[] edl) {
        super(edl);
        callViewUpdate();
        callOverviewUpdate();
    }

    @Override
    public void setSwapper(@NotNull Integer[] swapper) {
        super.setSwapper(swapper);
        callViewUpdate();
    }

    @Override
    protected void finalize() {
        if (!executorService.isTerminated())
            executorService.shutdown();
    }

    public void stop() {
        executorService.shutdown();
    }

    public void pause() {
        stop = true;
        online = false;
    }

    public void start() {
        stop = false;
        online = true;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
        for (int i = 0;i < dataLines.length;i++) {
            dataLines[i].setOnline(online);
        }
    }

    public boolean isOverviewSuppressed() {
        return overviewSuppressed;
    }

    public void setOverviewSuppressed(boolean overviewSuppressed) {
        this.overviewSuppressed = overviewSuppressed;
        if (!this.overviewSuppressed)
            callOverviewUpdate();
    }

    @Override
    public void clearMarks() {
        super.clearMarks();
        callMarkUpdate();
    }

    @Override
    protected void addMark(int ch, int start, int finish, String name,
                           String color, String label_color) {
        super.addMark(ch, start, finish, name, color, label_color);
        callMarkUpdate();
    }

    @Override
    protected void addGlobalMark(int start, int finish, String name,
                                 String color, String label_color) {
        super.addGlobalMark(start, finish, name, color, label_color);
        callMarkUpdate();
    }

    @Override
    public void setFilterGlobal(@NotNull double[] data) {
        super.setFilterGlobal(data);
        callViewUpdate();
    }

    @Override
    public void addData(@NotNull double[][] data) {
        if (stop)
            return;
        super.addData(data);
        callOverviewUpdate();
        callViewUpdate();
    }

    @Override
    public void addData(@NotNull long[][] data) {
        if (stop)
            return;
        super.addData(data);
        callOverviewUpdate();
        callViewUpdate();
    }

    protected void updateOverview() {
        Set<Callable<Boolean>> taskList = new HashSet<>();
        for (int i=0;i < swapper.length;i++) {
            OnlineDataLine dl = dataLines[swapper[i]];
            if (!dl.isOverviewActual()) {
                taskList.add(()->{
                    dl.calculateOverview();
                    return true;
                });
            }
        }
        try {
            executorService.invokeAll(taskList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void unsetOverview() {
        for (int i=0;i < dataLines.length;i++) {
            dataLines[i].unsetOverview();
        }
    }

    @Override
    public void cut(int start, int size) {
        super.cut(start, size);
        callOverviewUpdate();
        callViewUpdate();
        callMarkUpdate();
    }

    private void updateView() {
        if (swapper == null)
            return;
        if (online)
            setTailView();
        for (int i = 0; i < swapper.length; i++) {
            OnlineDataLine dl = dataLines[swapper[i]];
            dl.clearModes();
            for (DataLineMode em : modes) {
                dl.addMode(em);
            }
            dl.setView(view[0], view[1]);
        }
    }

    @Override
    protected void setView(int start, int end) {
        super.setView(start, end);
        callViewUpdate();
    }

    @Override
    protected void setMaxView() {
        super.setMaxView();
        callViewUpdate();
    }

    @Override
    protected void setTailView() {
        super.setTailView();
        callViewUpdate();
    }

    @Override
    public void setCurrentMark(String name, String color, String label_color) {
        super.setCurrentMark(name, color, label_color);
        callMarkUpdate();
    }
}
