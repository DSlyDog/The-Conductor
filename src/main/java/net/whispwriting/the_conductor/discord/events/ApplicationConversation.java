package net.whispwriting.the_conductor.discord.events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.whispwriting.the_conductor.Main;
import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.util.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationConversation extends ListenerAdapter {

    private Member member;
    private TextChannel channel;
    private int ticketID;
    private int step = 0;

    private Profile profile;
    private Map<Integer, String> questions = new HashMap<>();
    private Map<Integer, String> responses = new HashMap<>();
    private String vrcName;
    private String name;
    private String logo;
    private List<String> genres = new ArrayList<>();
    private List<String> demos = new ArrayList<>();
    private List<String> socials = new ArrayList<>();

    public ApplicationConversation(Member member, TextChannel channel, int ticketID){
        this.member = member;
        this.channel = channel;
        this.ticketID = ticketID;

        questions.put(0, "What is your DJ name?");
        questions.put(1, "What is your VRChat name?");
        questions.put(2, "What genres do you play? Please add each in its own message. When you are done, press \"Done\"");
        questions.put(3, "What are your socials? Please add each in its own message. When you are done, press \"Done\"");
        questions.put(4, "What's your logo? Please attach it to your message.");
        questions.put(5, "Please provide a link to a demo set. It should be no longer than 60 minutes");
        questions.put(6, "This is your profile. If everything looks good, I'll send it off");

        responses.put(0, "Okay, your DJ name is: %%. Is this correct?");
        responses.put(1, "Alright, your VRChat name is: %%. Is this correct?");
        responses.put(2, "For your genres, I've got: %%. Is this correct?");
        responses.put(3, "For your socials, I've got: %%. Is this correct?");
        responses.put(5, "Okay, your demo link is: %%. Is this correct?");
    }

    public void start(){
        Conductor.getInstance().sendMessage("Hello, " + member.getAsMention() + "! \n\nThank you for your interest in " +
                "DJing at our club. Please answer the following questions I'll get your application off to our head DJ asap.\n\n-\n\n", channel, 1000);
        Conductor.getInstance().sendMessage("First, what is your DJ name?", channel, 1000);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getMember().getId().equals(member.getId()) && event.getMessage().getChannelId().equals(channel.getId())){
            switch (step){
                case 0:
                    this.name = event.getMessage().getContentRaw();
                    sendResponse(responses.get(step), this.name);
                    break;
                case 1:
                    this.vrcName = event.getMessage().getContentRaw();
                    sendResponse(responses.get(step), this.vrcName);
                    break;
                case 2:
                    genres.add(event.getMessage().getContentRaw());
                    break;
                case 3:
                    socials.add(event.getMessage().getContentRaw());
                    break;
                case 4:
                    if (event.getMessage().getAttachments().size() == 0) {
                        this.logo = "";
                        sendResponse("You do not have a logo, correct?", this.logo);
                    }else {
                        this.logo = event.getMessage().getAttachments().get(0).getUrl();
                        sendResponse("Is the image you provided correct?", this.logo);
                    }
                    break;
                case 5:
                    demos.add(event.getMessage().getContentRaw());
                    sendResponse(responses.get(step), demos);
                    break;
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        if (event.getMember().getId().equals(member.getId())){
            MessageCreateBuilder builder = new MessageCreateBuilder();

            if (event.getButton().getId().equals("yes" + this.ticketID)){
                switch (step){
                    case 0:
                        step++;
                        Conductor.getInstance().sendMessage("Wonderful! " + questions.get(step), channel, 1000);
                        break;
                    case 1:
                        step++;
                        sendResponseDoneButton("Great! " + questions.get(step));
                        break;
                    case 2:
                        step++;
                        sendResponseDoneButton("Awesome! " + questions.get(step));
                        break;
                    case 3:
                        step++;
                        builder.addContent("Fantastic! " + questions.get(step));
                        Conductor.getInstance().sendMessage(builder.build(), channel, 1000);
                        break;
                    case 4:
                        step++;
                        builder.addContent("Excellent! " + questions.get(step));
                        Conductor.getInstance().sendMessage(builder.build(), channel, 1000);
                        break;
                    case 5:
                        step++;

                        Conductor.getInstance().sendMessage("Perfect!", channel, 1000);

                        Button good = Button.of(ButtonStyle.SUCCESS, "good" + this.ticketID, "Looks good, send it!");
                        Button bad = Button.of(ButtonStyle.DANGER, "bad" + this.ticketID, "That's not right, let me start over");
                        Button cancel = Button.of(ButtonStyle.PRIMARY, "cancel" + this.ticketID, "Nevermind, cancel my application");
                        profile = Profile.buildFromApplication(member.getId(), vrcName, name, logo, genres, socials, demos);
                        builder.addContent(questions.get(step));
                        builder.addActionRow(good, bad, cancel);
                        builder.addEmbeds(profile.getProfileEmbed());
                        Conductor.getInstance().sendMessage(builder.build(), channel, 1000);
                        break;
                }
            }else if (event.getButton().getId().equals("no" + this.ticketID)){
                switch (step) {
                    case 2:
                        genres.clear();
                        sendResponseDoneButton("Okay, let's try that again. " + questions.get(step));
                        break;
                    case 3:
                        socials.clear();
                        sendResponseDoneButton("Okay, let's try that again. " + questions.get(step));
                        break;
                    default:
                        Conductor.getInstance().sendMessage("Okay, let's try that again. " + questions.get(step), channel, 1000);
                }
            }else if (event.getButton().getId().equals("done" + this.ticketID)){
                switch (step){
                    case 2:
                        sendResponse(responses.get(step), genres);
                        break;
                    case 3:
                        sendResponse(responses.get(step), socials);
                        break;
                }
            }else if (event.getButton().getId().equals("good" + this.ticketID)){
                Conductor.ApplicationManager.addApplication(profile, member.getId());
                Conductor.ApplicationManager.sendApplicationMessage(member.getUser(), profile);
                Conductor.getInstance().sendMessage("Okay! I've sent your application to our staff for review. I'll let you know the results asap!", channel, 1000);
                Conductor.getInstance().getJDA().removeEventListener(this);
            }else if (event.getButton().getId().equals("bad" + this.ticketID)){
                step = 0;
                genres.clear();
                socials.clear();
                demos.clear();
                Conductor.getInstance().sendMessage("My apologies, let's start over. " + questions.get(step), channel, 1000);
            }else if (event.getButton().getId().equals("cancel" + this.ticketID)){
                Conductor.getInstance().sendMessage("I'm sorry things didn't quite work out. If you'd like to apply in the future, please open a new application. " +
                        "Thank you, and have a great day", channel, 1000);
                Conductor.getInstance().getJDA().removeEventListener(this);
            }
            event.deferEdit().queue();
        }
    }

    private String listToString(List<String> lst){
        StringBuilder builder = new StringBuilder();
        for (String s : lst){
            builder.append(s).append(", ");
        }

        return builder.substring(0, builder.toString().length()-2);
    }

    private void sendResponse(String message, String data){
        Button yes = Button.of(ButtonStyle.SUCCESS, "yes" + this.ticketID, "Yes");
        Button no = Button.of(ButtonStyle.DANGER, "no" + this.ticketID, "No");

        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(message.replace("%%", data));
        builder.addActionRow(yes, no);
        Conductor.getInstance().sendMessage(builder.build(), channel, 1000);
    }

    private void sendResponse(String message, List<String> data){
        Button yes = Button.of(ButtonStyle.SUCCESS, "yes" + this.ticketID, "Yes");
        Button no = Button.of(ButtonStyle.DANGER, "no" + this.ticketID, "No");

        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(message.replace("%%", listToString(data)));
        builder.addActionRow(yes, no);
        Conductor.getInstance().sendMessage(builder.build(), channel, 1000);
    }

    private void sendResponseDoneButton(String message){
        Button done = Button.of(ButtonStyle.PRIMARY, "done" + this.ticketID, "Done");

        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(message);
        builder.addActionRow(done);
        Conductor.getInstance().sendMessage(builder.build(), channel, 1000);
    }
}
