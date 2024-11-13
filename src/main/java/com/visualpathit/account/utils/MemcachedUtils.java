package com.visualpathit.account.utils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.visualpathit.account.beans.Components;
import com.visualpathit.account.model.User;

import net.spy.memcached.MemcachedClient;

@Service
public class MemcachedUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(MemcachedUtils.class);
    private static Components components;

    @Autowired
    public void setComponents(Components components) {
        MemcachedUtils.components = components;
    }

    public static String setDataToMemcached(User user, String key) {        
        String result;
        int expireTime = 900;
        
        try (MemcachedClient client = createMemcachedConnection()) {
            if (client == null) {
                logger.error("Failed to establish Memcached connection.");
                return "Failed to connect to Memcached";
            }

            Future<Boolean> future = client.set(key, expireTime, user);
            if (future.get()) {
                result = "Data is from DB and inserted into cache!";
                logger.info("Successfully set data in cache with key: {}", key);
            } else {
                result = "Failed to insert data into cache!";
                logger.warn("Failed to set data in cache with key: {}", key);
            }
        } catch (Exception e) {
            logger.error("Exception in setting data to Memcached: ", e);
            result = "Error setting data in Memcached";
        }
        return result;
    }

    public static User getDataFromMemcached(String key) {
        User userData = null;
        
        try (MemcachedClient client = createMemcachedConnection()) {
            if (client != null) {
                userData = (User) client.get(key);
                logger.info("Retrieved data from cache with key: {}", key);
            } else {
                logger.warn("Failed to establish Memcached connection.");
            }
        } catch (Exception e) {
            logger.error("Exception in retrieving data from Memcached: ", e);
        }
        
        return userData;
    }

    private static MemcachedClient createMemcachedConnection() {
        String host = components.getActiveHost();
        String port = components.getActivePort();
        
        try {
            return connectToMemcached(host, port);
        } catch (Exception e) {
            logger.warn("Active Memcached connection failed. Trying standby...");
        }

        // Standby bağlantısını dene
        host = components.getStandByHost();
        port = components.getStandByPort();
        
        try {
            return connectToMemcached(host, port);
        } catch (Exception e) {
            logger.error("Failed to connect to standby Memcached.", e);
        }
        
        return null;
    }

    private static MemcachedClient connectToMemcached(String host, String port) throws Exception {
        if (host == null || port == null || host.isEmpty() || port.isEmpty()) {
            throw new IllegalArgumentException("Host or port is invalid.");
        }
        
        MemcachedClient client = new MemcachedClient(new InetSocketAddress(host, Integer.parseInt(port)));
        logger.info("Connected to Memcached server at {}:{}", host, port);
        
        // Sağlık kontrolü
        for (SocketAddress address : client.getStats().keySet()) {
            String pid = client.getStats().get(address).get("pid");
            if (pid == null) {
                client.shutdown();
                throw new RuntimeException("Failed to connect: server PID not found.");
            }
        }
        
        return client;
    }
}
