package me.chickblock.serverMessenger.MessageEvents;


public record PluginMessage(byte[] keyWord, boolean requiresResponse, boolean voidReply, String messageContents, String pluginID) {
}
