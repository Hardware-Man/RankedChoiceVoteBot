package com.github.hardwareman;

import java.util.ArrayList;

public class Election {
    private final String electionName;
    private final String electionID;
    private long serverID;
    ArrayList<Candidate> candidates = new ArrayList<>();
    ArrayList<Voter> voters = new ArrayList<>();
    public Election(String electionName, String electionID) {
        this.electionName = electionName;
        this.electionID = electionID;
    }

    public void addCandidate(Candidate candid) {
        candidates.add(candid);
    }

    public void addVoter(Voter voter) {
        voters.add(voter);
    }

    public String getElectionName() {
        return electionName;
    }

    public String getElectionID() {
        return electionID;
    }
}
