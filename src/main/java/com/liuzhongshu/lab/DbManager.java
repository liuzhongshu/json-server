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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DbManager implements Managed {

	private ObjectMapper objectMapper;
	private Map<String,Object> memDb;
	
	public DbManager() throws Exception {
		objectMapper = new ObjectMapper();
		memDb = objectMapper.readValue(new File("db.json"), HashMap.class);
	}
	
	@Override
	public void start() throws Exception {
	}

	@Override
	public void stop() throws Exception {
	}

	public List<Map> list(String model) throws Exception {
		Object entities = memDb.get(model);
		if(!(entities instanceof List))
			throw new Exception("model not found");

		return (List<Map>)entities;
	}

	public Map<String,Object> getById(String model, long id) throws Exception {
		Object entities = memDb.get(model);
		if(!(entities instanceof List))
			throw new Exception("model not found");
		
		for (Map entity : (List<Map>)entities) {
			if (entity.get("id") != null && id == Long.parseLong(entity.get("id").toString()))
				return entity;
		}
		
		throw new Exception("entity not found");
	}
	
	public void save() throws Exception {
		objectMapper.writeValue(new File("db.json"), memDb);
	}

	public void delteById(String model, long id) throws Exception {
		Object entities = memDb.get(model);
		if(!(entities instanceof List))
			throw new Exception("model not found");
		
		Iterator<Map> iter = ((List) entities).iterator();
		while (iter.hasNext()) {
			Map entity = iter.next();
			if (entity.get("id") != null && id == Long.parseLong(entity.get("id").toString())) {
				iter.remove();
				save();
				return;
			}
		}
		
		throw new Exception("entity not found");
	}

	public void create(String model, Map<String, Object> entity) throws Exception {
		Object entities = memDb.get(model);
		if(!(entities instanceof List))
			throw new Exception("model not found");
		
		try {
			long id = Long.parseLong(entity.get("id").toString());
		}catch(Exception e) {
			throw new Exception("entity should have id");
		}
		
		((List)entities).add(entity);
		save();
	}

	public void update(String model, Map<String, Object> entity) throws Exception {
		Object entities = memDb.get(model);
		if(!(entities instanceof List))
			throw new Exception("model not found");
		
		long id = 0;
		try {
			id = Long.parseLong(entity.get("id").toString());
		}catch(Exception e) {
			throw new Exception("entity should have id");
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
		throw new Exception("entity not found");
	}
}
