package me.chickblock.serverMessenger;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.PluginContainer;
import me.chickblock.serverMessenger.MessageEvents.EventRegistryEntry;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerEvent;
import me.chickblock.serverMessenger.MessageEvents.ServerMessengerInitialiseEvent;
import org.jetbrains.annotations.NotNull;
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
    public static boolean registerEvent(EventRegistryEntry entry){
        if(!active){
            log.warn("A plugin is attempting to interact with the Event Class Registry has been initialised. If you are the developer please wait for for ServerMessengerInitialiseEvent before attempting to interact with Server Messenger.");
            return false;
        }
        if(eventClassRegistry.contains(entry) || entry.eventToFire().getClass().isAssignableFrom(ServerMessengerEvent.class)){
            return false;
        }else{
            eventClassRegistry.add(entry);
            return true;
        }
    }

    public static boolean isActive(){
        return active;
    }


    public static @Nullable Object getEvent(@Nullable String pluginId, @Nullable String keyWord){
        for(EventRegistryEntry entry: eventClassRegistry){
            if(entry.pluginId().equals(pluginId) && entry.keyWord().equals(keyWord)){
                return entry.eventToFire();
            }
        }
        return null;
    }




}
