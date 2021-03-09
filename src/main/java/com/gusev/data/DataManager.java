package com.gusev.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gusev.data.offline.StaticDataContainer;
import com.gusev.data.online.DynamicDataContainer;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class DataManager<T extends DataContainer> extends Observable {
    protected final List<ExtendedDataLine<T>> dataLines = new LinkedList();
    protected final List<Mark> marks = new LinkedList();
    protected ExtendedDataLine.Mode[] modes;
    private Integer[] swapper = null;
    private String personDate;
    private double discretisation = 250;
    private double timePeriod = 0.004;
    private String[] dataLabel;
    private boolean needUpdateOverview = true;
    private boolean needUpdateView = true;
    private boolean needUpdateMarks = true;
    private boolean stopped = false;
    private boolean started = false;
    private boolean overviewSuppressed = false;
    private int[] start_end = new int[2];
    private final Thread updater = new Thread(()->{
        while (!stopped) {
            started = true;
            if (needUpdateOverview && !overviewSuppressed) {
                synchronized (this) {
                    unsetOverview();
                    updateOverview();
                    needUpdateOverview = false;
                    System.out.println("Updated Overview");
                }
                setChanged();
                notifyObservers(Action.OverviewUpdated);
            }
            if (needUpdateView) {
                synchronized (this) {
                    updateView();
                    needUpdateView = false;
                    System.out.println("Updated View");
                }
                setChanged();
                notifyObservers(Action.ViewUpdated);
            }
            if (needUpdateMarks) {
                synchronized (this) {
                    needUpdateMarks = false;
                    System.out.println("Updated Marks");
                }
                setChanged();
                notifyObservers(Action.MarksUpdated);
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    public DataManager(int n, ExtendedDataLine[] edl) {
        personDate = LocalDateTime.now().toString();
        swapper = new Integer[n];
        dataLabel = new String[n];
        for (int i=0;i < n;i++) {
            dataLines.add(edl[i]);
            swapper[i] = i;
            dataLabel[i] = "";
        }
        updater.start();
    }

    public DataManager(int n) {
        swapper = new Integer[n];
        for (int i=0;i < n;i++) {
            dataLines.add(new ExtendedDataLine(new DynamicDataContainer()));
            swapper[i] = i;
        }
        updater.start();
//        while (!started) {
//            System.out.println("o");
//            started = false;
//        }
    }

    public DataManager(@NotNull double[] ... data) {
        for (int i=0;i < data.length;i++) {
            dataLines.add(new ExtendedDataLine(new StaticDataContainer(data[i])));
        }
        updater.start();
    }

    public DataManager(String filename) throws IOException {
        InputStream inputStream = new FileInputStream(filename);
        Reader inputStreamReader = new InputStreamReader(inputStream);
        JsonParser ow = new ObjectMapper().reader().createParser(inputStream);
        DataModelJson data = ow.readValueAs(DataModelJson.class);
        inputStreamReader.close();
        for (int i=0;i < data.data.length;i++) {
            dataLines.add(new ExtendedDataLine(new StaticDataContainer(data.data[i])));
        }
        dataLabel = data.data_label;
        for (int i=0;i < data.name.length;i++) {
            marks.add(new Mark(data.channel[i], data.start[i], data.finish[i], data.name[i],
                    data.color[i], data.label_color[i]));
        }
        swapper = new Integer[dataLines.size()];
        for (int i=0;i < dataLines.size();i++) {
            swapper[i] = i;
        }
        updater.start();
    }

    @Override
    protected void finalize() {
        stopped = true;
    }

    public void stop() {
        stopped = true;
    }

    public boolean isOverviewSuppressed() {
        return overviewSuppressed;
    }

    public void setOverviewSuppressed(boolean overviewSuppressed) {
        this.overviewSuppressed = overviewSuppressed;
    }

    public void setSwapper(@NotNull Integer[] swapper) {
        synchronized (this) {
            for (Integer sw : swapper) {
                if (!((0 <= sw) && (sw < dataLines.size()))) {
                    throw new IndexOutOfBoundsException("Wrong index!");
                }
            }
            this.swapper = swapper;
            needUpdateView = true;
        }
    }

    public Integer[] getSwapper() {
        return swapper;
    }

    public void setFilterGlobal(@NotNull double[] data) {
        synchronized (this) {
            for (int i=0;i < dataLines.size();i++) {
                dataLines.get(i).setFilter(new FIR(data));
            }
            needUpdateView = true;
        }
    }

    public void addDataMap(@NotNull double[][] data, int[] src, int[] map) {
        synchronized (this) {
            for (int i=0;i < map.length && i < src.length;i++) {
                dataLines.get(map[i]).add(data[src[i]]);
            }
            needUpdateOverview = true;
        }
    }

    public void addDataMap(@NotNull long[][] data, int[] src, int[] map) {
        synchronized (this) {
            for (int i=0;i < map.length && i < src.length;i++) {
                dataLines.get(map[i]).add(data[src[i]]);
            }
            needUpdateOverview = true;
        }
    }

    public void addData(@NotNull double[][] data) {
        synchronized (this) {
            for (int i=0;i < dataLines.size() && i < data.length;i++) {
                dataLines.get(i).add(data[i]);
            }
            needUpdateOverview = true;
        }
    }

    public void addData(@NotNull long[][] data) {
//        while (!started) {
//            System.out.println("o");
//        }
        synchronized (this) {
            for (int i=0;i < dataLines.size() && i < data.length;i++) {
                dataLines.get(i).add(data[i]);
            }
            needUpdateOverview = true;
        }
    }

    public void clearMarks() {
        synchronized (this) {
            marks.clear();
            needUpdateMarks = true;
        }
    }

    protected void addMark(int ch, int start, int finish, String name,
                        String color, String label_color) {
        synchronized (this) {
            marks.add(new Mark(ch, start, finish, name, color, label_color));
            needUpdateMarks = true;
        }
    }

    protected void addGlobalMark(int start, int finish, String name,
                              String color, String label_color) {
        synchronized (this) {
            marks.add(new Mark(-1, start, finish, name, color, label_color));
            needUpdateMarks = true;
        }
    }

    public void saveToFile(String filename) throws IOException {
        DataModelJson data = new DataModelJson(dataLines.size(), marks.size());
        Iterator<ExtendedDataLine<T>> it = dataLines.iterator();
        int i = 0;
        while (it.hasNext()) {
            ExtendedDataLine dlds = it.next();
            data.data[i++] = dlds.toArray();
        }
        for (i = 0;i < getDataLabel().length;i++) {
            data.data_label[i] = getDataLabel()[i];
        }
        Iterator<Mark> it2 = marks.iterator();
        i = 0;
        while (it2.hasNext()) {
            Mark dlds = it2.next();
            data.start[i] = dlds.start;
            data.finish[i] = dlds.finish;
            data.name[i] = dlds.name;
            data.channel[i] = dlds.channel;
            data.color[i] = dlds.color;
            data.label_color[i] = dlds.label_color;
            i++;
        }
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        OutputStream outputStream = new FileOutputStream(filename);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        ow.writeValue(outputStream, data);
        outputStreamWriter.close();
    }

    public void saveToTXT(String filename) throws IOException {
        DataModelJson data = new DataModelJson(dataLines.size(), marks.size());
        Iterator<ExtendedDataLine<T>> it = dataLines.iterator();
        int i = 0;
        int length = 0;
        while (it.hasNext()) {
            ExtendedDataLine dlds = it.next();
            length = data.data[i].length;
            data.data[i++] = dlds.toArray();
        }
        Iterator<Mark> it2 = marks.iterator();
        i = 0;
        while (it2.hasNext()) {
            Mark dlds = it2.next();
            data.start[i] = dlds.start;
            data.finish[i] = dlds.finish;
            data.name[i] = dlds.name;
            data.channel[i] = dlds.channel;
            data.color[i] = dlds.color;
            data.label_color[i] = dlds.label_color;
            i++;
        }
        OutputStream outputStream = new FileOutputStream(filename);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        outputStreamWriter.write(String.format("%s\t<Дата проведения исследования: день, месяц, год>\n", data.person_date));
        outputStreamWriter.write(String.format("%d\t<Общее количество каналов>\n", dataLines.size()));
        outputStreamWriter.write(String.format("%d\t<Частота дискретизации, Гц>\n", data.discretization));
        outputStreamWriter.write(String.format("1\t<Количество групп физиологических сигналов>\n"));
        outputStreamWriter.write(String.format("%d\t<Количество сигналов>\n", dataLines.size()));
        outputStreamWriter.write(String.format("\t<Идентификатор исследования>\n"));
        outputStreamWriter.write(String.format("1\t<Количество фрагментов записи>\n"));

        //Фрагмент
        outputStreamWriter.write(String.format("\t<Время начала фрагмента>\n", data.person_name));
        outputStreamWriter.write(String.format("%d\t<Продолжительность фрагмента в отсчетах, отсчетов>\n", length));
        for (int k = 0;k < data.data_label.length;k++) {
            outputStreamWriter.write(data.data_label[k] + "\t");
        }
        outputStreamWriter.write("\n");
        for (int ki = 0;ki < data.data.length;ki++) {
            for (int k = 0;k < data.data[ki].length;k++) {
                outputStreamWriter.write("" + data.data[ki][k] + "\t");
            }
            outputStreamWriter.write("\n");
        }
        outputStreamWriter.close();
    }

    public int size() {
        return dataLines.size();
    }

    private ExtendedDataLine<T> getFromSwapper(int i){
        try {
            return dataLines.get(swapper[i]);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex);
        }
        return null;
    }

    public double[] getRawDataLine(int i) {
        return getFromSwapper(i).toArray();
    }

    public double[] getDataLine(int i, ExtendedDataLine.Mode mode) {
        return getFromSwapper(i).getDataView(mode);
    }

    public int getLineCount() {
        return dataLines.size();
    }

    public double[] getTimeLine(int i, ExtendedDataLine.Mode mode) {
        return getFromSwapper(i).getTimeView(mode);
    }

    public double[] getOverview(int i) {
        return getFromSwapper(i).getDataOverview();
    }

    public Set<ExtendedDataLine.Mode> getMode(int i) {
        return getFromSwapper(i).getModes();
    }

    public int getDataContainerSize(int i) {
        return getFromSwapper(i).dataArray.length();
    }

    public int getActiveView(int i, ExtendedDataLine.Mode mode) {
        return getFromSwapper(i).getActiveView(mode);
    }

    public double[] getTimeOverview(int i) {
        return getFromSwapper(i).getTimeOverview();
    }

    protected void updateOverview() {
        for (int i=0;i < dataLines.size();i++) {
            if (!dataLines.get(i).isOverviewActual())
                dataLines.get(i).calculateOverview();
        }
    }

    protected void unsetOverview() {
        for (int i=0;i < dataLines.size();i++) {
            dataLines.get(i).overviewActual = false;
        }
    }

    public void cut(int start, int size) {
        synchronized (this) {
            for (int i = 0; i < dataLines.size(); i++) {
                dataLines.get(i).cut(start, size);
            }
            needUpdateOverview = true;
            needUpdateView = true;
            needUpdateMarks = true;
        }
    }

    public int[] getLastView() {
        return start_end;
    }

    private void updateView() {
        for (int i = 0; i < getSwapper().length; i++) {
            ExtendedDataLine dl = dataLines.get(getSwapper()[i]);
            dl.clearModes();
            for (ExtendedDataLine.Mode em : modes) {
                dl.addMode(em);
            }
            dl.setView(start_end[0], start_end[1]);
        }
    }

    protected void setView(int start, int end) {
        synchronized (this) {
            start_end[0] = start;
            start_end[1] = end;
            needUpdateView = true;
        }
    }

    public double getDiscretization() {
        return discretisation;
    }

    public void setDiscretization(int discretization) {
        this.discretisation = discretization;
    }

    public String[] getDataLabel() {
        return dataLabel;
    }

    public void setDataLabel(String[] dataLabel) {
        this.dataLabel = dataLabel;
    }

    public String getPersonDate() {
        return personDate;
    }

    public void setPersonDate(String personDate) {
        this.personDate = personDate;
    }

    public double getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(double timePeriod) {
        this.timePeriod = timePeriod;
        this.discretisation = 1 / timePeriod;
    }

    public double getDiscretisation() {
        return discretisation;
    }

    public void setDiscretisation(double discretisation) {
        this.timePeriod = 1 / discretisation;
        this.discretisation = discretisation;
    }

    public void setMode(int i, ExtendedDataLine.Mode def) {
        synchronized (this) {
            modes[i] = def;
            needUpdateView = true;
        }
    }

    public enum Action {
        OverviewUpdated, ViewUpdated, MarksUpdated
    }
}
