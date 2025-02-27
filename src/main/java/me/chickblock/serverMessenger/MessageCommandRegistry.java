package me.chickblock.serverMessenger;

import me.chickblock.serverMessenger.MessageCommands.MessageCommand;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class MessageCommandRegistry {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MessageCommandRegistry.class);
    private static final List<MessageCommand> commandRegistry = new ArrayList<MessageCommand>();
    private static int idCount = -1;
    private static final List<Integer> idRegistry = new ArrayList<Integer>();
    private static boolean active = false;

    static void init(){
        if(active){
            return;
        }
        MessageCommandRegistry.active = true;
    }


    public static boolean registerCommand(@Nullable MessageCommand command){
        if(!active){
            log.warn("A plugin is attempting to register a Message Command before the registry has been activated. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return false;
        }

        if(command == null){
            return false;
        }
        for(MessageCommand reg: commandRegistry){
            if(reg.equals(command)){
                log.warn("A message command has already been registered with the following parameters: " + command.toString() + " unable to complete registry.");
                return false;
            }
        }
        log.info("Registering new Server Messenger command from plugin: " + command.getRegisteredPlugin().getDescription().getId());
        idCount++;
        command.setRegistryID(idCount);
        idRegistry.add(idCount);
        commandRegistry.add(command);
        return true;
    }

    public static int getIdOfCommand(MessageCommand command){
        if(!active){
            log.warn("A plugin is attempting to register a Message Command before the registry has been activated. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return -1;
        }

        int index = commandRegistry.indexOf(command);
        if(index >= 0){
            return idRegistry.get(index);
        }else{
            return -1;
        }
    }

    public static @Nullable MessageCommand getCommandFromId(int i){
        if(!active){
            log.warn("A plugin is attempting to register a Message Command before the registry has been activated. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return null;
        }

        if(idRegistry.contains(i)){
            return commandRegistry.get(idRegistry.indexOf(i));
        }else{
            return null;
        }
    }


    public static boolean commandIsInRegistry(MessageCommand command){
        if(!active){
            log.warn("A plugin is attempting to register a Message Command before the registry has been activated. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return false;
        }
        return commandRegistry.contains(command);
    }

    public static boolean commandIDIsValid(int i){
        if(!active){
            log.warn("A plugin is attempting to register a Message Command before the registry has been active. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return false;
        }
        return (i >= 0 && i <= idCount);
    }

    public static boolean isActive(){
        return isActive();
    }
}
