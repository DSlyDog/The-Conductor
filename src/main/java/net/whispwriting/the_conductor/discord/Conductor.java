package net.whispwriting.the_conductor.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
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
import net.whispwriting.the_conductor.Main;
import net.whispwriting.the_conductor.discord.commands.Command;
import net.whispwriting.the_conductor.discord.commands.CommandHandler;
import net.whispwriting.the_conductor.discord.events.*;
import net.whispwriting.the_conductor.discord.util.JsonFile;
import net.whispwriting.the_conductor.discord.util.Profile;
import net.whispwriting.the_conductor.discord.util.Strings;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
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

    private String applicationMessageID = "";
    private boolean isAcceptingApplications;

    public enum SearchType{
        NAME,
        ID
    }
    private static Conductor instance;
    private Conductor(){}
        public void init(String token) throws LoginException {
        Thread postLoad = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    loadAnnouncers();
                    loadDJProfiles();
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
        jda.addEventListener((new ApplyButton()));
        jda.addEventListener(new CreateProfileButton());
        jda.addEventListener(new UpdateProfileButton());
        avatar = jda.getSelfUser().getAvatarUrl();
    }

    private void loadAnnouncers(){
        if (!announcers.isEmpty()) {
            Guild guild = jda.getGuilds().get(0);
            for (Channel channel : guild.getChannels()) {
                String id = announcers.getString(channel.getId());
                if (id != null) {
                    Channel output = getChannel(id, SearchType.ID);
                    if (output.getType() == ChannelType.TEXT) {
                        announcerChannels.put((TextChannel) channel, (TextChannel) output);
                    }
                }
            }
        }
    }

    private void loadDJProfiles(){
        for (Member member : jda.getGuilds().get(0).getMembersWithRoles(jda.getRoleById(Strings.DJ_ROLE))){
            if (JsonFile.exists("dj_profiles", member.getId())){
                Profile profile = Profile.loadFromFile(member.getId());
                profiles.put(member.getId(), profile);
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
    public void sendMessage(String message, TextChannel channel, long delay){
        channel.sendTyping().queue();
        try{
            Thread.sleep(delay);
            channel.sendMessage(message).queue();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(MessageCreateData message, TextChannel channel, long delay){
        channel.sendTyping().queue();
        try{
            Thread.sleep(delay);
            channel.sendMessage(message).queue();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public Message sendMessageWithReturn(String message, TextChannel channel, long delay){
        channel.sendTyping().queue();
        try{
            Thread.sleep(delay);
            return channel.sendMessage(message).complete();
        }catch (InterruptedException e){
            e.printStackTrace();
            return null;
        }
    }

    public Message sendMessageWithReturn(MessageCreateData message, TextChannel channel, long delay){
        channel.sendTyping().queue();
        try{
            Thread.sleep(delay);
            return channel.sendMessage(message).complete();
        }catch (InterruptedException e){
            e.printStackTrace();
            return null;
        }
    }

    public Channel getChannel(String key, SearchType type){
        switch (type){
            case NAME:
                return getChannelByName(key);
            case ID:
                return getChannelByID(key);
            default:
                return null;
        }
    }

    private Channel getChannelByID(String id){
        Guild guild = jda.getGuilds().get(0);
        try {
            return guild.getChannelById(TextChannel.class, id);
        }catch(IndexOutOfBoundsException e){
            return null;
        }
    }

    private Channel getChannelByName(String name){
        Guild guild = jda.getGuilds().get(0);
        return guild.getTextChannelsByName(name, true).get(0);
    }

    public Role getRole(String key, SearchType type) {
        switch (type){
            case NAME:
                return getRoleByName(key);
            case ID:
                return getRoleByID(key);
            default:
                return null;
        }
    }

    private Role getRoleByName(String name){
        Guild guild = jda.getGuilds().get(0);
        try {
            return guild.getRolesByName(name, true).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private Role getRoleByID(String id){
        Guild guild = jda.getGuilds().get(0);
        return guild.getRoleById(id);
    }

    public Profile getProfile(Member member){
        return profiles.get(member.getId());
    }

    public void addProfile(Member member, Profile profile){
        profiles.put(member.getId(), profile);
    }

    public List<TextChannel> getChannels(){
        return jda.getTextChannels();
    }

    public void setApplicationMessageID(String applicationMessageID){
        this.applicationMessageID = applicationMessageID;
    }

    public String getApplicationMessageID(){
        return this.applicationMessageID;
    }

    public void openDJApplications(){
        this.isAcceptingApplications = true;
    }

    public void closeDJApplicaations(){
        this.isAcceptingApplications = false;
    }

    public void stop(){
        jda.shutdown();
    }

    public static Conductor getInstance() {
        if (instance == null)
            instance = new Conductor();
        return instance;
    }

    public class ApplicationManager {
        private static final TextChannel APPLICATION_CHANNEL = Conductor.getInstance().jda.getTextChannelById("1194827653279141938");
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
                    .addEmbeds(profile.getProfileEmbed())
                            .addActionRow(accept, deny);

            Conductor.getInstance().sendMessage(builder.build(), APPLICATION_CHANNEL, 1000);
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