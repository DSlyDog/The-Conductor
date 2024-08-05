package net.whispwriting.the_conductor.discord.util;

import net.whispwriting.the_conductor.Main;
import org.json.JSONException;

import java.io.IOException;

public class Strings {

    public static String ADMIN_ROLE = "";
    public static String DJ_ROLE = "";
    public static String DJ_APPLICATION_ROLE = "";
    public static String DJ_APPLICATION_CATEGORY = "";
    public static String DJ_SUBMISSION_CHANNEL = "";
    public static String DJ_APP_ARCHIVE_CATEGORY = "";

    public static void loadData(){
        JsonFile file = new JsonFile("config", "./");

        if (!JsonFile.exists("config", "./")) {
            file.set("admin_role", ADMIN_ROLE);
            file.set("dj_role", DJ_ROLE);
            file.set("dj_application_role", DJ_APPLICATION_ROLE);
            file.set("dj_application_category", DJ_APPLICATION_CATEGORY);
            file.set("dj_submission_channel", DJ_SUBMISSION_CHANNEL);
            file.set("dj_app_archive_category", DJ_APPLICATION_CATEGORY);

            try {
                file.save();
            } catch (IOException e) {
                System.err.println("There was an error building the config.yml");
                e.printStackTrace();
            }
        }else {
            ADMIN_ROLE = file.getString("admin_role");
            DJ_ROLE = file.getString("dj_role");
            DJ_APPLICATION_ROLE = file.getString("dj_application_role");
            DJ_APPLICATION_CATEGORY = file.getString("dj_application_category");
            DJ_SUBMISSION_CHANNEL = file.getString("dj_submission_channel");
            DJ_APP_ARCHIVE_CATEGORY = file.getString("dj_app_archive_category");
        }

        Main.getLogger().info("config path: " + file.getFilePath());
    }
}
