package me.chickblock.serverMessenger;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.chickblock.serverMessenger.MessageCommands.MessageCommand;
import me.chickblock.serverMessenger.MessageEvents.PluginMessage;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerInitialiseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Arrays;


@Plugin(
        id = "servermessenger",
        name = "ServerMessenger",
        version = BuildConstants.VERSION
)
public class ServerMessenger {
    public final static String PLUGIN_MESSAGING_CHANNEL = "servermessenger";
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("servermessenger:main");
    private static Logger logger;
    private static PluginManager pluginManager;
    private final ProxyServer server;


    @Inject
    public ServerMessenger(ProxyServer server, Logger logger){
        this.server = server;
        ServerMessenger.logger = logger;
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Starting server messenger...");
        server.getChannelRegistrar().register(IDENTIFIER);
        logger.info("Initialising Event Class Registry...");
        EventClassRegistry.init(logger);
        pluginManager = server.getPluginManager();
        logger.info("Initialising Response Handler...");
        ResponseHandler.init(logger, server.getEventManager());

        server.getEventManager().fire(new ServerMessengerInitialiseEvent(this));
        logger.info("Server messenger has successfully started.");

    }

    protected static PluginManager getPluginManager() {
        return pluginManager;
    }

    protected static Logger getLogger() {
        return logger;
    }

    public static @Nullable PluginMessage composeMessage(int messageCommandRegistryID, @NotNull String messageContents){
        MessageCommand command = MessageCommandRegistry.getCommandFromId(messageCommandRegistryID);
        if(command == null){
            logger.warn("Received a request to compose a message for a command registry ID that does not exist.");
            return null;
        }
        boolean requiresResponse;
        boolean noReply;
        requiresResponse = switch (command.getResponseType()) {
            case VOID -> {
                noReply = true;
                yield false;
            }
            case OPTIONAL -> {
                noReply = false;
                yield false;
            }
            case REQUIRED -> {
                noReply = false;
                yield true;
            }
        };
        return new PluginMessage(command.getCommandKeyWord(), requiresResponse, noReply, messageContents, command.getRegisteredPlugin().id());
    }

    public static PluginMessage composeMessage(@NotNull MessageCommand command, @NotNull String messageContents){
        if(!MessageCommandRegistry.commandIDIsValid(command.getRegistryID())){
            logger.warn("A plugin is attempting to compose a message with an unregistered command, this is unsupported behavior and likely means the plugin owner is not properly using Server Messenger.");
        }
        boolean requiresResponse;
        boolean noReply;
        requiresResponse = switch (command.getResponseType()) {
            case VOID -> {
                noReply = true;
                yield false;
            }
            case OPTIONAL -> {
                noReply = false;
                yield false;
            }
            case REQUIRED -> {
                noReply = false;
                yield true;
            }
        };
        return new PluginMessage(command.getCommandKeyWord(), requiresResponse, noReply, messageContents, command.getRegisteredPlugin().id());

    }

    public static boolean sendMessage(@NotNull RegisteredServer destinationServer, @NotNull PluginMessage message){
        byte[] data;
        ByteArrayDataOutput in = ByteStreams.newDataOutput();
        in.write(message.keyWord());
        in.writeBoolean(message.requiresResponse());
        in.writeBoolean(message.voidReply());
        in.writeUTF(message.pluginID());
        in.writeUTF(message.messageContents());
        data = in.toByteArray();
        return destinationServer.sendPluginMessage(IDENTIFIER, data);
    }


}
