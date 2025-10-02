package be.openint.pxltraining;

import be.openint.pxltraining.exception.TicketInventoryNotFoundException;
import be.openint.pxltraining.generated.TicketInventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import java.util.Objects;

@ApplicationScoped
public class TicketInventoryAPIRoute extends RouteBuilder{

    @Inject
    TicketInventoryCache ticketInventoryCache;

    @Override
    public void configure() throws Exception {
        onException(TicketInventoryNotFoundException.class)
                .handled(true)
                .transform(exceptionMessage())
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404));

        from("direct:ticketInventory")
            .routeId(getClass().getSimpleName())
            .log("ticket inventory request for type: ${header.ticketType}")
            .process(exchange -> {
                String ticketType = exchange.getIn().getHeader("ticketType", String.class);
                TicketInventory ticketInventory = ticketInventoryCache.get(ticketType);
                if(Objects.isNull(ticketInventory)) {
                    throw new TicketInventoryNotFoundException("not inventory found for ticket type '" + ticketType +"'");
                }
                exchange.getIn().setBody(ticketInventory);
            });
    }
}
