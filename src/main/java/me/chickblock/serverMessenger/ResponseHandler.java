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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class ResponseHandler {
    private static Logger logger;
    private static boolean initialise = false;
    private static EventManager eventManager;

    protected static void init(Logger logger, EventManager eventManager){
        if(initialise){
            return;
        }
        ResponseHandler.logger = logger;
        ResponseHandler.eventManager = eventManager;
        initialise = true;
    }

    @Subscribe
    public static void onPluginMessageFromBackend(@NotNull PluginMessageEvent event){
        if(!initialise){
            return; // Ignore packets sent during initialisation
        }
        if(!ServerMessenger.IDENTIFIER.equals(event.getIdentifier())){
            return;
        }else{
            event.setResult(PluginMessageEvent.ForwardResult.handled());
        }

        if(event.getSource() instanceof Player player){
            logger.warn("Received plugin message originating from a player. This may indicate a player is attempting to spoof messages to the proxy.\nPlayer name: " + player.getUsername() + "(" + player.getUniqueId() + ")");
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
                            logger.warn("A received packet required a response but no response was sent, this may cause problems on the backend server.");
                        }

                    });
                } catch (IOException e) {
                    logger.warn("Received malformed packet data from server: " + ((ServerConnection) event.getSource()).getServer().toString() + " This could be the result of a buggy plugin on that server.");
                }
            }
        }
    }

    public static boolean isInitialise() {
        return initialise;
    }
}
