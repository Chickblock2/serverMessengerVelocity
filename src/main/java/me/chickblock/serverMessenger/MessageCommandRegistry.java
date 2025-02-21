package me.chickblock.serverMessenger;

import com.velocitypowered.api.event.Subscribe;
import me.chickblock.serverMessenger.MessageCommands.MessageCommand;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerInitialiseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

public class MessageCommandRegistry {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MessageCommandRegistry.class);
    private static final List<MessageCommand> commandRegistry = new ArrayList<MessageCommand>();
    private static int idCount = -1;
    private static final List<Integer> idRegistry = new ArrayList<Integer>();
    private static boolean initialise = false;
    private static boolean active = false;

    // Initialisation/Activation of Registry
    protected static void init(){
        if(initialise){
            return;
        }
        MessageCommandRegistry.initialise = true;
    }

    @Subscribe
    private void onServerMessengerInitialised(ServerMessengerInitialiseEvent event) throws UnexpectedException {
        if(initialise){
            active = true;
        }else{
            throw new UnexpectedException("FATAL ERROR: Somehow the ServerMessengerInitialiseEvent was thrown before the Event Class Registry was fully initialised. This should NEVER happen.");
        }

    }

    // Registry Access Methods - Public use
    public static boolean registerCommand(@NotNull MessageCommand command){
        if(!active){
            log.warn("A plugin is attempting to register a Message Command before the registry has been activated. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
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

    // Registry management methods - INTERNAL USE ONLY
    protected static List<MessageCommand> getMessageCommandRegistry(){
        if(!initialise){
            throw new InternalError("SERVER MESSENGER INTERNAL ERROR: Attempted to access message command registry before registry has been initialised. This should not happen.");
        }
        return commandRegistry;
    }

    protected static List<Integer> getIdRegistry(){
        if(!initialise){
            throw new InternalError("SERVER MESSENGER INTERNAL ERROR: Attempted to access message command registry before registry has been initialised. This should not happen.");
        }
        return idRegistry;
    }

    protected static boolean isInitialise() {
        return initialise;
    }



}
