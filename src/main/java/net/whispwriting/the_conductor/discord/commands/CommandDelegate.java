package net.whispwriting.the_conductor.discord.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.whispwriting.the_conductor.Main;
import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.commands.command.CreateAnnouncerChannel;
import net.whispwriting.the_conductor.discord.commands.command.TestCommand;

public class CommandDelegate {

    public static void registerCommands(Conductor bot){
        Main.getLogger().info("Initializing commands...");
        bot.getJDA().updateCommands().addCommands(
                Commands.slash("conductor", "test the conductor bot")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                        .addOption(OptionType.STRING, "op1", "op1")
                        .addOption(OptionType.STRING, "op2", "op2"),
                Commands.slash("announcer", "link an input channel to an announcement channel.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                        .addOption(OptionType.CHANNEL, "input", "input channel")
                        .addOption(OptionType.CHANNEL, "output", "output channel")
        ).queue();

        bot.registerCommand("conductor", new TestCommand());
        bot.registerCommand("announcer", new CreateAnnouncerChannel());
        Main.getLogger().info("Commands initialized");
    }
}
