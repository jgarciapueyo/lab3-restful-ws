package rest.addressbook.web;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import rest.addressbook.domain.AddressBook;
import rest.addressbook.domain.Person;

/**
 * A service that manipulates contacts in an address book.
 */
@Path("/contacts")
public class AddressBookController {

    /**
     * The (shared) address book object.
     */
    @Inject
    AddressBook addressBook;

    /**
     * A GET /contacts request should return the address book in JSON.
     *
     * @return a JSON representation of the address book.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "List all users",
            description = "List all users saved in the address book",
            responses = {
                    @ApiResponse(
                            description = "The address book",
                            content = @Content(schema = @Schema(implementation = AddressBook.class))
                    )
            }
    )
    public AddressBook getAddressBook() {
        return addressBook;
    }

    /**
     * A POST /contacts request should add a new entry to the address book.
     *
     * @param info   the URI information of the request
     * @param person the posted entity
     * @return a JSON representation of the new entry that should be available at
     * /contacts/person/{id}.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Saves a user",
            description = "Saves a user in the address book",
            responses = {
                    @ApiResponse(
                            description = "The user",
                            content = @Content(schema = @Schema(implementation = Person.class))
                    )
            }
    )
    public Response addPerson(@Context UriInfo info, Person person) {
        addressBook.getPersonList().add(person);
        person.setId(addressBook.nextId());
        person.setHref(info.getAbsolutePathBuilder().path("person/{id}").build(person.getId()));
        return Response.created(person.getHref()).entity(person).build();
    }

    /**
     * A GET /contacts/person/{id} request should return a entry from the address book
     *
     * @param id the unique identifier of a person
     * @return a JSON representation of the new entry or 404
     */
    @GET
    @Path("/person/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find user",
            description = "Finds user by ID inside the address book",
            responses = {
                    @ApiResponse(
                            description = "The user",
                            content = @Content(schema = @Schema(implementation = Person.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public Response getPerson(@PathParam("id") int id) {
        for (Person p : addressBook.getPersonList()) {
            if (p.getId() == id) {
                return Response.ok(p).build();
            }
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    /**
     * A PUT /contacts/person/{id} should update a entry if exists
     *
     * @param info   the URI information of the request
     * @param person the posted entity
     * @param id     the unique identifier of a person
     * @return a JSON representation of the new updated entry or 400 if the id is not a key
     */
    @PUT
    @Path("/person/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Update user",
            description = "Update user depending on ID in the address book",
            responses = {
                    @ApiResponse(
                            description = "The user",
                            content = @Content(schema = @Schema(implementation = Person.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Person with that ID does not exist")
            }
    )
    public Response updatePerson(@Context UriInfo info,
                                 @PathParam("id") int id, Person person) {
        for (int i = 0; i < addressBook.getPersonList().size(); i++) {
            if (addressBook.getPersonList().get(i).getId() == id) {
                person.setId(id);
                person.setHref(info.getAbsolutePath());
                addressBook.getPersonList().set(i, person);
                return Response.ok(person).build();
            }
        }
        return Response.status(Status.BAD_REQUEST).build();
    }

    /**
     * A DELETE /contacts/person/{id} should delete a entry if exists
     *
     * @param id the unique identifier of a person
     * @return 204 if the request is successful, 404 if the id is not a key
     */
    @DELETE
    @Path("/person/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Delete User",
            description = "Delete user depending on ID in the address book",
            responses = {
                    @ApiResponse(responseCode = "204", description = "The user was correctly deleted"),
                    @ApiResponse(responseCode = "404", description = "The user does not exist")
            }
    )
    public Response deletePerson(@PathParam("id") int id) {
        for (int i = 0; i < addressBook.getPersonList().size(); i++) {
            if (addressBook.getPersonList().get(i).getId() == id) {
                addressBook.getPersonList().remove(i);
                return Response.noContent().build();
            }
        }
        return Response.status(Status.NOT_FOUND).build();
    }
}
