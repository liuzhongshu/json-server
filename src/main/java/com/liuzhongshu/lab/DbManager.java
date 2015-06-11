package com.liuzhongshu.lab;

import io.dropwizard.lifecycle.Managed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		objectMapper.writeValue(new File("db.json"), memDb);		
	}

	public List<Map> list(String model) {
		Object entities = memDb.get(model);
		if ( entities instanceof List) {
			return (List<Map>)entities;
		}
		return null;
	}

	public Map<String,Object> getById(String model, long id) {
		Object entities = memDb.get(model);
		if ( entities instanceof List) {
			for (Map entity : (List<Map>)entities) {
				if (entity.get("id") != null && id == Long.parseLong(entity.get("id").toString()))
					return entity;
			}
		}
		return null;
	}
}
