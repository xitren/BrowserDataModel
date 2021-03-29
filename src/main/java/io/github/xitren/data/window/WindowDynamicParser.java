package io.github.xitren.data.window;

public abstract class WindowDynamicParser {
    protected final WindowSource typeOfSource;
    protected int activeView;

    protected WindowDynamicParser(WindowSource typeOfSource) {
        this.typeOfSource = typeOfSource;
    }

    public WindowSource getTypeOfSource() {
        return typeOfSource;
    }

    public void setData(double[] data) {
        setData(data, null);
    }

    public abstract void setData(double[] data, double[] timeline);
}
