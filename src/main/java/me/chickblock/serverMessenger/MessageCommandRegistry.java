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
import org.slf4j.Logger;

public class MessageCommandRegistry {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MessageCommandRegistry.class);
    private static final List<MessageCommand> commandRegistry = new ArrayList<MessageCommand>();
    private static Logger logger;
    private static int idCount = -1;
    private static final List<Integer> idRegistry = new ArrayList<Integer>();
    private static boolean initialise = false;
    private static boolean active = false;

    // Initialisation/Activation of Registry
    protected static void init(org.slf4j.Logger logger){
        if(initialise){
            return;
        }
        MessageCommandRegistry.logger = logger;
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

    // Registry Access Commands - Public use
    public static int getMessageCommandIndex(MessageCommand command){
        if(!active){
            log.warn("A plugin is attempting to register a Message Command before the registry has been activated. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return -1;
        }
        return commandRegistry.indexOf(command);
    }

    public static int getIdOfCommand(MessageCommand command){
        if(!active){
            log.warn("A plugin is attempting to register a Message Command before the registry has been activated. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return -1;
        }

        int index = getMessageCommandIndex(command);
        if(index >= 0){
            return idRegistry.get(index);
        }else{
            return -1;
        }
    }

    public static @Nullable MessageCommand getCommandFromIndex(int i){
        if(!active){
            log.warn("A plugin is attempting to register a Message Command before the registry has been activated. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return null;
        }

        try{
            return commandRegistry.get(i);
        }catch(IndexOutOfBoundsException e){
            log.warn("Unable to find a registered message command at index of: '" + i +"'");
            return null;
        }
    }

    public static @Nullable MessageCommand getCommandFromId(int i){
        if(!active){
            log.warn("A plugin is attempting to register a Message Command before the registry has been activated. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return null;
        }

        if(idRegistry.contains(i)){
            return commandRegistry.get(getIndexOfId(i));
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
        return i >= 0 && i <= idCount;
    }

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
        log.info("Registering new Server Messenger command from plugin: " + command.getRegisteredPlugin().name());
        idCount++;
        command.setRegistryID(idCount);
        idRegistry.add(idCount);
        commandRegistry.add(command);
        return true;
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

    protected static boolean removeMessageCommand(@NotNull MessageCommand command){
        if(!initialise){
            throw new InternalError("SERVER MESSENGER INTERNAL ERROR: Attempted to access message command registry before registry has been initialised. This should not happen.");
        }
        log.warn("Attempting to deregister message command: " + command.getName() + ". This is NOT supported behavior and may cause problems if a message is in transit.");
        int index = commandRegistry.indexOf(command);
        if(index >= 0){
            idRegistry.remove(index);
            return true;
        }else{
            return false;
        }
    }

    protected static boolean removeMessageCommand(int registryIndex){
        if(!initialise){
            throw new InternalError("SERVER MESSENGER INTERNAL ERROR: Attempted to access message command registry before registry has been initialised. This should not happen.");
        }

        log.warn("Attempting to deregister message command at index of: '" + registryIndex + "' in the registry. This is NOT supported behavior and may cause problems if a message is in transit.");
        try{
            commandRegistry.remove(registryIndex);
            idRegistry.remove(registryIndex);
            return true;
        }catch(IndexOutOfBoundsException e){
            log.warn("Unable to degister message command at index of: '" + registryIndex + "' in the registry. This means that no message command has been registered at this position.");
            return false;
        }
    }

    protected static int getIndexOfId(int i){
        if(!initialise){
            throw new InternalError("SERVER MESSENGER INTERNAL ERROR: Attempted to access message command registry before registry has been initialised. This should not happen.");
        }
        return idRegistry.indexOf(i);
    }

    protected static boolean isInitialise() {
        return initialise;
    }



}
