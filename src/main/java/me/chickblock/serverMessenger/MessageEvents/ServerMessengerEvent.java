package me.chickblock.serverMessenger.MessageEvents;

import org.jetbrains.annotations.NotNull;

public class ServerMessengerEvent{
    private final String keyWord;
    private final String pluginID;
    private final boolean requireResponse;
    private final boolean voidReply;
    private String messageContents;
    protected PluginMessage replyMessage = null;
    private int messageEventRegistryID = -1;

    public ServerMessengerEvent(@NotNull String keyWord, @NotNull String pluginID, boolean requireResponse, boolean voidReply){
        this.keyWord = keyWord;
        this.pluginID = pluginID;
        this.requireResponse = requireResponse;
        this.voidReply = voidReply;
    }

    public ServerMessengerEvent(@NotNull String keyWord, @NotNull String pluginID, boolean requireResponse, boolean voidReply, @NotNull String messageContents){
        this.keyWord = keyWord;
        this.pluginID = pluginID;
        this.requireResponse = requireResponse;
        this.voidReply = voidReply;
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

    public boolean isVoidReply() {
        return voidReply;
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

    public int getMessageEventRegistryID() {
        return messageEventRegistryID;
    }

    public void setMessageEventRegistryID(int messageEventRegistryID) {
        this.messageEventRegistryID = messageEventRegistryID;
    }

    // For overriding in extended (private) classes
    // False discontinues built-in callback execution (no reply message)
    // True continues built-in callback execution (reply message)
    public boolean callBackFunction(){
        return true;
    }


}

