package me.chickblock.serverMessenger.MessageCommands;


public record PluginMessage(String keyWord, boolean requiresResponse, String messageContents, String pluginID) {
}
