package net.whispwriting.the_conductor.discord.commands.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.whispwriting.the_conductor.Main;
import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.commands.Command;

import java.util.List;

public class TestCommand implements Command {
    @Override
    public void onCommand(SlashCommandInteractionEvent event, User sender, String label, List<OptionMapping> args, TextChannel channel) {
        for (OptionMapping option : args){
            Conductor.getInstance().sendMessage(option.getAsString(), channel);
        }

        event.reply("This is a response!").setEphemeral(true).queue();
    }
}
