package rest.addressbook.config;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import rest.addressbook.domain.AddressBook;
import rest.addressbook.web.AddressBookController;

public class ApplicationConfig extends ResourceConfig {

  /**
   * Default constructor
   */
  public ApplicationConfig() {
    this(new AddressBook());

    // support for OpenAPI 3.0
    OpenApiResource openApiResource = new OpenApiResource();
    register(openApiResource);
  }


  /**
   * Main constructor
   *
   * @param addressBook a provided address book
   */
  public ApplicationConfig(final AddressBook addressBook) {
    register(AddressBookController.class);
    register(MOXyJsonProvider.class);
    register(new AbstractBinder() {

      @Override
      protected void configure() {
        bind(addressBook).to(AddressBook.class);
      }
    });

    // support for OpenAPI 3.0
    OpenApiResource openApiResource = new OpenApiResource();
    register(openApiResource);
  }

}
