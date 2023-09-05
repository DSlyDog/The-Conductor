package net.whispwriting.the_conductor.discord.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;
import java.util.List;

public class Profile {

    private String discordID;
    private String name;
    private String logo;
    private List<String> genres;
    private List<String> socials;
    private List<String> demoSets;

    private JsonFile file;


    public Profile(String discordID, String name, String logo, JsonFile file){
        this.discordID = discordID;
        this.name = name;
        this.logo = logo;
        this.file = file;
    }

    public Profile(String discordID, String name, String logo, List<String> genres, List<String> socials, List<String> demoSets){
        this.discordID = discordID;
        this.name = name;
        this.logo = logo;
        this.genres = genres;
        this.socials = socials;
        this.demoSets = demoSets;
    }

    public boolean updateName(String name){
        this.name = name;
        file.set("name", name);
        return saveFile();
    }

    public boolean updateLogo(String logo){
        this.logo = logo;
        file.set("logo", logo);
        return saveFile();
    }

    public boolean addGenre(String genre){
        genres.add(genre);
        file.set("genres", genres);
        return saveFile();
    }

    public boolean removeGenre(int index){
        genres.remove(index);
        file.set("genres", genres);
        return saveFile();
    }

    public boolean addSocial(String social){
        socials.add(social);
        file.set("socials", socials);
        return saveFile();
    }

    public boolean removeSocial(int index){
        socials.remove(index);
        file.set("socials", socials);
        return saveFile();
    }

    public boolean addDemoSet(String demoSet){
        demoSets.add(demoSet);
        file.set("demoSets", demoSets);
        return saveFile();
    }

    private boolean saveFile(){
        try {
            file.save();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save DJ Profile file");
            e.printStackTrace();
            return false;
        }
    }

    public MessageEmbed getProfileEmbed(){
        EmbedBuilder builder = new EmbedBuilder();

        builder.addField("DJ Name", name, true);

        String genreString = listFieldBuilder(genres);
        genreString = String.format(genreString, "genres");
        builder.addField("Genres", genreString, true);

        String socialsString = listFieldBuilder(socials);
        socialsString = String.format(socialsString, "socials");
        builder.addField("Socials", socialsString, true);

        String demoSetString = listFieldBuilder(demoSets);
        demoSetString = String.format(demoSetString, "demo sets");
        builder.addField("Demo Sets", demoSetString, true);

        builder.setThumbnail(logo);

        return builder.build();
    }

    private String listFieldBuilder(List<String> lst){
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : lst){
            stringBuilder.append(item).append(", ");
        }
        return stringBuilder.toString().length() > 0 ? stringBuilder.toString() : "There are no %s listed.";
    }

    public static Profile buildFromApplication(String discordID, String name, String logo){
        JsonFile file = new JsonFile(discordID, "dj_profiles");

        return new Profile(discordID, name, logo, file);
    }

    public static Profile loadFromFile(String discordID){
        JsonFile file = new JsonFile(discordID, "dj_profiles");
        String name = file.getString("name");
        String logo = file.getString("logo");
        List<String> genres = file.getStringList("genres");
        List<String> socials = file.getStringList("socials");
        List<String> demoSets = file.getStringList("demoSets");

        return new Profile(discordID, name, logo, genres, socials, demoSets);
    }
}
