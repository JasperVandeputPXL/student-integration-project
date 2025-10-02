package be.openint.pxltraining.util;

import be.openint.pxltraining.TicketStatusCache;
import be.openint.pxltraining.generated.PurchaseStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class TicketStatusCacheRoute extends RouteBuilder {
    @Inject
    TicketStatusCache ticketStatusCache;

    @Override
    public void configure() throws Exception {
        from("direct:loadPurchaseStatus")
            .inputType(PurchaseStatus.class)
            .routeId(getClass().getSimpleName())
            .process(exchange -> {
                PurchaseStatus purchaseStatus = exchange.getIn().getBody(PurchaseStatus.class);
                ticketStatusCache.put(purchaseStatus.getPurchaseId(), purchaseStatus);
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));
            });
    }
}
