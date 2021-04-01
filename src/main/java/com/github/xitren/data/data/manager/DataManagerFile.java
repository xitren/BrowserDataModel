package com.github.xitren.data.data.manager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.xitren.data.data.DataModelJson;
import com.github.xitren.data.data.Mark;
import com.github.xitren.data.data.line.DataLine;
import com.github.xitren.data.data.line.ExtendedDataLine;
import com.github.xitren.data.data.container.DataContainer;
import com.github.xitren.data.data.container.StaticDataContainer;

import java.io.*;
import java.util.Iterator;

public class DataManagerFile<V extends DataLine<T>, T extends DataContainer> extends DataManager<V, T> {

    public DataManagerFile(V[] edl) {
        super(edl);
    }

    public static DataManager<ExtendedDataLine<StaticDataContainer>, StaticDataContainer> DataManagerFactory(
            String filename) throws IOException {
        InputStream inputStream = new FileInputStream(filename);
        Reader inputStreamReader = new InputStreamReader(inputStream);
        JsonParser ow = new ObjectMapper().reader().createParser(inputStream);
        DataModelJson data = ow.readValueAs(DataModelJson.class);
        inputStreamReader.close();
        ExtendedDataLine[] odl = new ExtendedDataLine[data.data.length];
        for (int i = 0;i < odl.length;i++) {
            odl[i] = new ExtendedDataLine(new StaticDataContainer(data.data[i]), data.data_label[i]);
        }
        DataManager dm = new DataManager(odl);
        for (int i=0;i < data.name.length;i++) {
            dm.marks.add(new Mark(data.channel[i], data.start[i], data.finish[i], data.name[i],
                    data.color[i], data.label_color[i]));
        }
        dm.swapper = new Integer[odl.length];
        for (int i=0;i < dm.swapper.length;i++) {
            dm.swapper[i] = i;
        }
        return dm;
    }

    public void saveToFile(String filename) throws IOException {
        DataModelJson data = new DataModelJson(dataLines.length, marks.size());
        int i;
        for (i = 0;i < dataLines.length;i++) {
            V dlds = dataLines[i];
            data.data[i] = dlds.toArray();
            data.data_label[i] = dlds.getName();
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
        DataModelJson data = new DataModelJson(dataLines.length, marks.size());
        int i;
        int length = 0;
        for (i = 0;i < dataLines.length;i++) {
            V dlds = dataLines[i];
            length = data.data[i].length;
            data.data[i] = dlds.toArray();
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
        outputStreamWriter.write(String.format("%d\t<Общее количество каналов>\n", dataLines.length));
        outputStreamWriter.write(String.format("%d\t<Частота дискретизации, Гц>\n", data.discretization));
        outputStreamWriter.write(String.format("1\t<Количество групп физиологических сигналов>\n"));
        outputStreamWriter.write(String.format("%d\t<Количество сигналов>\n", dataLines.length));
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
}
