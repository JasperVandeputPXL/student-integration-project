package be.openint.pxltraining;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URISyntaxException;

@ApplicationScoped
public class TicketAPIRestSetupRoute extends RouteBuilder {

    @ConfigProperty(name = "ticket-purchase.openapi.filename")
    private String openApiFilename;

    @Override
    public void configure() throws IOException, URISyntaxException {

        restConfiguration()
            .apiContextPath("/api-doc")
            .bindingMode(RestBindingMode.json);

        rest()
            .openApi(openApiFilename).getOpenApi().setMissingOperation("ignore");
    }
}
