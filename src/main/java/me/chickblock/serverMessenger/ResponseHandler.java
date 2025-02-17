package me.chickblock.serverMessenger;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;

import java.util.logging.Logger;

public class ResponseHandler {
    private static ChannelIdentifier IDENTIFIER;
    private static Logger logger;

    @Subscribe
    public static void onPluginMessageFromBackend(PluginMessageEvent event){
        if(!IDENTIFIER.equals(event.getIdentifier())){
            return;
        }else{
            event.setResult(PluginMessageEvent.ForwardResult.handled());
        }

        if(event.getSource() instanceof Player player){
            logger.warning("Received plugin message originating from a player. This may indicate a player is attempting to spoof messages to the proxy.\nPlayer name: " + player.getUsername() + "(" + player.getUniqueId() + ")");
        }else if(event.getSource() instanceof ServerConnection){
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String subchannel = in.readUTF();
            if(subchannel.equals())

        }


    }


}
