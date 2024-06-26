package net.whispwriting.the_conductor.discord.commands.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.commands.Command;
import net.whispwriting.the_conductor.discord.util.JsonFile;

import java.io.IOException;
import java.util.List;

public class CreateAnnouncerChannel implements Command {
    @Override
    public void onCommand(SlashCommandInteractionEvent event, User sender, String label, List<OptionMapping> args, TextChannel channel) {
        if (args.size() <2){
            event.reply("You must supply both an input channel and an output channel.").setEphemeral(true).queue();
            return;
        }

        JsonFile file = Conductor.getInstance().getAnnouncerFile();

        TextChannel input = args.get(0).getAsChannel().asTextChannel();
        TextChannel output = args.get(1).getAsChannel().asTextChannel();

        if (Conductor.getInstance().getAnnouncerChannels().containsKey(input)){
            event.reply("That input channel is already in use.").setEphemeral(true).queue();
            return;
        }

        file.set(input.getId(), output.getId());
        try{
            file.save();
            Conductor.getInstance().getAnnouncerChannels().put(input, output);
            event.reply("Messages in " + input.getAsMention() + " will now be announced in " + output.getAsMention()).setEphemeral(true).queue();
        }catch(IOException e){
            System.err.println("Failed to save announcer hook to file");
            e.printStackTrace();
            event.reply("Could not save the announcer configuration. Please check the console logs.").setEphemeral(true).queue();
        }
    }
}
