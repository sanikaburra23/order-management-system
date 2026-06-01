package com.b2r.ordermgmt.resources;

import com.b2r.ordermgmt.db.ItemDAO;
import com.b2r.ordermgmt.core.Item;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final ItemDAO itemDAO;

    public ItemResource(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }
    @GET
    @UnitOfWork
    public List<Item> getItems() {
        return itemDAO.getAllItems();
    }
    @GET
    @Path("/{id}")
    @UnitOfWork
    public Item getItemById(@PathParam("id") Long id) {
        Optional<Item> item = itemDAO.findById(id);
        if (!item.isPresent()) {
            throw new WebApplicationException("Menu item not found!", Response.Status.NOT_FOUND);
        }
        return item.get();
    }
    @POST
    @UnitOfWork
    public Response createItem(Item newItem) {
        Item savedItem = itemDAO.save(newItem); // Saves row via Hibernate
        return Response.status(Response.Status.CREATED).entity(savedItem).build();
    }
    @PUT
    @Path("/{id}")
    @UnitOfWork
    public Response updateItem(@PathParam("id") Long id, Item updatedItemDetails) {
        Optional<Item> itemOptional = itemDAO.findById(id);
        if (!itemOptional.isPresent()) {
            throw new WebApplicationException("Item not found to update!", Response.Status.NOT_FOUND);
        }

        Item existingItem = itemOptional.get();
        existingItem.setName(updatedItemDetails.getName());
        existingItem.setPrice(updatedItemDetails.getPrice());
        existingItem.setStockQuantity(updatedItemDetails.getStockQuantity());

        itemDAO.save(existingItem);
        return Response.ok(existingItem).build();
    }
    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response deleteItem(@PathParam("id") Long id) {
        Optional<Item> itemOptional = itemDAO.findById(id);
        if (itemOptional.isPresent()) {
            itemDAO.delete(itemOptional.get());
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}