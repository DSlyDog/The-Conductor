package net.whispwriting.the_conductor.discord.commands.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.commands.Command;
import net.whispwriting.the_conductor.discord.util.Strings;
import net.whispwriting.the_conductor.discord.util.TickerBoardJsonBuilder;

import java.util.List;

public class TickerSetWelcome implements Command {
    @Override
    public void onCommand(SlashCommandInteractionEvent event, User sender, String label, List<OptionMapping> args, TextChannel channel) {
        if (sender.isBot())
            return;

        Member user = Conductor.getInstance().getJDA().getGuilds().get(0).getMemberById(sender.getIdLong());
        if (!user.getRoles().contains(Conductor.getInstance().getRole(Strings.ADMIN_ROLE, Conductor.SearchType.ID))){
            event.reply("You are not permitted to open DJ applications").setEphemeral(true).queue();
            return;
        }

        if (args.size() < 4){
            event.reply("All fields must be provided to update.").setEphemeral(true).queue();
            return;
        }

        String[] ticker = args.get(3).getAsString().split(",");

        TickerBoardJsonBuilder.getInstance().setWelcome(args.get(0).getAsString(), args.get(1).getAsString(), args.get(2).getAsInt(), ticker);
    }
}
