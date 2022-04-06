package com.example.gamemg;

public class RecordBean {
    private int step;
    private String time;

    public RecordBean(int step, String time) {
        this.step = step;
        this.time = time;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
