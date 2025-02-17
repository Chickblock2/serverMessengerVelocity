package me.chickblock.serverMessenger.MessageCommands;


public class ShutdownCommand extends  MessageCommand{

    public ShutdownCommand() {
        super("Shutdown", "SHUTDOWN", SendType.BOTH, ResponseType.REQUIRED);
    }
}
