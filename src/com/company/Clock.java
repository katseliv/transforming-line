package com.company;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Clock extends Label{

    Timer timer;
    TimerTask timerTask;

    public Clock(String text) {
        this.setText(text);
    }

    public void start(){
        this.timer = new Timer();
        this.timerTask = new UpdateUITask();
        timer.schedule(timerTask, 0, 1);
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

        public int getSeconds() {
            return nSeconds;
        }
    }
}
