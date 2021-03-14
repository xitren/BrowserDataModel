package com.gusev.data;

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
        discretisationView = discretisation;
        activeView = OVERVIEW_SIZE;
        view[1] = getMaxView();
        view[0] = view[1] - OVERVIEW_SIZE;
        dataArrayFiltered.lastblock(filterView[0], OVERVIEW_SIZE);
        for (int i = 0; i < (view[1] - view[0]); i++) {
            filterView[1][i] = (view[0] + i);
        }
    }

    protected void calculateOnlineView() {
        discretisationView = discretisation;
        activeView = OVERVIEW_SIZE;
        dataArray.lastblock(usualView[0], OVERVIEW_SIZE);
        for (int i = 0; i < (view[1] - view[0]); i++) {
            usualView[1][i] = (view[0] + i);
        }
    }
}
