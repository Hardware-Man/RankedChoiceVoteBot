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

public class DiscordBot {
    public static void main(String[] args) {
        // Insert your bot's token here
        String token = "ODI1NDU5MTcwNzY0NzgzNzE3.YF-Owg.-y8s5DvxU6Pjm8t52fVLINgSNLY";

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        ArrayList<Election> elections = new ArrayList<>();
        int currElection = 0;

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            StringTokenizer st = new StringTokenizer(event.getMessageContent());
            if (event.isServerMessage()) {
                if (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    if (tok.equalsIgnoreCase("!votehelp")) {
                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("Command List")
                                .setDescription("`!startvote [title] [candidate1],[candidate2],...` - Start a vote\n" +
                                        "`!endvote [electionid]` - End a running vote\n" +
                                        "`!votestatus [electionid]` - Show a currently running election's status (Graphical Representation)\n" +
                                        "`!getcandidates [electionid]` - Show a currently running election's Candidates")
                                .setColor(new Color(245, 132, 66))
                                .setThumbnail("https://i.ytimg.com/vi/P10PFuBFVL8/maxresdefault.jpg");
                        event.getChannel().sendMessage(embed);
                    } else if (tok.equalsIgnoreCase("!startvote")) {
                        EmbedBuilder embed = new EmbedBuilder();
                        if (st.hasMoreTokens()) {
                            tok = st.nextToken(" ");
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
                            elections.add(new Election(tok, eID));
                            embed.setTitle(tok);
                            StringBuilder candidates = new StringBuilder();
                            if (st.hasMoreTokens()) {
                                tok = st.nextToken(",");
                                tok = tok.replaceAll("\\s+", " ");
                                elections.get(currElection).addCandidate(new Candidate(tok));
                                candidates.append(tok);
                                while (st.hasMoreTokens()) {
                                    tok = st.nextToken("\t\n\r\f,");
                                    tok = tok.replaceAll("\\s+", " ");
                                    elections.get(currElection).addCandidate(new Candidate(tok));
                                    candidates.append("\n").append(tok);
                                }
                                embed.addField("Candidates", candidates.toString())
                                        .addField("Election ID", eID)
                                        .setThumbnail("https://i.ytimg.com/vi/P10PFuBFVL8/maxresdefault.jpg");
                                event.getChannel().sendMessage(embed);
                            } else {
                                event.getChannel().sendMessage("State at least one candidate.");
                            }
                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Invalid Start of Vote")
                                    .setDescription("Please state the election's title and at least one candidate.\n" +
                                            "Command format: `!startvote [title] [candidate1],[candidate2],...`"));
                        }
                    } else if (tok.equalsIgnoreCase("!getcandidates")) {
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
                                }
                            }
                            if (selectedEle != null) {
                                selectedEle.voters.add(new Voter(event.getMessageAuthor().getDiscriminatedName()));
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setTitle("Joined Election Successfully")
                                        .setDescription("You have joined the election: " + selectedEle.getElectionName() + "!"));
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
}
