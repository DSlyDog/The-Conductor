package net.whispwriting.the_conductor.discord.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.util.Strings;

import java.util.EnumSet;
import java.util.Random;

public class ApplyButton extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        if (event.getButton().getId().equals("apply") && !event.isAcknowledged()){
            if (event.getMember().getRoles().contains(Conductor.getInstance().getRole(Strings.DJ_ROLE, Conductor.SearchType.ID))){
                event.reply("You're already a DJ, silly!").setEphemeral(true).queue();
                return;
            }

            Random random = new Random(System.currentTimeMillis());
            int ticketID = random.nextInt(100000, 1000000);
            TextChannel channel = Conductor.getInstance().getJDA().getGuilds().get(0).getCategoryById(Strings.DJ_APPLICATION_CATEGORY).createTextChannel("application-" + ticketID)
                    .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(Conductor.getInstance().getJDA().getRoleById(Strings.ADMIN_ROLE), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(event.getMember().getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL)).complete();
            ApplicationConversation conversation = new ApplicationConversation(event.getMember(), channel, ticketID);
            Conductor.getInstance().getJDA().addEventListener(conversation);
            conversation.start();
            event.reply("Application opened in <#" + channel.getId() + ">").setEphemeral(true).queue();
        }
    }
}
