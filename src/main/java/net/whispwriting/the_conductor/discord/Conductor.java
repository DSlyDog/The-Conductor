package net.whispwriting.the_conductor.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.whispwriting.the_conductor.Main;
import net.whispwriting.the_conductor.discord.commands.Command;
import net.whispwriting.the_conductor.discord.commands.CommandHandler;
import net.whispwriting.the_conductor.discord.events.MessageEvent;
import net.whispwriting.the_conductor.discord.util.JsonFile;
import org.w3c.dom.Text;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conductor {
    private JDA jda;
    private JsonFile announcers;
    private CommandHandler handler = new CommandHandler();
    private Map<TextChannel, TextChannel> announcerChannels = new HashMap<>();
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
    public Channel getChannel(String id){
        Guild guild = jda.getGuilds().get(0);
        try {
            return guild.getChannelById(Channel.class, id);
        }catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    public Role getRole(String name){
        Guild guild = jda.getGuilds().get(0);
        try{
            return guild.getRolesByName(name, true).get(0);
        }catch(IndexOutOfBoundsException e){
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
    }