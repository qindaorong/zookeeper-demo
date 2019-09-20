package com.qindaorong.selector;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.Closeable;
import java.io.IOException;

public class ClientA extends LeaderSelectorListenerAdapter implements Closeable {

    private String name;
    private LeaderSelector leaderSelector;

    public ClientA(String name) {
        this.name = name;
    }

    public LeaderSelector getLeaderSelector() {
        return leaderSelector;
    }

    public void setLeaderSelector(LeaderSelector leaderSelector) {
        this.leaderSelector = leaderSelector;
    }

    public void close() throws IOException {
        leaderSelector.close();
    }

    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        System.out.println(name+"->现在是leader了");
    }

    public static String CONNECTION_STR="192.168.100.16:2181,192.168.100.17:2181,192.168.100.18:2181";

    public static void main(String[] args) throws IOException {
        //build CuratorFramework
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString(CONNECTION_STR).sessionTimeoutMs(5*1000).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        curatorFramework.start();

        ClientA client=new ClientA("ClientA");
        LeaderSelector leaderSelector=new LeaderSelector(curatorFramework,"/leader",client);
        client.setLeaderSelector(leaderSelector);


        client.start(); //开始选举
        System.in.read();
    }

    private void start() {
        leaderSelector.start();
    }
}
