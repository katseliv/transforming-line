package com.company.interfaces;

import com.company.ScreenPoint;
import java.awt.*;

public interface LineDrawer {
    void drawLine(ScreenPoint p1, ScreenPoint p2, Color color);
}
