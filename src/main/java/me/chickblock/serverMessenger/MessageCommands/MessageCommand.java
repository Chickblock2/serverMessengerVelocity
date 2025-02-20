package me.chickblock.serverMessenger.MessageCommands;

import com.velocitypowered.api.plugin.Plugin;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class MessageCommand {
    private final String name;
    private final byte[] commandKeyWord;
    private final SendType sendType;
    private final ResponseType responseType;
    private final Plugin registeredPlugin;
    private int registryID = -1;


    public MessageCommand(@Nullable String commandTypeName, @NotNull String commandKeyWord, @NotNull SendType sendType, @NotNull ResponseType responseType, @NotNull Plugin registeringPlugin){
        if(commandKeyWord.isBlank()){
            throw new IllegalArgumentException("Command word cannot be blank.");
        }
        this.commandKeyWord = commandKeyWord.getBytes();
        this.name = commandTypeName;
        this.sendType = sendType;
        this.responseType = responseType;
        this.registeredPlugin = registeringPlugin;
    }

    public String getName(){
        return name;
    }

    public byte[] getCommandKeyWord(){
        return commandKeyWord;
    }

    public SendType getSendType(){
        return sendType;
    }

    public ResponseType getResponseType(){
        return responseType;
    }


    public Plugin getRegisteredPlugin() {
        return registeredPlugin;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageCommand that = (MessageCommand) o;
        return Objects.deepEquals(commandKeyWord, that.commandKeyWord) && sendType == that.sendType && responseType == that.responseType && Objects.equals(registeredPlugin, that.registeredPlugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(commandKeyWord), sendType, responseType, registeredPlugin);
    }

    @Override
    public String toString() {
        return "MessageCommand{" +
                "name='" + name + '\'' +
                ", commandKeyWord=" + Arrays.toString(commandKeyWord) +
                ", sendType=" + sendType +
                ", responseType=" + responseType +
                ", registeredPlugin=" + registeredPlugin +
                '}';
    }

    public int getRegistryID() {
        return registryID;
    }

    public void setRegistryID(int i){
        this.registryID = i;
    }

    public ServerMessengerEvent generateListenerEvent(){
        boolean responseRequired;
        boolean voidReply = switch (responseType) {
            case REQUIRED -> {
                responseRequired = true;
                yield false;
            }
            case OPTIONAL -> {
                responseRequired = false;
                yield false;
            }
            case VOID -> {
                responseRequired = false;
                yield true;
            }
        };
        return new ServerMessengerEvent(Arrays.toString(commandKeyWord), registeredPlugin.id(), responseRequired, voidReply);
    }
}
