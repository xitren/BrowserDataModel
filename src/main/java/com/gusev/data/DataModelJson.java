package com.gusev.data;

public class DataModelJson {
    public double[][] data;
    public int[] start;
    public int[] finish;
    public String[] name;
    public int[] channel;
    public double[] red;
    public double[] green;
    public double[] blue;

    public DataModelJson(){}

    public DataModelJson(int lines, int marks){
        this.data = new double[lines][];
        this.start = new int[marks];
        this.finish = new int[marks];
        this.name = new String[marks];
        this.channel = new int[marks];
        this.red = new double[marks];
        this.green = new double[marks];
        this.blue = new double[marks];
    }
}
