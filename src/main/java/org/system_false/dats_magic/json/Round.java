package org.system_false.dats_magic.json;

import java.util.Date;

public class Round {
    private int duration;
    private Date endAt;
    private String name;
    private int repeat;
    private Date startAt;
    private String status;

    private Round() {}

    public Round(int duration, Date endAt, String name, int repeat, Date startAt, String status) {
        this.duration = duration;
        this.endAt = endAt;
        this.name = name;
        this.repeat = repeat;
        this.startAt = startAt;
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public Date getEndAt() {
        return endAt;
    }

    public String getName() {
        return name;
    }

    public int getRepeat() {
        return repeat;
    }

    public Date getStartAt() {
        return startAt;
    }

    public String getStatus() {
        return status;
    }
}
