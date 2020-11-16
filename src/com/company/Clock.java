package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Clock extends Label{

    Timer timer = new Timer();

    public Clock(String text) {
        this.setText(text);
//        JFrame f = new JFrame("Seconds");
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.add(timeLabel);
//        f.pack();
//        f.setLocationRelativeTo(null);
//        f.setVisible(true);

    }

    public void start(){
        timer.schedule(new UpdateUITask(), 0, 1);
    }

    public void stop(){
        timer.cancel();
    }

    private class UpdateUITask extends TimerTask {
        int nSeconds = 0;

        @Override
        public void run() {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Elapsed e = new Elapsed();
                    setText(e.format(nSeconds++));
                }
            });
        }
    }
}
