package net.whispwriting.the_conductor.discord.commands.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.commands.Command;
import net.whispwriting.the_conductor.discord.util.Profile;

import java.util.List;

public class Apply implements Command {

    @Override
    public void onCommand(SlashCommandInteractionEvent event, User sender, String label, List<OptionMapping> args, TextChannel channel) {
        if (args.size() < 4){
            event.reply("Please supply all the requested info for your DJ application.").setEphemeral(true).queue();
            return;
        }

        Profile profile = Profile.buildFromApplication(sender.getId(), args.get(0).getAsString(),
                args.get(1).getAsAttachment().getUrl(), args.get(2).getAsString(), args.get(3).getAsString());

        if (!Conductor.ApplicationManager.addApplication(profile, sender.getId())){
            event.reply("Looks like you've already submitted an application. Please wait for it to be reviewed." +
                    " If this is wrong, please contact a staff member for assistance").setEphemeral(true).queue();
        }else{
            MessageCreateBuilder builder = new MessageCreateBuilder();
            builder.addContent("Your application has been submitted. It will be reviewed shortly. Here is a preview of " +
                    "your DJ profile. You may edit it at any time if your application is accepted.")
                    .addEmbeds(profile.getProfileEmbed());
            event.reply(builder.build()).queue();
        }
    }
}
