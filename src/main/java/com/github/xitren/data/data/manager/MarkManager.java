package com.github.xitren.data.data.manager;

import com.github.xitren.data.data.Mark;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class MarkManager extends Observable {
    protected final List<Mark> marks = new LinkedList();

    public void clearMarks() {
        synchronized (marks) {
            marks.clear();
        }
    }

    protected void addMark(int ch, int start, int finish, String name,
                           String color, String label_color) {
        synchronized (marks) {
            marks.add(new Mark(ch, start, finish, name, color, label_color));
        }
    }

    protected void addGlobalMark(int start, int finish, String name,
                                 String color, String label_color) {
        synchronized (marks) {
            marks.add(new Mark(-1, start, finish, name, color, label_color));
        }
    }
}
