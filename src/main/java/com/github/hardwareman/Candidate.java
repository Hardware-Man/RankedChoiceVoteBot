package com.github.hardwareman;

public class Candidate {
    private final String name;
    private int rank = 0;

    public Candidate(String name) {
        this.name = name;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }
}
