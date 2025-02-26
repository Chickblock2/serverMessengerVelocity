package me.chickblock.serverMessenger.MessageCommands;

import com.velocitypowered.api.plugin.PluginContainer;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

public class MessageCommand {
    private final String name;
    private final String keyWord;
    private final SendType sendType;
    private final ResponseType responseType;
    private final PluginContainer registeredPlugin;
    private int registryID = -1;


    public MessageCommand(@Nullable String commandTypeName, @NotNull String keyWord, @NotNull SendType sendType, @NotNull ResponseType responseType, @NotNull PluginContainer registeringPlugin){
        if(keyWord.isBlank()){
            throw new IllegalArgumentException("Command word cannot be blank.");
        }
        this.keyWord = keyWord;
        this.name = commandTypeName;
        this.sendType = sendType;
        this.responseType = responseType;
        this.registeredPlugin = registeringPlugin;
    }

    public String getName(){
        return name;
    }

    public String getKeyWord(){
        return keyWord;
    }

    public SendType getSendType(){
        return sendType;
    }

    public ResponseType getResponseType(){
        return responseType;
    }


    public PluginContainer getRegisteredPlugin() {
        return registeredPlugin;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageCommand that = (MessageCommand) o;
        return Objects.deepEquals(keyWord, that.keyWord) && sendType == that.sendType && responseType == that.responseType && Objects.equals(registeredPlugin, that.registeredPlugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyWord, sendType, responseType, registeredPlugin);
    }

    @Override
    public String toString() {
        return "MessageCommand{" +
                "name='" + name + '\'' +
                ", commandKeyWord=" + keyWord +
                ", sendType=" + sendType +
                ", responseType=" + responseType +
                ", registeredPlugin=" + registeredPlugin.getDescription().getName() +
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
        return new ServerMessengerEvent(keyWord, registeredPlugin.getDescription().getId(), responseRequired, voidReply);
    }
}
