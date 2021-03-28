package com.github.hardwareman;
import java.util.ArrayList;

public class Candidate {
    private final String name;
    private int rank;

    public Candidate(String name) {
        this.name = name;
    }

    public int getRank(){
        return this.rank;
    }
    public void setRank(int newRank){
        this.rank = newRank;
    }
    public String getName() {
        return name;
    }
}
