package com.gusev.data.window.op;

import com.gusev.data.window.op.WindowSource;

public abstract class WindowDynamicParser {
    protected final WindowSource typeOfSource;

    protected WindowDynamicParser(WindowSource typeOfSource) {
        this.typeOfSource = typeOfSource;
    }

    public void setData(double[] data) {
        setData(data, null);
    }

    public abstract void setData(double[] data, double[] timeline);
}
