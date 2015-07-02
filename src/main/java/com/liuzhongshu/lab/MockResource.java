package com.liuzhongshu.lab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/{seg:.*}")
@Produces(MediaType.APPLICATION_JSON)
public class MockResource {

	private DbManager dbManager;
	private ObjectMapper objectMapper;
	
	public MockResource(DbManager dbManager, ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.dbManager = dbManager;
	}
	
	@GET
    public Object get(@PathParam("seg") List<PathSegment> segments, @Context UriInfo uriInfo) throws JsonException {
		dbManager.loadMemDb();
        String path = StringUtils.join(segments, "/");
        
        List<Map> resources  = dbManager.getMockResource(path,"GET");        
        if (resources != null)
        	return processMockRequest(MultivaluedMap2map(uriInfo.getQueryParameters()), resources);
        
        
        String model = segments.get(0).getPath();
        if (dbManager.isModel(model)) {
        	if (segments.size() > 1) {
        		String entity = segments.get(1).getPath();
        		if (entity.matches("[0-9]+"))
        			return dbManager.getById(model, Long.parseLong(entity));
        	}
        	else{
        		return dbManager.list(model);
        	}
        }
        
        throw new WebApplicationException(new Exception("Resource not found"), 404);
    }

	/*
	 * can not use Map<String,Object> as parameter, or will get 415 error when client send empty payload
	 */
	@POST
    public Object post(@PathParam("seg") List<PathSegment> segments, String body) throws JsonException {
		dbManager.loadMemDb();
        String path = StringUtils.join(segments, "/");
        
        List<Map> resources  = dbManager.getMockResource(path,"POST");   
        Map<String,Object> paras = getMapPayload(body);
        if (resources != null)
        	return processMockRequest(paras, resources);
        
        
        String model = segments.get(0).getPath();
        if (dbManager.isModel(model)) {
       		dbManager.create(model,paras);
       		return new JsonResult();
        }
        
        throw new JsonException("resource not found", 404);
    }
	
	
	@PUT
    public Object put(@PathParam("seg") List<PathSegment> segments, String body) throws Exception {
		dbManager.loadMemDb();
        String path = StringUtils.join(segments, "/");
        
        List<Map> resources  = dbManager.getMockResource(path,"PUT");
        Map<String,Object> paras = getMapPayload(body);
        if (resources != null)
        	return processMockRequest(paras, resources);
        
        
        String model = segments.get(0).getPath();
        if (dbManager.isModel(model)) {
   			dbManager.update(model, paras);
   			return new JsonResult();
        }
        
        throw new JsonException("resource not found", 404);
    }
	
	@DELETE
    public Object delete(@PathParam("seg") List<PathSegment> segments, String body) throws JsonException {	
		dbManager.loadMemDb();
        String path = StringUtils.join(segments, "/");
        
        List<Map> resources  = dbManager.getMockResource(path,"DELETE");  
        Map<String,Object> paras = getMapPayload(body);
        if (resources != null)
        	return processMockRequest(paras, resources);
        
        
        String model = segments.get(0).getPath();
        if (dbManager.isModel(model)) {
        	if (segments.size() > 1) {
        		String entity = segments.get(1).getPath();
        		if (entity.matches("[0-9]+"))
        			dbManager.delteById(model, Long.parseLong(entity));
        			return new JsonResult();
        	}
        }
        
        throw new JsonException("resource not found", 404);
    }
	
	private Map<String, Object> getMapPayload(String body) {
		try {
			return objectMapper.readValue(body, HashMap.class);
		}
		catch(Exception e) {
			return null;
		}
	}

	private Object processMockRequest(Map<String,Object> paras, List<Map> resources) throws JsonException {
		for (Map resource : resources) {
			
			if (mapInMap((Map<String,Object>)resource.get("request"), paras)){
					Object response = resource.get("response");
					if (response instanceof Map) {
						Object exception = ((Map<String,Object>)response).get("!exception");
						if (exception != null)
							throw new JsonException(exception.toString());
					}
					return response;
			}
		}
		
		throw new WebApplicationException("resource not found");
	}

	private boolean mapInMap(Map<String,Object> sourceMap, Map<String, Object> targetMap) {
		if (sourceMap == null)
			return true;
		if (targetMap == null)
			return false;
		
		boolean result = true;
		for (String key : sourceMap.keySet()) 
		{
			if (targetMap.get(key) == null || !sourceMap.get(key).equals(targetMap.get(key))) {
				result = false;
				break;
			}
		}
		
		return result;			
	}
	
	private Map<String,Object> MultivaluedMap2map(MultivaluedMap<String,String> smap) {
		Map<String,Object> tmap = new HashMap<String,Object>();
		for (Entry<String, List<String>> entry : smap.entrySet()) {
			tmap.put(entry.getKey(), entry.getValue().get(0));
		}
		
		return tmap;
	}
}
