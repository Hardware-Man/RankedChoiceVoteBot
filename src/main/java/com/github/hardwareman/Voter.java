package com.github.hardwareman;

import java.util.ArrayList;

public class Voter {
    private final String name;
    private final ArrayList<String> rankings = new ArrayList<>();

    public Voter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getRankings() {
        return rankings;
    }

    public void addChoice(String choice) {
        rankings.add(choice);
    }
}
