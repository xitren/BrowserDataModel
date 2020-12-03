package com.gusev.data;

public class DataModelJson {
    public double[][] data;
    public int[] start;
    public int[] finish;
    public String[] name;
    public int[] channel;
    public String[] color;
    public String[] label_color;

    public DataModelJson(){}

    public DataModelJson(int lines, int marks){
        this.data = new double[lines][];
        this.start = new int[marks];
        this.finish = new int[marks];
        this.name = new String[marks];
        this.channel = new int[marks];
        this.color = new String[marks];
        this.label_color = new String[marks];
    }
}
