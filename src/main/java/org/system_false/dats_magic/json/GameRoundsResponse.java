package org.system_false.dats_magic.json;

import java.util.List;

public class GameRoundsResponse {
    private String gameName;
    private String now;
    private List<Round> rounds;

    private GameRoundsResponse() {}

    public GameRoundsResponse(String gameName, String now, List<Round> rounds) {
        this.gameName = gameName;
        this.now = now;
        this.rounds = rounds;
    }

    public String getGameName() {
        return gameName;
    }

    public String getNow() {
        return now;
    }

    public List<Round> getRounds() {
        return rounds;
    }
}
