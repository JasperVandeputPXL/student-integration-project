package be.openint.pxltraining;

import be.openint.pxltraining.generated.TicketInventory;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ApplicationScoped
public class TicketInventoryCache {
    private static Map<String, TicketInventory> cache = new ConcurrentHashMap<>();

    public void put(String ticketType, TicketInventory ticketInventory) {
        cache.put(ticketType, ticketInventory);
    }

    public TicketInventory get(String ticketType) {
        return cache.get(ticketType);
    }
}
