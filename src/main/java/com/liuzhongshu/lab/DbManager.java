package com.liuzhongshu.lab;

import io.dropwizard.lifecycle.Managed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DbManager implements Managed {

	private ObjectMapper objectMapper;
	private Map<String,Object> memDb;
	
	public DbManager(ObjectMapper objectMapper) throws JsonException {
		this.objectMapper = objectMapper;		
		loadMemDb();
	}
	
	public void loadMemDb() throws JsonException {
		try {
			memDb = objectMapper.readValue(new File("db.json"), HashMap.class);
		} catch (Exception e) {
			throw new JsonException("load db.json error");
		}
	}
	
	@Override
	public void start() throws Exception {
	}

	@Override
	public void stop() throws Exception {
	}

	public List<Map> list(String model) throws JsonException {
		Object entities = memDb.get(model);
		if(!(entities instanceof List))
			throw new JsonException("model not found");

		return (List<Map>)entities;
	}

	public boolean isModel (String model) {
		Object entities = memDb.get(model);
		if(entities instanceof List)
			return true;
		else
			return false;
	}
	
	public Map<String,Object> getById(String model, long id) throws JsonException {
		Object entities = memDb.get(model);
		if(!(entities instanceof List))
			throw new JsonException("model not found");
		
		for (Map entity : (List<Map>)entities) {
			if (entity.get("id") != null && id == Long.parseLong(entity.get("id").toString()))
				return entity;
		}
		
		throw new JsonException("entity not found");
	}
	
	public void save() throws JsonException {
		try {
			objectMapper.writeValue(new File("db.json"), memDb);
		} catch (Exception e) {
			throw new JsonException("save db.json error");
		}
	}

	public void delteById(String model, long id) throws JsonException {
		Object entities = memDb.get(model);
		if(!(entities instanceof List))
			throw new JsonException("model not found");
		
		Iterator<Map> iter = ((List) entities).iterator();
		while (iter.hasNext()) {
			Map entity = iter.next();
			if (entity.get("id") != null && id == Long.parseLong(entity.get("id").toString())) {
				iter.remove();
				save();
				return;
			}
		}
		
		throw new JsonException("entity not found");
	}

	public void create(String model, Map<String, Object> entity) throws JsonException {
		Object entities = memDb.get(model);
		if(!(entities instanceof List))
			throw new JsonException("model not found");
		
		try {
			long id = Long.parseLong(entity.get("id").toString());
		}catch(Exception e) {
			long maxid=0;
			for (Map<String,Object> old : (List<Map<String,Object>>)entities) {
				Object id = old.get("id");
				if (id!= null && Long.parseLong(id.toString()) > maxid)
					maxid = Long.parseLong(id.toString());
			}
			entity.put("id", maxid + 1);
		}
		
		((List)entities).add(entity);
		save();
	}

	public void update(String model, Map<String, Object> entity) throws JsonException {
		Object entities = memDb.get(model);
		if(!(entities instanceof List))
			throw new JsonException("model not found");
		
		long id = 0;
		try {
			id = Long.parseLong(entity.get("id").toString());
		}catch(Exception e) {
			throw new JsonException("entity should have id");
		}
		
		Iterator<Map> iter = ((List) entities).iterator();
		while (iter.hasNext()) {
			Map item = iter.next();
			if (entity.get("id") != null && id == Long.parseLong(item.get("id").toString())) {
				iter.remove();
				((List) entities).add(entity);
				save();
				return;
			}
		}		
		throw new JsonException("entity not found");
	}
	
	
	public List<Map> getMockResource(String path, String method) {
		Object resource = memDb.get(path + "@" + method);
		if (resource != null && resource instanceof List)
			return (List<Map>)memDb.get(path + "@" + method);
		else
			return null;
	}
	
}
