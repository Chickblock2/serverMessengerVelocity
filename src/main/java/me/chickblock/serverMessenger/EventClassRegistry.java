package me.chickblock.serverMessenger;
import com.velocitypowered.api.event.Subscribe;
import me.chickblock.serverMessenger.MessageEvents.EventRegistryEntry;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerEvent;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerInitialiseEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import java.rmi.UnexpectedException;
import java.util.ArrayList;

public class EventClassRegistry {
    private static ArrayList<EventRegistryEntry> eventClassRegistry = new ArrayList<EventRegistryEntry>();
    private static boolean initialise = false;
    private static boolean active = false;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EventClassRegistry.class);

    protected static void init(){
        if(initialise){
            return;
        }
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

    // Registry Access Methods - PUBLIC
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
