package org.system_false.dats_magic.json;

import java.util.Date;
import java.util.List;

public class GameRoundsResponse {
    private String gameName;
    private String now;
    private List<Round> rounds;

    private GameRoundsResponse() {}

    public String getGameName() {
        return gameName;
    }

    public String getNow() {
        return now;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public Round getCurrentRound() {
        Date now = new Date();
        for (Round round : rounds) {
            if (round.getStartAt().after(now) && now.before(round.getEndAt())) {
                return round;
            }
        }
        return null;
    }
}
