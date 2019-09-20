package com.qindaorong.api;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author Administrator
 */
public class WatcherDemo {


    public static String CONNECTION_STR="192.168.100.16:2181,192.168.100.17:2181,192.168.100.18:2181";

    public CuratorFramework curatorFramework;
    {
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(CONNECTION_STR).sessionTimeoutMs(5000).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    }


    public WatcherDemo() {
        curatorFramework.start();
    }

    /**
     * 给一个节点添加watch
     * @throws Exception
     */
    public void addListenerWithNode() throws Exception {
        NodeCache nodeCache = new NodeCache(this.curatorFramework,"/watch",false);
        NodeCacheListener listener =() ->{
            System.out.println("receive Node Changed");
            System.out.println(nodeCache.getCurrentData().getPath()+"---"+new String(nodeCache.getCurrentData().getData()));
        };

        nodeCache.getListenable().addListener(listener);
        nodeCache.start();
    }


    /**
     * 给一个节点以及子节点添加watch
     * @throws Exception
     */
    public void addListenerWithNodeAndChild() throws Exception {
        PathChildrenCache cache = new PathChildrenCache(this.curatorFramework,"/watch",true);
        PathChildrenCacheListener listener = (curatorFramework1,pathChildrenCacheEvent)->{
            System.out.println(pathChildrenCacheEvent.getType()+"->"+new String(pathChildrenCacheEvent.getData().getData()));
        };

        cache.getListenable().addListener(listener);
        cache.start();
    }



    public static void main(String[] args) {
        WatcherDemo demo = new WatcherDemo();

        try {
            demo.addListenerWithNode();
            demo.addListenerWithNodeAndChild();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
