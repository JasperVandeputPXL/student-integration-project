package be.openint.pxltraining;

import be.openint.pxltraining.generated.PurchaseStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@ApplicationScoped
public class TicketStatusCache {
    private static Map<String, PurchaseStatus> cache = new ConcurrentHashMap<>();

    public void put(UUID purchaseId, PurchaseStatus purchaseStatus) {
        cache.put(purchaseId.toString(), purchaseStatus);
    }

    public PurchaseStatus get(UUID purchaseId) {
        return cache.get(purchaseId.toString());
    }
}
