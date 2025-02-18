package me.chickblock.serverMessenger.MessageEvents;

import com.velocitypowered.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class EventClassRegistry {
    private static ArrayList<Object> eventClassRegistry = new ArrayList<Object>();
    private static ArrayList<Integer> idList = new ArrayList<Integer>();
    private static ArrayList<Plugin> registeredPlugin = new ArrayList<Plugin>();
    private static int idCount = -1;

    public boolean registerEvent(Object eventToRegister, Plugin plugin){
        if(eventClassRegistry.contains(eventToRegister) || eventToRegister.getClass().isAssignableFrom(ServerMessengerEvent.class)){
            return false;
        }else{
            idCount++;
            idList.add(idCount);
            eventClassRegistry.add(eventToRegister);
            registeredPlugin.add(plugin);
            return true;
        }
    }

    protected static ArrayList<Object> getEventClassRegistry() {
        return eventClassRegistry;
    }


    protected static ArrayList<Integer> getIdList() {
        return idList;
    }

    public static int getIndexOfPlugin(Plugin plugin){
        return(registeredPlugin.indexOf(plugin));
    }

    public static @Nullable Plugin findPluginFromID(@NotNull String pluginId){
        if(pluginId.isBlank()){
            return null;
        }

        for(Plugin p: registeredPlugin){
            if(p.id().equals(pluginId)){
                return p;
            }
        }
        return null;
    }

    public static @Nullable Object getEventFromPlugin(Plugin plugin){
        try{
            return eventClassRegistry.get(registeredPlugin.indexOf(plugin));
        }catch(IndexOutOfBoundsException e){
            return null;
        }

    }


}
