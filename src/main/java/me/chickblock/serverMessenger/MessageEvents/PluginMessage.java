package me.chickblock.serverMessenger.MessageEvents;


public record PluginMessage(String keyWord, boolean requiresResponse, boolean voidReply, String messageContents, String pluginID) {
}
