package me.chickblock.serverMessenger;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.rmi.UnexpectedException;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerInitialiseEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

public class ResponseHandler {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ResponseHandler.class);
    private static EventManager eventManager;
    private static boolean initialise = false;
    private static boolean active = false;


    protected static void init(EventManager eventManager){
        if(initialise){
            return;
        }
        ResponseHandler.eventManager = eventManager;
        initialise = true;
    }

    @Subscribe
    private void onServerMessengerInitialised(ServerMessengerInitialiseEvent event) throws UnexpectedException {
        if(initialise){
            active = true;
        }else{
            throw new UnexpectedException("FATAL ERROR: Somehow the ServerMessengerInitialiseEvent was thrown before the Event Class Registry was fully initialised. This should NEVER happen.");
        }

    }

    @Subscribe
    public static void onPluginMessageFromBackend(@NotNull PluginMessageEvent event){
        if(!active){
            return;
        }
        if(!ServerMessenger.IDENTIFIER.equals(event.getIdentifier())){
            return;
        }else{
            event.setResult(PluginMessageEvent.ForwardResult.handled());
        }

        if(event.getSource() instanceof Player player){
            log.warn("Received plugin message originating from a player. This may indicate a player is attempting to spoof messages to the proxy.\nPlayer name: " + player.getUsername() + "(" + player.getUniqueId() + ")");
        }else if(event.getSource() instanceof ServerConnection){
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String subchannel = in.readUTF();
            if (subchannel.equals(ServerMessenger.PLUGIN_MESSAGING_CHANNEL)) {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);

                DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgbytes));
                String keyWord;
                boolean requiresResponse;
                boolean noReply;
                String pluginID;
                String messageContents;
                Plugin plugin;
                Object eventToFire;
                try {
                    keyWord = msgIn.readUTF(); // Read the data in the same way you wrote it
                    requiresResponse = msgIn.readBoolean();
                    noReply = msgIn.readBoolean();
                    pluginID = msgIn.readUTF();
                    messageContents = msgIn.readUTF();
                    // Check for registered plugin to fire event for.
                    plugin = EventClassRegistry.findPluginFromID(pluginID);
                    if(plugin != null){
                        eventToFire = EventClassRegistry.getEventFromPlugin(plugin);
                    }else{
                        eventToFire = new ServerMessengerEvent(keyWord, pluginID, requiresResponse, noReply, messageContents);
                    }
                    eventManager.fire(eventToFire).thenAccept((returnedEvent) -> {
                        boolean messageHasBeenRepliedTo = false;
                        ServerMessengerEvent SMReturnedEvent = (ServerMessengerEvent) returnedEvent;
                        if(SMReturnedEvent.callBackFunction()){
                            if(!noReply && SMReturnedEvent.getReplyMessage() != null){
                                ServerMessenger.sendMessage(((ServerConnection) event.getSource()).getServer(), SMReturnedEvent.getReplyMessage());
                                messageHasBeenRepliedTo = true;
                            }
                        }else{
                            return;
                        }
                        if(requiresResponse && !messageHasBeenRepliedTo){
                            log.warn("A received packet required a response but no response was sent, this may cause problems on the backend server.");
                        }

                    });
                } catch (IOException e) {
                    log.warn("Received malformed packet data from server: " + ((ServerConnection) event.getSource()).getServer().toString() + " This could be the result of a buggy plugin on that server.");
                }
            }
        }
    }

    public static boolean isActive() {
        return active;
    }

    protected static  boolean isInitialise(){
        return initialise;
    }
}
