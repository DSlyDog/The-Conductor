package net.whispwriting.the_conductor;

import net.whispwriting.the_conductor.discord.Conductor;
import net.whispwriting.the_conductor.discord.commands.CommandDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {


    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static boolean listen = true;

    public static void main(String[] args){

        if (args.length < 1){
            LOGGER.error("You must provide the bot token");
            return;
        }

        Conductor bot = Conductor.getInstance();
        try{
            bot.init(args[0]);
        }catch (LoginException e){
            LOGGER.error("Failed to connect to the Discord bot");
            e.printStackTrace();
            return;
        }

        CommandDelegate.registerCommands(bot);

        listen(bot);
    }

    public static void listen(Conductor bot){
        Scanner sc = new Scanner(System.in);
        while (listen){
            String line = sc.nextLine();
            onCommandLineCmd(line, bot);
        }
    }

    public static void onCommandLineCmd(String cmd, Conductor bot){
        switch (cmd){
            case "stop":
                LOGGER.info("Shutting down bot...");
                listen = false;
                bot.stop();
                break;
            case "time":
                LocalDateTime time = LocalDateTime.now();
                System.out.println(time.getHour());
                break;
            default:
                LOGGER.info("Command not found");
                break;
        }
    }

    public static Logger getLogger(){
        return LOGGER;
    }
}
