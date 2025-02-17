package me.chickblock.serverMessenger;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;




@Plugin(
        id = "servermessenger",
        name = "ServerMessenger",
        version = BuildConstants.VERSION
)
public class ServerMessenger {

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


    public byte[] composeServerCommand(MessageCommandType commandType) throws Exception {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(server.toString());
        out.writeUTF(IDENTIFIER.toString());

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);
        switch(commandType){
            case RESTARTSBACKEND:
                msgOut.writeUTF("RESTARTBACKEND");
                break;
            default:
                logger.error("This shit ain't done cooking yet, call back later");
                return null;
        }
        byte[] byteArr = msgBytes.toByteArray();
        out.writeShort(byteArr.length);
        out.write(byteArr);

        return out.toByteArray();
    }

    private boolean sendToBackend(RegisteredServer server, byte[] data){
        return sendToBackend(server, IDENTIFIER, data);
    }

    private boolean sendToBackend(RegisteredServer server, ChannelIdentifier identifier, byte[] data){
        return server.sendPluginMessage(identifier, data);
    }


}
