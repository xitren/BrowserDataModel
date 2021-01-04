package com.gusev.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gusev.data.offline.StaticDataContainer;
import com.gusev.data.online.DynamicDataContainer;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DataManager<T extends DataContainer> {
    protected final List<ExtendedDataLine<T>> dataLines = new LinkedList();
    protected final List<Mark> marks = new LinkedList();
    private Integer[] swapper = null;

    public DataManager(int n) {
        swapper = new Integer[n];
        for (int i=0;i < n;i++) {
            dataLines.add(new ExtendedDataLine(new DynamicDataContainer()));
            swapper[i] = i;
        }
    }

    public DataManager(@NotNull double[] ... data) {
        for (int i=0;i < data.length;i++) {
            dataLines.add(new ExtendedDataLine(new StaticDataContainer(data[i])));
        }
    }

    public void setSwapper(Integer[] swapper) {
        for (Integer sw : swapper) {
            if (!((0 <= sw) && (sw < dataLines.size()))) {
                throw new IndexOutOfBoundsException("Wrong index!");
            }
        }
        this.swapper = swapper;
    }

    public void setFilterGlobal(@NotNull double[] data) {
        for (int i=0;i < dataLines.size();i++) {
            dataLines.get(i).setFilter(new FIR(data));
        }
    }

    public void addData(@NotNull double[][] data) {
        for (int i=0;i < dataLines.size();i++) {
            dataLines.get(i).add(data[i]);
        }
    }

    public void addData(@NotNull long[][] data) {
        for (int i=0;i < dataLines.size();i++) {
            dataLines.get(i).add(data[i]);
        }
    }

    public void clearMarks() {
        marks.clear();
    }

    protected void addMark(int ch, int start, int finish, String name,
                        String color, String label_color) {
        marks.add(new Mark(ch, start, finish, name, color, label_color));
    }

    protected void addGlobalMark(int start, int finish, String name,
                              String color, String label_color) {
        marks.add(new Mark(-1, start, finish, name, color, label_color));
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
        for (int i=0;i < data.name.length;i++) {
            marks.add(new Mark(data.channel[i], data.start[i], data.finish[i], data.name[i],
                    data.color[i], data.label_color[i]));
        }
    }

    public void saveToFile(String filename) throws IOException {
        DataModelJson data = new DataModelJson(dataLines.size(), marks.size());
        Iterator<ExtendedDataLine<T>> it = dataLines.iterator();
        int i = 0;
        while (it.hasNext()) {
            ExtendedDataLine dlds = it.next();
            data.data[i++] = DataContainer.toArray(dlds.getDataArray());
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

    public int size() {
        return dataLines.size();
    }

    private ExtendedDataLine<T> getFromSwapper(int i){
        return dataLines.get(swapper[i]);
    }

    public double[] getRawDataLine(int i) {
        return DataContainer.toArray(getFromSwapper(i).getDataArray());
    }

    public double[] getDataLine(int i) {
        return getFromSwapper(i).getDataView();
    }

    public double[] getTimeLine(int i) {
        return getFromSwapper(i).getTimeView();
    }

    public double[] getOverview(int i) {
        return getFromSwapper(i).getDataOverview();
    }

    public ExtendedDataLine.Mode getMode(int i) {
        return getFromSwapper(i).getMode();
    }

    public int getDataContainerSize(int i) {
        return getFromSwapper(i).dataArray.length();
    }

    public int getActiveView(int i) {
        return getFromSwapper(i).getActiveView();
    }

    public double[] getTimeOverview(int i) {
        return getFromSwapper(i).getTimeOverview();
    }

    protected void updateOverview() {
        for (int i=0;i < dataLines.size();i++) {
            dataLines.get(i).calculateOverview();
        }
    }

    public void cut(int start, int size) {
        for (int i=0;i < dataLines.size();i++) {
            dataLines.get(i).cut(start, size);
        }
    }
}
