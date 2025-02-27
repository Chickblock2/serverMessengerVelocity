package me.chickblock.serverMessenger;
import me.chickblock.serverMessenger.MessageEvents.EventRegistryEntry;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerEvent;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;

public class EventClassRegistry {
    private static ArrayList<EventRegistryEntry> eventClassRegistry = new ArrayList<EventRegistryEntry>();
    private static boolean active = false;
    private static org.slf4j.Logger log;

    static void init(org.slf4j.Logger logger){
        if(active){
            return;
        }
        EventClassRegistry.log = logger;
        EventClassRegistry.active = true;
    }

    public static boolean registerEvent(@Nullable EventRegistryEntry entry){
        if(!active){
            log.warn("A plugin is attempting to interact with the Event Class Registry has been activated. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return false;
        }
        if(entry != null){
            if(entry.getRegisteredPlugin() != null || entry.getObjectToFire() != null){
                if(!eventClassRegistry.contains(entry) || !entry.getObjectToFire().getClass().isAssignableFrom(ServerMessengerEvent.class)){
                    eventClassRegistry.add(entry);
                    return true;
                }
            }
        }
        return  false;
    }

    public static boolean isActive(){
        return active;
    }

    public static @Nullable Object getEvent(@Nullable String pluginId, @Nullable String keyWord){
        for(EventRegistryEntry entry: eventClassRegistry){
            if(entry.getRegisteredPlugin().getDescription().getId().equals(pluginId) && entry.getKeyword().equals(keyWord)){
                return entry.getObjectToFire();
            }
        }
        return null;
    }
}
