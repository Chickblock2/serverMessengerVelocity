package me.chickblock.serverMessenger;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.chickblock.serverMessenger.MessageCommands.MessageCommand;
import me.chickblock.serverMessenger.MessageEvents.EventClassRegistry;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;


@Plugin(
        id = "servermessenger",
        name = "ServerMessenger",
        version = BuildConstants.VERSION
)
public class ServerMessenger {
    public final static String PLUGIN_MESSAGING_CHANNEL = "servermessenger";
    private final ProxyServer server;
    private final Logger logger;
    private static MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("servermessenger:main");

    @Inject
    public ServerMessenger(ProxyServer server, Logger logger){
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Starting server messenger...");
        server.getChannelRegistrar().register(IDENTIFIER);
        logger.info("Server messenger has successfully started.");
    }

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event){
        if(!IDENTIFIER.equals(event.getIdentifier())){
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
                String pluginID;
                String messageContents;
                ServerMessengerEvent SMEvent;
                Plugin plugin;
                Object eventToFire;
                try {
                    keyWord = msgIn.readUTF(); // Read the data in the same way you wrote it
                    requiresResponse = msgIn.readBoolean();
                    pluginID = msgIn.readUTF();
                    messageContents = msgIn.readUTF();
                    // Check for registered plugin to fire event for.
                    plugin = EventClassRegistry.findPluginFromID(pluginID);
                    if(plugin != null){
                        eventToFire = EventClassRegistry.getEventFromPlugin(plugin);
                    }else{
                        eventToFire = new ServerMessengerEvent(keyWord, pluginID, requiresResponse, messageContents);
                    }
                    server.getEventManager().fire(eventToFire).thenAccept((returnedEvent) -> {
                        // TODO: handle response
                    });
                } catch (IOException e) {
                    logger.warn("Received malformed packet data from server: " + ((ServerConnection) event.getSource()).getServer().toString() + " This could be the result of a buggy plugin on that server.");
                }
            }
        }
    }

    public boolean sendMessage(@NotNull RegisteredServer server, int messageCommandRegistryID, @NotNull String messageContents){
        return false;
    }

    public boolean sendMessage(@NotNull RegisteredServer server, MessageCommand messageCommand, @NotNull String messageContents){
        if(!MessageCommandRegistry.commandIDIsValid(messageCommand.getRegistryID())){
            logger.warn("A plugin is attempting to send an unregistered command, this is unsupported behavior and likely means the plugin owner is not properly using Server Messenger.");
        }
        return false;
    }


}
