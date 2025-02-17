package me.chickblock.serverMessenger.MessageCommands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageCommand {
    private final String name;
    private final byte[] commandKeyWord;
    private final SendType sendType;
    private final ResponseType responseType;
    private final byte[] messageContents;


    public MessageCommand(@Nullable String commandTypeName, @NotNull String commandKeyWord, @NotNull SendType sendType, @NotNull ResponseType responseType){
        this(commandTypeName, commandKeyWord, null, sendType, responseType);
    }

    public MessageCommand(@Nullable String commandTypeName, @NotNull String commandKeyWord, byte @Nullable [] messageContents, @NotNull SendType sendType, @NotNull ResponseType responseType){
        if(commandKeyWord.isBlank()){
            throw new IllegalArgumentException("Command word cannot be blank.");
        }
        this.commandKeyWord = commandKeyWord.getBytes();
        this.name = commandTypeName;
        this.sendType = sendType;
        this.responseType = responseType;
        this.messageContents = messageContents;
    }

    protected String getName(){
        return name;
    }

    protected byte[] getCommandKeyWord(){
        return commandKeyWord;
    }

    protected SendType getSendType(){
        return sendType;
    }

    protected ResponseType getResponseType(){
        return responseType;
    }

    protected byte[] getMessageContents(){
        return messageContents;
    }



}
