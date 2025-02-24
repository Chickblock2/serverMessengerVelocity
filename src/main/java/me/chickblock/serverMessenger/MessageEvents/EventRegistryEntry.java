package me.chickblock.serverMessenger.MessageEvents;

import com.velocitypowered.api.plugin.PluginContainer;

public record EventRegistryEntry(int messageCommandRegistryId, PluginContainer registeringPlugin, String keyWord, Object eventToFire) {
}
