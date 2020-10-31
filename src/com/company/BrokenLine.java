package com.company;

import java.util.ArrayList;
import java.util.List;

public class BrokenLine {
    int id;
    List<RealPoint> brokenLine = new ArrayList<>();

    public BrokenLine(int id) {
        this.id = id;
    }

    public List<RealPoint> getBrokenLine() {
        return brokenLine;
    }
}
