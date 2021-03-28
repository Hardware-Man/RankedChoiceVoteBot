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
                                ArrayList<Candidate> Candidates = selectedEle.candidates;
                                ArrayList<Voter> Voters = selectedEle.voters;
                                //Work here Zach
                                //Option to print all output
                                //Option to show the results of rounds
                                boolean printing = false;
                                boolean showRounds = false;
                                ArrayList<Candidate> lost = new ArrayList<>();
                                int d = 0;
                                while(Candidate.size()>1){
                                    if(showRounds)   System.out.print("Round "+d+ ": ");
                                    //Calculate:

                                    //Initialize all ranks to zero
                                    for(int i = 0; i<Candidate.size(); i++){
                                        Candidate.get(i).rank = 0;
                                    }
                                    if(printing)System.out.println("Initialized vals to 0");
                                    //For each Voter
                                    for(int j = 0; j<Voters.size(); j++){
                                        Voter current = Voters.get(j);
                                        //check if their rankings is empty, and if so delete them
                                        if(current.rankings.isEmpty()){
                                            if(printing)System.out.println("Rankings are empty, so removing Voter"+j +" and redoing j="+j);
                                            Voters.remove(j);
                                            j--;
                                            continue;
                                        }
                                        //if their top choice lost, remove it and move stuff up.
                                        if(printing)System.out.println("Voter"+j+ " is not empty!!!!");
                                        boolean isInLost = false;
                                        for(int k = 0; k<lost.size(); k++){
                                            if(lost.get(k).name.equals(current.rankings.get(0))){
                                                isInLost = true;
                                            }
                                        }
                                        if(isInLost){
                                            if(printing)System.out.println("They appear in Lost!");
                                            current.rankings.remove(0);
                                            //if they now have no rankings, skip theem
                                            if(current.rankings.isEmpty()){
                                                if(printing)System.out.println("After getting rid of their lost person, they have no more rankings so removing the Voter");
                                                Voters.remove(j);
                                                j--;
                                                continue;
                                            }
                                        }
                                        //Their top choice is still in, add 1 to their rank
                                        if(printing)System.out.println("Top choice "+current.rankings.get(0)+" is still in, so adding 1 to:");
                                        int IndexOfCandidate = 0;
                                        for(int k = 0; k<Candidate.size(); k++){
                                            if(current.rankings.get(0).equals(Candidate.get(k).name)){
                                                IndexOfCandidate = k;
                                                break;
                                            }
                                        }
                                        if(printing)System.out.println(Candidate.get(IndexOfCandidate).name);
                                        Candidate.get(IndexOfCandidate).rank++;
                                    }

                                    //Run the Poll
                                    if(printing)System.out.println("RUNNING POLL!");
                                    if(showRounds)  System.out.println("Standings at the start:");
                                    for(int i = 0; i<Voters.size(); i++){
                                        if(printing) System.out.println("Voter"+i+": "+Voters.get(i).rankings);
                                    }
                                    for(int i = 0; i<Candidate.size(); i++){
                                        if(showRounds)    System.out.println("Candidate"+i+": "+Candidate.get(i).name+","+Candidate.get(i).rank);
                                    }
                                    //Initialize lowest to candidate 0
                                    Candidate lowest = Candidate.get(0);
                                    Candidate highest = Candidate.get(0);
                                    //Calculate votesToWin as 0.5 * the amount of Voters left.
                                    double votesToWin = 0.5*Voters.size();
                                    if(printing)System.out.println("RUNNING THE POLL, want to get above "+votesToWin+" votes to win.");
                                    //Quickly check whether anyone won with more than 1/2 the votes.
                                    for(int i = 0; i<Candidate.size(); i++){
                                        if(Candidate.get(i).rank>votesToWin){
                                            System.out.println("WINNER!!!!! "+Candidate.get(i).name+" with: "+Candidate.get(i).rank+" votes.");
                                            return;
                                        }
                                    }
                                    //Go through and see who's lowest
                                    for(int i = 0; i<Candidate.size(); i++){
                                        Candidate current = Candidate.get(i);
                                        if(printing)System.out.println("HERE AND "+current.name +" Has votes #: "+current.rank);

                                        //If we have less votes than the lowest, we're the lowest.
                                        if(current.rank>highest.rank){
                                            highest = current;
                                            if(printing)  System.out.println("Highest is: "+highest.name);

                                        }
                                        if(current.rank<=lowest.rank){
                                            lowest = current;
                                            if(printing) System.out.println("Lowest is: "+lowest.name);

                                        }
                                    }
                                    //Check for ties for lowest
                                    ArrayList<Integer> indicesOfTies = new ArrayList<Integer>();
                                    for(int m =0; m<Candidate.size(); m++){
                                        //if equal to lowest, add the index to the arrayList.
                                        if(Candidate.get(m).rank==lowest.rank){
                                            if(printing)System.out.println("Adding index: "+m+" to indicesOfTies");
                                            indicesOfTies.add(m);
                                        }
                                    }
                                    //No Tie for last, just remove last and add to removed list
                                    if(indicesOfTies.size()==1){
                                        if(showRounds)    System.out.print("Removed: "+lowest.name);
                                        lost.add(lowest);
                                        Candidate.remove(lowest);
                                    }
                                    //Tie for last, add them up and see if together they are >= highest. If yes, end vote. If no, remove them.
                                    else{
                                        if(printing)System.out.println("Tied for last");
                                        int valOfLowest = 0;
                                        int valOfHighest= highest.rank;
                                        String names = "";
                                        for(int s = 0; s<indicesOfTies.size(); s++){
                                            names +=Candidate.get(indicesOfTies.get(s)).name;
                                            if(s!=indicesOfTies.size()-1) names+=" ";
                                            valOfLowest+=Candidate.get(indicesOfTies.get(s)).rank;
                                            if(printing)System.out.println("valOfLowest ="+valOfLowest+" after adding: "+Candidate.get(indicesOfTies.get(s)).name+":"+Candidate.get(indicesOfTies.get(s)).rank);
                                        }
                                        //tie
                                        if(valOfLowest>=valOfHighest){
                                            System.out.println("Tie between the following! Lowest people: "+names +", with valOfLowest "+valOfLowest+" alongside the highest: "+highest.name+" has: "+valOfHighest);
                                            return;
                                        }
                                        //no tie
                                        else{
                                            if(printing)  System.out.println("Lowest "+valOfLowest+"< Highest "+valOfHighest);
                                            //For each last place, add to lost and remove from Candidate. Descending order so doesn't affect others.
                                            if(showRounds)    System.out.print("Removed: ");
                                            for(int n = indicesOfTies.size()-1; n>=0; n--){
                                                int removal = indicesOfTies.get(n);
                                                if(showRounds)    System.out.print(Candidate.get(removal).name+" ");
                                                lost.add(Candidate.get(removal));
                                                Candidate.remove(removal);
                                            }
                                        }
                                    }
                                    d++;
                                    if(showRounds)   System.out.println();
                                    if(showRounds)    System.out.println("Standings at the end of Round: "+d+" ");
                                    for(int i = 0; i<Voters.size(); i++){
                                        if(printing) System.out.println("Voter"+i+": "+Voters.get(i).rankings);
                                    }
                                    for(int i = 0; i<Candidate.size(); i++){
                                        if(showRounds)    System.out.println("Candidate"+i+": "+Candidate.get(i).name+","+Candidate.get(i).rank);
                                    }
                                    if(showRounds)   System.out.println();

                                }
                                if(Candidate.size()==1){
                                    System.out.println("WE HAVE A WINNER AS A LAST SURVIVOR! " + Candidate.get(0).name);
                                }
                                else if(Candidate.isEmpty()){
                                    System.out.println("WE HAVE NO Candidate LEFT! NO ONE WINS!");
                                }
                                else{
                                    System.out.println("WE SHOULDNT BE HERE");
                                }
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
