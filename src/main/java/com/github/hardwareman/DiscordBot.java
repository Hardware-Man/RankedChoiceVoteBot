package com.github.hardwareman;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class DiscordBot {
    public static void main(String[] args) {
        // Insert your bot's token here
        String token = "ODI1NDU5MTcwNzY0NzgzNzE3.YF-Owg.-y8s5DvxU6Pjm8t52fVLINgSNLY";

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Bong!");
            }
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }
}
