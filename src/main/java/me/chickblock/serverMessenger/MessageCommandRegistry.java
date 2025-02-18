package me.chickblock.serverMessenger;

import me.chickblock.serverMessenger.MessageCommands.MessageCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MessageCommandRegistry {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MessageCommandRegistry.class);
    private static final List<MessageCommand> commandRegistry = new ArrayList<MessageCommand>();
    private static Logger logger;
    private static int idCount = -1;
    private static final List<Integer> idRegistry = new ArrayList<Integer>();

    public static boolean registerCommand(@NotNull MessageCommand command){
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

    protected static List<MessageCommand> getMessageCommandRegistry(){
        return commandRegistry;
    }

    protected static List<Integer> getIdRegistry(){
        return idRegistry;
    }

    protected static boolean removeMessageCommand(@NotNull MessageCommand command){
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

    public static int getMessageCommandIndex(MessageCommand command){
        return commandRegistry.indexOf(command);
    }

    public static int getIdOfCommand(MessageCommand command){
        int index = getMessageCommandIndex(command);
        if(index >= 0){
            return idRegistry.get(index);
        }else{
            return -1;
        }
    }

    public static @Nullable MessageCommand getCommandFromIndex(int i){
        try{
            return commandRegistry.get(i);
        }catch(IndexOutOfBoundsException e){
            log.warn("Unable to find a registered message command at index of: '" + i +"'");
            return null;
        }
    }

    public static @Nullable MessageCommand getCommandFromId(int i){
        if(idRegistry.contains(i)){
            return commandRegistry.get(getIndexOfId(i));
        }else{
            return null;
        }
    }

    public static boolean commandIsInRegistry(MessageCommand command){
        return commandRegistry.contains(command);
    }

    public static boolean commandIDIsValid(int i){
        return i >= 0 && i <= idCount;
    }

    protected static int getIndexOfId(int i){
        return idRegistry.indexOf(i);
    }

}
