package com.qindaorong.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;


public class LockDemo {

    public static String CONNECTION_STR="192.168.100.16:2181,192.168.100.17:2181,192.168.100.18:2181";

    public static void main(String[] args) throws IOException {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString(CONNECTION_STR).sessionTimeoutMs(5000).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        curatorFramework.start();

        final InterProcessMutex lock = new InterProcessMutex(curatorFramework,"/locks");

        for(int i =0; i< 10 ;i++ ){
            System.out.println(Thread.currentThread().getName()+"->尝试竞争锁");
            new Thread(()->{
                try {
                    lock.acquire();
                    System.out.println(Thread.currentThread().getName()+"->成功获得了锁");

                    Thread.sleep(5 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },"Thread-"+i).start();
        }
    }
}
