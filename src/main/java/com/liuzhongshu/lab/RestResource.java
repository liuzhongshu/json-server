package com.liuzhongshu.lab;

import java.util.List;
import java.util.Map;

import io.dropwizard.setup.Environment;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/{model}")
@Produces(MediaType.APPLICATION_JSON)
public class RestResource {
	
	private DbManager dbManager;
	
	RestResource(DbManager dbManager) {
		this.dbManager = dbManager;
	}
	
	@GET
    public List<Map> list(@PathParam("model") String model) {
		
        return dbManager.list(model);
    }
	
	@GET
    @Path("{id:[0-9]+?}")
    public Map<String,Object> getById(@PathParam("model") String model, @PathParam("id") long id) {
        return dbManager.getById(model, id);
    }
	
	@POST
    public String create(@PathParam("model") String model) {
        return model;
    }
	
	@PUT
    @Path("{id:[0-9]+?}")
    public String modify(@PathParam("model") String model) {
        return model;
    }
	
	@DELETE
    @Path("{id:[0-9]+?}")
    public String delete(@PathParam("model") String model) {
        return model;
    }
	
}
