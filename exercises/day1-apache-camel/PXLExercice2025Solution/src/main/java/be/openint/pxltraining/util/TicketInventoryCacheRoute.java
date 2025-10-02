package be.openint.pxltraining.util;

import be.openint.pxltraining.TicketInventoryCache;
import be.openint.pxltraining.generated.PurchaseStatus;
import be.openint.pxltraining.generated.TicketInventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class TicketInventoryCacheRoute extends RouteBuilder {
    @Inject
    TicketInventoryCache ticketInventoryCache;

    @Override
    public void configure() throws Exception {
        from("direct:loadTicketInventory")
            .inputType(TicketInventory.class)
            .routeId(getClass().getSimpleName())
            .process(exchange -> {
                TicketInventory ticketInventory = exchange.getIn().getBody(TicketInventory.class);
                ticketInventoryCache.put(ticketInventory.getTicketType(), ticketInventory);
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));
            });
    }
}
