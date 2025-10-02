package be.openint.pxltraining;

import be.openint.pxltraining.exception.PurchaseNotFoundException;
import be.openint.pxltraining.generated.PurchaseStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class TicketPaymentStatusAPIRoute extends RouteBuilder{

    @Inject
    TicketStatusCache ticketStatusCache;

    @Override
    public void configure() throws Exception {
        onException(IllegalArgumentException.class)
            .handled(true)
            .transform().simple("invalid purchaseId, it has to be a UUID: ${header.purchaseId}")
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400));

        onException(PurchaseNotFoundException.class)
                .handled(true)
                .transform(exceptionMessage())
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404));

        from("direct:purchaseStatus")
            .routeId(getClass().getSimpleName())
            .log("ticket purchase status request for purchaseId: ${header.purchaseId}")
            .process(exchange -> {
                String purchaseIdParam = exchange.getIn().getHeader("purchaseId", String.class);
                UUID purchaseId = UUID.fromString(purchaseIdParam);
                PurchaseStatus purchaseStatus = ticketStatusCache.get(purchaseId);
                if(Objects.isNull(purchaseStatus)) {
                    throw new PurchaseNotFoundException("purchase with id '" + purchaseIdParam + "' was not found");
                }
                exchange.getIn().setBody(purchaseStatus);
            });
    }
}
