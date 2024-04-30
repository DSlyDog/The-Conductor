package net.whispwriting.the_conductor.discord.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.whispwriting.the_conductor.Main;
import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.commands.command.Apply;
import net.whispwriting.the_conductor.discord.commands.command.CloseDJApplications;
import net.whispwriting.the_conductor.discord.commands.command.CreateAnnouncerChannel;
import net.whispwriting.the_conductor.discord.commands.command.OpenDJApplications;

public class CommandDelegate {

    public static void registerCommands(Conductor bot){
        Main.getLogger().info("Initializing commands...");
        bot.getJDA().getGuilds().get(0).updateCommands().addCommands(
                Commands.slash("announcer", "link an input channel to an announcement channel.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                        .addOption(OptionType.CHANNEL, "input", "input channel")
                        .addOption(OptionType.CHANNEL, "output", "output channel"),
                Commands.slash("apply", "Apply to be a DJ")
                        .addOption(OptionType.STRING, "dj_name", "Your DJ stage name")
                        .addOption(OptionType.STRING, "vrc_name", "Your name on VRChat")
                        .addOption(OptionType.ATTACHMENT, "logo", "Your DJ Logo")
                        .addOption(OptionType.STRING, "genre", "Your genre")
                        .addOption(OptionType.STRING, "demo", "Link to your demo set"),
                Commands.slash("open_dj_apps", "Open DJ applications"),
                Commands.slash("close_dj_apps", "Close DJ Applications")
        ).queue();

        bot.registerCommand("announcer", new CreateAnnouncerChannel());
        bot.registerCommand("apply", new Apply());
        bot.registerCommand("open_dj_apps", new OpenDJApplications());
        bot.registerCommand("close_dj_apps", new CloseDJApplications());
        Main.getLogger().info("Commands initialized");
    }
}
