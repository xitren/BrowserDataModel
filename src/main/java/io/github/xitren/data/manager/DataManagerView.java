package io.github.xitren.data.manager;

import io.github.xitren.data.Mark;
import io.github.xitren.data.container.DataContainer;
import io.github.xitren.data.line.DataLine;
import io.github.xitren.data.line.OnlineDataLine;

public class DataManagerView<V extends OnlineDataLine<T>, T extends DataContainer> extends DataManagerExtended<V, T>{

    protected int[] view = new int[2];

    public DataManagerView(V[] edl) {
        super(edl);
    }

    protected void setView(int start, int end) {
        synchronized (view) {
            view[0] = start;
            view[1] = end;
        }
        synchronized (dataLines) {
            for (int i = 0; i < swapper.length; i++) {
                V dl = dataLines[swapper[i]];
                dl.setOnline(false);
            }
        }
    }

    protected void setMaxView() {
        OnlineDataLine dl = null;
        synchronized (dataLines) {
            for (int i = 0; i < swapper.length; i++) {
                dl = dataLines[swapper[i]];
                dl.setOnline(false);
                break;
            }
        }
        if (dl != null)
            synchronized (view) {
                view[0] = 0;
                view[1] = dl.getMaxView();
            }
    }

    protected int getMaxView() {
        OnlineDataLine dl = null;
        synchronized (this) {
            for (int i = 0; i < swapper.length; i++) {
                dl = dataLines[swapper[i]];
                dl.setOnline(true);
            }
        }
        if (dl == null)
            return 0;
        else
            return dl.getMaxView();
    }

    protected void setTailView() {
        OnlineDataLine dl = null;
        synchronized (this) {
            for (int i = 0; i < swapper.length; i++) {
                dl = dataLines[swapper[i]];
                dl.setOnline(true);
            }
        }
        synchronized (view) {
            if (dl == null) {
                view[1] = 0;
                view[0] = 0;
            } else {
                view[1] = dl.getMaxView();
                view[0] = view[1] - DataLine.OVERVIEW_SIZE;
            }
        }
    }

    public void setCurrentMark(String name, String color, String label_color) {
        synchronized (marks) {
            marks.add(new Mark(-1, view[0], view[1], name, color, label_color));
        }
    }

    public int[] getLastView() {
        return view;
    }
}
