package me.chickblock.serverMessenger;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.ResultedEvent.GenericResult;


public class ServerMessengerInitialiseEvent {
    private final ServerMessenger serverMessenger;

    public ServerMessengerInitialiseEvent(ServerMessenger serverMessenger){
        this.serverMessenger = serverMessenger;
    }

    public ServerMessenger getServerMessenger() {
        return serverMessenger;
    }



}
