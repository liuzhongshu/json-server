package com.liuzhongshu.lab;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;


public class JsonServerApp extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new JsonServerApp().run(args);
    }

    @Override
    public String getName() {
        return "JsonServer";
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
         
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
    	
    	
    	DbManager dbManager = new DbManager();
        environment.lifecycle().manage(dbManager);
        
    	environment.jersey().register(new RestResource(dbManager));
    }

}