package me.chickblock.serverMessenger;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.chickblock.serverMessenger.MessageCommands.MessageCommand;
import me.chickblock.serverMessenger.MessageEvents.PluginMessage;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerInitialiseEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Plugin(
        id = "servermessenger",
        name = "ServerMessenger",
        version = BuildConstants.VERSION
)
public class ServerMessenger {
    public static final String PLUGIN_MESSAGING_CHANNEL = "servermessenger";
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("servermessenger:main");
    private final ProxyServer SERVER;
    private static Logger logger;


    @Inject
    private ServerMessenger(ProxyServer server, Logger logger){
        this.SERVER = server;
        ServerMessenger.logger = logger;
    }


    @Subscribe
    private void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Starting server messenger...");
        EventManager eventManager = SERVER.getEventManager();
        logger.info("Registering listeners...");
        SERVER.getChannelRegistrar().register(IDENTIFIER);
        eventManager.register(this, new MessageCommandRegistry());
        eventManager.register(this, new EventClassRegistry());
        logger.info("Initialising Event Class Registry...");
        EventClassRegistry.init(logger);
        logger.info("Initialising Message Command Registry...");
        MessageCommandRegistry.init(logger);
        logger.info("Initialising Packet Listener...");
        ResponseHandler.init(eventManager, logger);
        logger.info("All modules initialised, activating registries and packet listener...");
        eventManager.fireAndForget(new ServerMessengerInitialiseEvent());
        logger.info("Server messenger has successfully started, registries are now operational.");
        logger.info("Now listening for packets sent on the " + PLUGIN_MESSAGING_CHANNEL + " channel.");
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
        return new PluginMessage(command.getKeyWord(), requiresResponse, noReply, messageContents, command.getRegisteredPlugin().getDescription().getId());
    }

    @Contract("_, _ -> new")
    public static @NotNull PluginMessage composeMessage(@NotNull MessageCommand command, @NotNull String messageContents){
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
        return new PluginMessage(command.getKeyWord(), requiresResponse, noReply, messageContents, command.getRegisteredPlugin().getDescription().getId());
    }

    public static boolean sendMessage(@NotNull RegisteredServer destinationServer, @NotNull PluginMessage message){
        byte[] data;
        ByteArrayDataOutput in = ByteStreams.newDataOutput();
        in.writeUTF(message.keyWord());
        in.writeBoolean(message.requiresResponse());
        in.writeBoolean(message.voidReply());
        in.writeUTF(message.pluginID());
        in.writeUTF(message.messageContents());
        data = in.toByteArray();
        return destinationServer.sendPluginMessage(IDENTIFIER, data);
    }
}

