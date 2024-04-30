package net.whispwriting.the_conductor.discord.commands.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.commands.Command;
import net.whispwriting.the_conductor.discord.util.Strings;

import java.util.List;

public class OpenDJApplications implements Command {
    @Override
    public void onCommand(SlashCommandInteractionEvent event, User sender, String label, List<OptionMapping> args, TextChannel channel) {
        if (sender.isBot()){
            return;
        }

        Member user = Conductor.getInstance().getJDA().getGuilds().get(0).getMemberById(sender.getIdLong());
        if (!user.getRoles().contains(Conductor.getInstance().getRole(Strings.ADMIN_ROLE, Conductor.SearchType.ID))){
            event.reply("You are not permitted to open DJ applications").setEphemeral(true).queue();
            return;
        }

        Button apply = Button.of(ButtonStyle.SUCCESS, "apply", "Apply");
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent("Greetings! \n\nDJ applications are open! Click the \"Apply\" button below to apply.");
        builder.addActionRow(apply);
        Conductor.getInstance().sendMessage(builder.build(), channel, 1000);
        event.reply("Success!").setEphemeral(true).queue();
    }
}
