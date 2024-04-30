package net.whispwriting.the_conductor.discord.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.util.Strings;

public class ButtonPress extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        String id = event.getButton().getId();
        if (id.contains("app_accept")){
            String discordID = id.substring(id.indexOf("+")+1);
            Conductor.ApplicationManager.acceptApplication(discordID);
            Conductor.getInstance().getJDA().getUserById(discordID).openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage("Congratulations! Your DJ application for Bass Station has been accepted!")).queue();
            Conductor.getInstance().getJDA().getGuilds().get(0).addRoleToMember(Conductor.getInstance().getJDA().getUserById(discordID), Conductor.getInstance().getRole(Strings.DJ_ROLE, Conductor.SearchType.ID)).queue();
            event.reply("This application has been accepted").setEphemeral(true).queue();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            event.getMessage().delete().queue();
        }

        if (id.contains("app_deny")){
            String discordID = id.substring(id.indexOf("+")+1);
            Conductor.ApplicationManager.denyApplication(discordID);
            Conductor.getInstance().getJDA().getUserById(discordID).openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage("Our sincerest apologies, unfortunately your DJ " +
                            "application for Bass Station has not been accepted at this time.")).queue();
            event.reply("Application denied").setEphemeral(true).queue();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            event.getMessage().delete().queue();
        }
    }
}
