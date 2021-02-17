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

public class DataManager<T extends DataContainer> {
    protected final List<ExtendedDataLine<T>> dataLines = new LinkedList();
    protected final List<Mark> marks = new LinkedList();
    private Integer[] swapper = null;
    private String personDate;
    private int discretization = 250;
    private String[] dataLabel;

    public DataManager(int n, ExtendedDataLine[] edl) {
        personDate = LocalDateTime.now().toString();
        swapper = new Integer[n];
        dataLabel = new String[n];
        for (int i=0;i < n;i++) {
            dataLines.add(edl[i]);
            swapper[i] = i;
            dataLabel[i] = "";
        }
    }

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

    public void setSwapper(@NotNull Integer[] swapper) {
        for (Integer sw : swapper) {
            if (!((0 <= sw) && (sw < dataLines.size()))) {
                throw new IndexOutOfBoundsException("Wrong index!");
            }
        }
        this.swapper = swapper;
    }

    public Integer[] getSwapper() {
        return swapper;
    }

    public void setFilterGlobal(@NotNull double[] data) {
        for (int i=0;i < dataLines.size();i++) {
            dataLines.get(i).setFilter(new FIR(data));
        }
    }

    public void addDataMap(@NotNull double[][] data, int[] src, int[] map) {
        for (int i=0;i < map.length && i < src.length;i++) {
            dataLines.get(map[i]).add(data[src[i]]);
        }
    }

    public void addDataMap(@NotNull long[][] data, int[] src, int[] map) {
        for (int i=0;i < map.length && i < src.length;i++) {
            dataLines.get(map[i]).add(data[src[i]]);
        }
    }

    public void addData(@NotNull double[][] data) {
        for (int i=0;i < dataLines.size() && i < data.length;i++) {
            dataLines.get(i).add(data[i]);
        }
    }

    public void addData(@NotNull long[][] data) {
        for (int i=0;i < dataLines.size() && i < data.length;i++) {
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
        for (int i=0;i < dataLines.size();i++) {
            dataLines.get(i).cut(start, size);
        }
    }

    public int getDiscretization() {
        return discretization;
    }

    public void setDiscretization(int discretization) {
        this.discretization = discretization;
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
}
