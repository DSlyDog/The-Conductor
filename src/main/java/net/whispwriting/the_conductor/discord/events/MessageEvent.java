package net.whispwriting.the_conductor.discord.events;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.whispwriting.the_conductor.discord.Conductor;

public class MessageEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.isFromType(ChannelType.TEXT) && !event.getAuthor().isBot()){
            if (Conductor.getInstance().getAnnouncerChannels().containsKey(event.getChannel().asTextChannel())){
                Conductor.getInstance().getAnnouncerChannels().get(event.getChannel().asTextChannel())
                        .sendMessage(event.getMessage().getContentRaw()).queue();
                event.getMessage().reply("Your announcement has been sent.").queue();
            }
        }
    }
}
