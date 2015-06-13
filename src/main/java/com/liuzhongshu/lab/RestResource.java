package com.liuzhongshu.lab;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
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
    public List<Map> list(@PathParam("model") String model) throws Exception {
        return dbManager.list(model);
    }
	
	@GET
    @Path("{id:[0-9]+?}")
    public Map<String,Object> getById(@PathParam("model") String model, @PathParam("id") long id) throws Exception {
        return dbManager.getById(model, id);
    }
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    public JsonResult create(@PathParam("model") String model, Map<String, Object> entity) throws Exception {
		dbManager.create(model, entity);
        return new JsonResult();
    }
	
	@PUT
    @Path("{id:[0-9]+?}")
    public JsonResult update(@PathParam("model") String model, Map<String, Object> entity) throws Exception {
		dbManager.update(model, entity);
		return new JsonResult();
    }
	
	@DELETE
    @Path("{id:[0-9]+?}")
    public JsonResult delete(@PathParam("model") String model, @PathParam("id") long id) throws Exception {
		dbManager.delteById(model, id);
		return new JsonResult();
    }
	
}
