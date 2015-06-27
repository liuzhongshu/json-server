package com.liuzhongshu.lab;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;


public class JsonServerApp extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new JsonServerApp().run("server","server.yml");
    }

    @Override
    public String getName() {
        return "JsonServer";
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
    	bootstrap.addBundle(new AssetsBundle("/assets/", "/"));  
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
    	
    	
    	DbManager dbManager = new DbManager();
        environment.lifecycle().manage(dbManager);
        
    	environment.jersey().register(new MockResource(dbManager));
    	
    }

}