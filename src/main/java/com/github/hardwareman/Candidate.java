package com.github.hardwareman;
import java.util.ArrayList;

public class Candidate {
    private final String name;
    private ArrayList<Integer> rank = new ArrayList<>();

    public Candidate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
