package me.chickblock.serverMessenger;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerEvent;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerInitialiseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.rmi.UnexpectedException;
import java.util.ArrayList;

public class EventClassRegistry {
    private static ArrayList<Object> eventClassRegistry = new ArrayList<Object>();
    private static ArrayList<Integer> idList = new ArrayList<Integer>();
    private static ArrayList<Plugin> registeredPlugin = new ArrayList<Plugin>();
    private static int idCount = -1;
    private static boolean initialise = false;
    private static boolean active = false;
    private static Logger logger;

    protected static void init(org.slf4j.Logger logger){
        if(initialise){
            return;
        }
        EventClassRegistry.logger = logger;
        EventClassRegistry.initialise = true;
    }

    @Subscribe
    private void onServerMessengerInitialised(ServerMessengerInitialiseEvent event) throws UnexpectedException {
        if(initialise){
            active = true;
        }else{
            throw new UnexpectedException("FATAL ERROR: Somehow the ServerMessengerInitialiseEvent was thrown before the Event Class Registry was fully initialised. This should NEVER happen.");
        }

    }

    public boolean registerEvent(Object eventToRegister, Plugin plugin){
        if(!active){
            logger.warn("A plugin is attempting to interact with the Event Class Registry has been initialised. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return false;
        }
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
        if(!initialise){
            throw new InternalError("SERVER MESSENGER INTERNAL ERROR: Attempted to access event class registry before registry has been initialised. This should not happen.");
        }
        return eventClassRegistry;
    }

    protected static ArrayList<Integer> getIdList() {
        if(!initialise){
            throw new InternalError("SERVER MESSENGER INTERNAL ERROR: Attempted to access event class registry before registry has been initialised. This should not happen.");
        }
        return idList;
    }

    public static int getIndexOfPlugin(Plugin plugin){
        if(!active){
            logger.warn("A plugin is attempting to interact with the Event Class Registry has been initialised. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return -1;
        }
        return(registeredPlugin.indexOf(plugin));
    }

    public static @Nullable Plugin findPluginFromID(@NotNull String pluginId){
        if(!active){
            logger.warn("A plugin is attempting to interact with the Event Class Registry has been initialised. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return null;
        }
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
        if(!active){
            logger.warn("A plugin is attempting to interact with the Event Class Registry has been initialised. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return null;
        }
        try{
            return eventClassRegistry.get(registeredPlugin.indexOf(plugin));
        }catch(IndexOutOfBoundsException e){
            return null;
        }

    }

    protected static boolean isInitialised() {
        return initialise;
    }

    public static boolean isActive(){
        return active;
    }


}
