package me.chickblock.serverMessenger.MessageEvents;

import me.chickblock.serverMessenger.ServerMessenger;


public class ServerMessengerInitialiseEvent {
    private final ServerMessenger serverMessenger;

    public ServerMessengerInitialiseEvent(ServerMessenger serverMessenger){
        this.serverMessenger = serverMessenger;
    }

    public ServerMessenger getServerMessenger() {
        return serverMessenger;
    }



}
