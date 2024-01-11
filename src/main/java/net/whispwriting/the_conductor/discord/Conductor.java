package net.whispwriting.the_conductor.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.whispwriting.the_conductor.Main;
import net.whispwriting.the_conductor.discord.commands.Command;
import net.whispwriting.the_conductor.discord.commands.CommandHandler;
import net.whispwriting.the_conductor.discord.events.ButtonPress;
import net.whispwriting.the_conductor.discord.events.MessageEvent;
import net.whispwriting.the_conductor.discord.util.JsonFile;
import net.whispwriting.the_conductor.discord.util.Profile;
import org.w3c.dom.Text;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conductor {
    private JDA jda;
    private JsonFile announcers;
    private final CommandHandler handler = new CommandHandler();
    private Map<TextChannel, TextChannel> announcerChannels = new HashMap<>();
    private static Map<String, Profile> applications = new HashMap<>();
    private Map<String, Profile> profiles = new HashMap<>();
    private String avatar;
    private static Conductor instance;
    private Conductor(){}
        public void init(String token) throws LoginException {
        Thread postLoad = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    loadAnnouncers();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Main.getLogger().info("Assets loaded");
            }
        });
        jda = JDABuilder.createDefault(token)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_BANS,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.SCHEDULED_EVENTS,
                        GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE)
                .build();
        jda.getPresence().setActivity(Activity.of(Activity.ActivityType.PLAYING, "VRChat"));
        announcers = new JsonFile("announcers", System.getProperty("user.dir"));
        postLoad.start();
        jda.addEventListener(handler);
        jda.addEventListener(new MessageEvent());
        jda.addEventListener(new ButtonPress());
        avatar = jda.getSelfUser().getAvatarUrl();
    }

    private void loadAnnouncers(){
        if (!announcers.isEmpty()) {
            Guild guild = jda.getGuilds().get(0);
            for (Channel channel : guild.getChannels()) {
                String id = announcers.getString(channel.getId());
                if (id != null) {
                    Channel output = getChannel(id);
                    if (output.getType() == ChannelType.TEXT) {
                        announcerChannels.put((TextChannel) channel, (TextChannel) output);
                    }
                }
            }
        }
    }
    public JDA getJDA() {
        return jda;
    }
    public String getAvatar() {
        return avatar;
    }
    public Map<TextChannel, TextChannel> getAnnouncerChannels() {
        return announcerChannels;
    }

    public JsonFile getAnnouncerFile(){
        return announcers;
    }
    public void setPresence(Activity activity){
        jda.getPresence().setActivity(activity);
    }
    public void registerCommand(String label, Command command){
        handler.registerCommand(label, command);
    }
    public void sendMessage(String message, TextChannel channel){
        channel.sendTyping().queue();
        try{
            Thread.sleep(100);
            channel.sendMessage(message).queue();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(MessageCreateData message, TextChannel channel){
        channel.sendTyping().queue();
        try{
            Thread.sleep(100);
            channel.sendMessage(message).queue();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public Channel getChannel(String id){
        Guild guild = jda.getGuilds().get(0);
        try {
            return guild.getChannelById(Channel.class, id);
        }catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    public Role getRole(String name) {
        Guild guild = jda.getGuilds().get(0);
        try {
            return guild.getRolesByName(name, true).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    public List<TextChannel> getChannels(){
        return jda.getTextChannels();
    }

    public void stop(){
        jda.shutdown();
    }
    public static Conductor getInstance(){
        if (instance == null)
            instance = new Conductor();
        return instance;
    }

    public class ApplicationManager {
        private static final TextChannel APPLICATION_CHANNEL = Conductor.getInstance().jda.getTextChannelById("725539958399565885");
        public static boolean addApplication(Profile profile, String discordID){
            if (applications.containsKey(discordID))
                return false;

            applications.put(discordID, profile);
            return true;
        }

        public static void sendApplicationMessage(User applicant, Profile profile){
            Button accept = Button.of(ButtonStyle.SUCCESS, "app_accept+" + applicant.getId(), "Accept");
            Button deny = Button.of(ButtonStyle.DANGER, "app_deny+" + applicant.getId(), "Reject");

            MessageCreateBuilder builder = new MessageCreateBuilder()
                    .addEmbeds(new EmbedBuilder()
                            .setColor(Color.CYAN)
                            .setTitle("This is a message with a button!")
                            .setDescription("Please click a button")
                            .build())
                    .addActionRow(accept, deny);

            Conductor.getInstance().sendMessage(builder.build(), Conductor.getInstance().getChannels().get(0));
        }

        public static boolean acceptApplication(String discordID){
            Profile profile = applications.remove(discordID);
            Conductor.getInstance().profiles.put(discordID, profile);
            return profile.newSave();
        }

        public static void denyApplication(String discordID){
            applications.remove(discordID);
        }
    }
}