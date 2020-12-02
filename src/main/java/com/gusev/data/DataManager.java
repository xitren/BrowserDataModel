package com.gusev.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gusev.data.offline.StaticDataContainer;
import com.gusev.data.online.DynamicDataContainer;
import javafx.scene.chart.XYChart;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DataManager<T extends DataContainer> {
    protected final List<ExtendedDataLine<T>> dataLines = new LinkedList();
    protected final List<Mark> marks = new LinkedList();

    public DataManager(int n) {
        for (int i=0;i < n;i++) {
            dataLines.add(new ExtendedDataLine(new DynamicDataContainer()));
        }
    }

    public DataManager(double[] ... data) {
        for (int i=0;i < data.length;i++) {
            dataLines.add(new ExtendedDataLine(new StaticDataContainer(data[i])));
        }
    }

    public void setFilterGlobal(double[] data) {
        for (int i=0;i < dataLines.size();i++) {
            dataLines.get(i).setFilter(new FIR(data));
        }
    }

    public void clearMarks() {
        marks.clear();
    }

    public void addMark(int ch, int start, int finish, String name,
                        double red, double green, double blue) {
        marks.add(new Mark(ch, start, finish, name, red, green, blue));
    }

    public void addGlobalMark(int start, int finish, String name,
                              double red, double green, double blue) {
        marks.add(new Mark(-1, start, finish, name, red, green, blue));
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
                    data.red[i], data.green[i], data.blue[i]));
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
            data.red[i] = dlds.red;
            data.green[i] = dlds.green;
            data.blue[i] = dlds.blue;
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

    public double[] getRawDataLine(int i) {
        return DataContainer.toArray(dataLines.get(i).getDataArray());
    }

    public double[] getDataLine(int i) {
        return dataLines.get(i).getDataView();
    }

    public double[] getTimeLine(int i) {
        return dataLines.get(i).getTimeView();
    }

    public double[] getOverview(int i) {
        return dataLines.get(i).getDataOverview();
    }

    public int getDataContainerSize(int i) {
        return dataLines.get(i).dataArray.length();
    }

    public int getActiveView(int i) {
        return dataLines.get(i).getActiveView();
    }

    public double[] getTimeOverview(int i) {
        return dataLines.get(i).getTimeOverview();
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

    public class Mark {
        public int start;
        public int finish;
        public String name;
        public int channel;
        public double red;
        public double green;
        public double blue;

        public Mark(int channel, int start, int finish, String name,
                    double red, double green, double blue) {
            this.start = start;
            this.finish = finish;
            this.name = name;
            this.channel = channel;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
