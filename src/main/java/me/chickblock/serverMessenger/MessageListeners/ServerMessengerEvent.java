package me.chickblock.serverMessenger.MessageListeners;

import me.chickblock.serverMessenger.MessageCommands.PluginMessage;
import org.jetbrains.annotations.NotNull;

public class ServerMessengerEvent{
    private String keyWord;
    private String pluginID;
    private boolean requireResponse;
    private String messageContents;
    private PluginMessage replyMessage = null;
    private int messageCommandRegistryID = -1;

    public ServerMessengerEvent(@NotNull String keyWord, @NotNull String pluginID, boolean requireResponse, @NotNull String messageContents){
        this.keyWord = keyWord;
        this.pluginID = pluginID;
        this.requireResponse = requireResponse;
        this.messageContents = messageContents;
    }


    public String getKeyWord() {
        return keyWord;
    }

    public String getPluginID() {
        return pluginID;
    }

    public boolean isRequireResponse() {
        return requireResponse;
    }

    public String getMessageContents() {
        return messageContents;
    }

    public void setReplyMessage(PluginMessage newMessage){
        this.replyMessage = newMessage;
    }

    public PluginMessage getReplyMessage(){
        return replyMessage;
    }

    public int getMessageCommandRegistryID() {
        return messageCommandRegistryID;
    }

    public void setMessageCommandRegistryID(int messageCommandRegistryID) {
        this.messageCommandRegistryID = messageCommandRegistryID;
    }
}

