package me.chickblock.serverMessenger.MessageEvents;

import me.chickblock.serverMessenger.EventClassRegistry;
import me.chickblock.serverMessenger.MessageCommandRegistry;
import me.chickblock.serverMessenger.ServerMessenger;


public class ServerMessengerInitialiseEvent {
    private final ServerMessenger serverMessenger;
    private final MessageCommandRegistry messageCommandRegistry;
    private final EventClassRegistry eventClassRegistry;

    public ServerMessengerInitialiseEvent(ServerMessenger serverMessenger, MessageCommandRegistry messageCommandRegistry, EventClassRegistry eventClassRegistry){
        this.serverMessenger = serverMessenger;
        this.messageCommandRegistry = messageCommandRegistry;
        this.eventClassRegistry = eventClassRegistry;
    }

    public ServerMessenger getServerMessenger() {
        return serverMessenger;
    }


    public MessageCommandRegistry getMessageCommandRegistry() {
        return messageCommandRegistry;
    }

    public EventClassRegistry getEventClassRegistry() {
        return eventClassRegistry;
    }
}
