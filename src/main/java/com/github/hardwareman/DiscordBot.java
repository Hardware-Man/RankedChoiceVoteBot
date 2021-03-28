package com.github.hardwareman;

//import java.awt.*;
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

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.isServerMessage()) {
                StringTokenizer st = new StringTokenizer(event.getMessageContent());
                if (st.hasMoreTokens() && st.nextToken().equalsIgnoreCase("!startvote")) {
                    EmbedBuilder embed = new EmbedBuilder();
                    if (st.hasMoreTokens()) {
                        embed.setTitle(st.nextToken());
                        StringBuilder candidates = new StringBuilder();
                        if(st.hasMoreTokens()) {
                            candidates.append(st.nextToken("\t\n\r\f,"));
                            while (st.hasMoreTokens()) {
                                String tok = st.nextToken("\t\n\r\f,");

                                candidates.append(", ").append(tok);
                            }
                            embed.addField("Candidates", candidates.toString());
                            event.getChannel().sendMessage(embed);
                        }
                        else {
                            event.getChannel().sendMessage("State at least one candidate.");
                        }
                    }
                    else {
                        event.getChannel().sendMessage("State the election title.");
                    }
                }
            }
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }
}
