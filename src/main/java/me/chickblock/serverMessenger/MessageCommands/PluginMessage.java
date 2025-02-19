package me.chickblock.serverMessenger.MessageCommands;


public record PluginMessage(byte[] keyWord, boolean requiresResponse, boolean voidReply, String messageContents, String pluginID) {
}
