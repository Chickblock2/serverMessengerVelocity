package me.chickblock.serverMessenger.MessageEvents;

import com.velocitypowered.api.plugin.PluginContainer;
import me.chickblock.serverMessenger.MessageCommandRegistry;
import me.chickblock.serverMessenger.MessageCommands.MessageCommand;

public class EventRegistryEntry{
    private int messageCommandRegistryID;
    private String keyword;
    private PluginContainer registeredPlugin;
    private Object objectToFire;

    public EventRegistryEntry(int messageCommandRegistryID, Object objectToFire){
        MessageCommand command = MessageCommandRegistry.getCommandFromId(messageCommandRegistryID);
        if(command != null){
            this.messageCommandRegistryID = messageCommandRegistryID;
            this.keyword = command.getKeyWord();
            this.registeredPlugin = command.getRegisteredPlugin();
            this.objectToFire = objectToFire;
        }
    }

    public int getMessageCommandRegistryID() {
        return messageCommandRegistryID;
    }

    public String getKeyword() {
        return keyword;
    }

    public PluginContainer getRegisteredPlugin() {
        return registeredPlugin;
    }

    public Object getObjectToFire() {
        return objectToFire;
    }
}
