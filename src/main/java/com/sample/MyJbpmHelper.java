package com.sample;

import java.util.Properties;

import org.h2.tools.Server;
import org.jbpm.test.JBPMHelper;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class MyJbpmHelper {
	public static Server startH2Server() {
        try {
            // start h2 in memory database
            Server server = Server.createTcpServer(new String[0]);
            server.start();
            return server;
        } catch (Throwable t) {
            throw new RuntimeException("Could not start H2 server", t);
        }
    }

    public static PoolingDataSource setupDataSource() {
        Properties properties = getProperties();
        // create data source
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName(properties.getProperty("persistence.datasource.name", "jdbc/jbpm-ds"));
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", properties.getProperty("persistence.datasource.user", "root"));
        pds.getDriverProperties().put("password", properties.getProperty("persistence.datasource.password", "root"));
        pds.getDriverProperties().put("url", properties.getProperty("persistence.datasource.url", "jdbc:mysql://localhost:3306/myplmDB;MVCC=TRUE"));
        pds.getDriverProperties().put("driverClassName", properties.getProperty("persistence.datasource.driverClassName", "com.mysql.jdbc.Driver"));
        //pds.init();
        return pds;
    }
    public static void startUp() {
       // cleanupSingletonSessionId();
        Properties properties = getProperties();
        String driverClassName = properties.getProperty("persistence.datasource.driverClassName", "org.hibernate.dialect.MySQLDialect");
        if (driverClassName.startsWith("org.hibernate.dialect")) {
        	MyJbpmHelper.startH2Server();
        }
        String persistenceEnabled = properties.getProperty("persistence.enabled", "false");
        String humanTaskEnabled = properties.getProperty("taskservice.enabled", "false");
        if ("true".equals(persistenceEnabled) || "true".equals(humanTaskEnabled)) {
            MyJbpmHelper.setupDataSource();
        }
        /*if ("true".equals(humanTaskEnabled)) {
            JBPMHelper.startTaskService();
        }*/
    }
    public static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(MyJbpmHelper.class.getResourceAsStream("/jBPM.properties"));
        } catch (Throwable t) {
            // do nothing, use defaults
        }
        return properties;
    }
    



}
