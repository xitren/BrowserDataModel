package com.github.xitren.data;

public class Mark {
    public int start;
    public int finish;
    public String name;
    public int channel;
    public String color;
    public String label_color;

    public Mark(int channel, int start, int finish, String name,
                String color, String label_color) {
        this.start = start;
        this.finish = finish;
        this.name = name;
        this.channel = channel;
        this.color = color;
        this.label_color = label_color;
    }

    public String getName() {
        return name;
    }

    public String getLabelColor() {
        return label_color;
    }

    public String getColor() {
        return color;
    }

    public String getWebColor() {
        if (!color.contains("#")) {
            color = "#" + color;
        }
        if (color.length() > 6) {
            return color.substring(0, 7);
        } else {
            return color;
        }
    }

    public String getWebLabelColor() {
        if (!label_color.contains("#")) {
            label_color = "#" + label_color;
        }
        if (label_color.length() > 6) {
            return label_color.substring(0, 7);
        } else {
            return label_color;
        }
    }
}
