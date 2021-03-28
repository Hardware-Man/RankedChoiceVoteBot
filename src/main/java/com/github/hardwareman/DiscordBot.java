package com.github.hardwareman;

//import java.awt.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
//import java.io.*;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class DiscordBot {
    public static void main(String[] args) {
        // Insert your bot's token here
        String token = "ODI1NDU5MTcwNzY0NzgzNzE3.YF-Owg.-y8s5DvxU6Pjm8t52fVLINgSNLY";

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        ArrayList<Election> elections = new ArrayList<>();
        final int[] currElection = {0};

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            StringTokenizer st = new StringTokenizer(event.getMessageContent());
            EmbedBuilder embed = new EmbedBuilder();
            if (event.isServerMessage()) {
                if (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    if (tok.equalsIgnoreCase("!votehelp")) {
                        embed.setTitle("Command List")
                                .setDescription("__**Server Commands:**__\n" +
                                        "`!startvote [title] [candidate1],[candidate2],...` - Start a vote\n" +
                                        "`!endvote [electionid]` - End a running vote\n" +
                                        "\n__**Private Message Commands:**__\n" +
                                        "`!joinvote [electionid]` - Join a currently running election\n" +
                                        "`!setvote [candidate1], [candidate2],...` - Declare your candidates in order of preference (not all candidates need to be voted for)" +
                                        "\n__**Global Commands:**__\n" +
                                        "`!votestatus [electionid]` - Indicates whether election is currently running/exists\n" +
                                        "`!getcandidates [electionid]` - Show a currently running election's Candidates\n")
                                .setColor(new Color(245, 132, 66))
                                .setThumbnail("https://i.ytimg.com/vi/P10PFuBFVL8/maxresdefault.jpg");
                        event.getChannel().sendMessage(embed);
                    } else if (tok.equalsIgnoreCase("!votestatus")) {
                        getVoteStatus(elections, event, st, embed);
                    } else if (tok.equalsIgnoreCase("!startvote")) {
                        if (st.hasMoreTokens()) {
                            tok = st.nextToken();
                            String eID = new Random().ints(48, 123)
                                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                                    .limit(6)
                                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                    .toString();
                            for (int j = 0; j < elections.size(); j++) {
                                if (elections.get(j).getElectionID().equals(eID)) {
                                    eID = new Random().ints(48, 123)
                                            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                                            .limit(6)
                                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                            .toString();
                                    j = 0;
                                }
                            }
                            if (st.hasMoreTokens()) {
                                elections.add(new Election(tok, eID));
                                embed.setTitle(tok);
                                StringBuilder candidates = new StringBuilder();
                                tok = st.nextToken(",");
                                tok = tok.replaceAll("\\s+", " ");
                                elections.get(currElection[0]).addCandidate(new Candidate(tok));
                                candidates.append(tok);
                                while (st.hasMoreTokens()) {
                                    tok = st.nextToken("\t\n\r\f,");
                                    tok = tok.replaceAll("\\s+", " ");
                                    elections.get(currElection[0]).addCandidate(new Candidate(tok));
                                    candidates.append("\n").append(tok);
                                }
                                currElection[0]++;
                                embed.addField("Candidates", candidates.toString())
                                        .addField("Election ID", eID)
                                        .setThumbnail("https://i.ytimg.com/vi/P10PFuBFVL8/maxresdefault.jpg");
                                event.getChannel().sendMessage(embed);
                            } else {
                                event.getChannel().sendMessage(embed.setTitle("Invalid Start of Vote")
                                        .setDescription("Please state at least one candidate.\n" +
                                                "Command format: `!startvote [title] [candidate1],[candidate2],...`"));
                            }
                        } else {
                            event.getChannel().sendMessage(embed.setTitle("Invalid Start of Vote")
                                    .setDescription("Please state the election's title and at least one candidate.\n" +
                                            "Command format: `!startvote [title] [candidate1],[candidate2],...`"));
                        }
                    } else if (tok.equalsIgnoreCase("!getcandidates")) {
                        getCandidateList(elections, event, st);
                    } else if (tok.equalsIgnoreCase("!endvote")) {
                        if (st.hasMoreTokens()) {
                            String eleID = st.nextToken();
                            int eleIndex = -1;
                            for (int i = 0; i < elections.size(); i++) {
                                if (elections.get(i).getElectionID().equals(eleID)) {
                                    eleIndex = i;
                                    break;
                                }
                            }

                            if (eleIndex != -1) {
                                Election selectedEle = elections.get(eleIndex);
                                ArrayList<Candidate> candidates = selectedEle.candidates;
                                ArrayList<Voter> voters = selectedEle.voters;
                                //Work here Zach
                            }
                        }
                    }
                }
            } else {
                if(st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    if (tok.equalsIgnoreCase("!joinvote")) {
                        if(st.hasMoreTokens()) {
                            tok = st.nextToken();
                            Election selectedEle = null;
                            for(Election e : elections) {
                                if(e.getElectionID().equals(tok)) {
                                    selectedEle = e;
                                    break;
                                }
                            }
                            if (selectedEle != null) {
                                selectedEle.voters.add(new Voter(event.getMessageAuthor().getDiscriminatedName()));
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setTitle("Joined Election Successfully")
                                        .setDescription("You have joined the election: " + selectedEle.getElectionName() + "!\n" +
                                                "Cast your votes using the `!setvotes` command."));
                            } else {
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setTitle("Invalid Join")
                                        .setDescription("Election does not exist!"));
                            }
                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Invalid Join")
                                    .setDescription("Please state the election ID.\n" +
                                            "Command format: `!joinvote [electionid]`"));
                        }
                    }  else if (tok.equalsIgnoreCase("!votestatus")) {
                        getVoteStatus(elections, event, st, embed);
                    } else if (tok.equalsIgnoreCase("!getcandidates")) {
                        getCandidateList(elections, event, st);
                    } else if (tok.equalsIgnoreCase("!setvotes")) {
                        if(st.hasMoreTokens()) {
                            tok = st.nextToken();
                            Election selectedEle = null;
                            for(Election e : elections) {
                                if(e.getElectionID().equals(tok)) {
                                    selectedEle = e;
                                    break;
                                }
                            }
                            if (selectedEle != null) {
                                Voter currVoter = null;
                                for(Voter v : selectedEle.voters) {
                                    if(v.getName().equals(event.getMessageAuthor().getDiscriminatedName())) {
                                        currVoter = v;
                                        break;
                                    }
                                }
                                if (currVoter != null) {
                                    if (st.hasMoreTokens()) {
                                        StringBuilder candidates = new StringBuilder();
                                        tok = st.nextToken(",");
                                        tok = tok.replaceAll("\\s+", " ");
                                        currVoter.addChoice(tok);
                                        candidates.append(tok);
                                        while (st.hasMoreTokens()) {
                                            tok = st.nextToken("\t\n\r\f,");
                                            tok = tok.replaceAll("\\s+", " ");
                                            currVoter.addChoice(tok);
                                            candidates.append("\n").append(tok);
                                        }
                                        event.getChannel().sendMessage(new EmbedBuilder()
                                                .setTitle("Votes Set Successfully")
                                                .addField("Candidate Choices (Listed in Order of Preference): " , candidates.toString()));
                                    } else {
                                        event.getChannel().sendMessage(new EmbedBuilder()
                                                .setTitle("Invalid Vote")
                                                .setDescription("Please rank at least one candidate."));
                                    }
                                } else {
                                    event.getChannel().sendMessage(new EmbedBuilder()
                                            .setTitle("Invalid Vote")
                                            .setDescription("You are not registered in this election! Please first register in the election with the `!joinvote` command."));
                                }
                            } else {
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setTitle("Invalid Join")
                                        .setDescription("Election does not exist!"));
                            }
                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Invalid Join")
                                    .setDescription("Please state the election ID.\n" +
                                            "Command format: `!joinvote [electionid]`"));
                        }
                    }
                }
            }
        });
        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }

    private static void getVoteStatus(ArrayList<Election> elections, MessageCreateEvent event, StringTokenizer st, EmbedBuilder embed) {
        String tok;
        if (st.hasMoreTokens()) {
            tok = st.nextToken();
            Election searchEle = null;
            for (Election e : elections) {
                if (e.getElectionID().equals(tok)) {
                    searchEle = e;
                    break;
                }
            }
            if (searchEle != null) {
                StringBuilder cans = new StringBuilder();
                for (Candidate c : searchEle.candidates) {
                    cans.append(c.getName()).append("\n");
                }
                event.getChannel().sendMessage(embed.setTitle("Successful Search")
                        .setDescription("Election Found: " + searchEle.getElectionName())
                        .addField("Candidates",cans.toString()));
            } else {
                event.getChannel().sendMessage(embed.setTitle("Unsuccessful Search")
                        .setDescription("No such election found :("));
            }
        }
    }

    private static void getCandidateList(ArrayList<Election> elections, MessageCreateEvent event, StringTokenizer st) {
        StringBuilder sb = new StringBuilder();
        if (st.hasMoreTokens()) {
            String eleID = st.nextToken();
            for (Election ele : elections) {
                if (ele.getElectionID().equals(eleID)) {
                    for (Candidate candid : ele.candidates) {
                        sb.append(candid.getName()).append("\n");
                    }
                }
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Candidate List")
                    .setDescription(sb.toString()));
        }
    }
}
