package com.qindaorong.api;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * @author Administrator
 */
public class CuratorApiDemo {

    public static String CONNECTION_STR="192.168.100.16:2181,192.168.100.17:2181,192.168.100.18:2181";

    public CuratorFramework curatorFramework;
    {
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(CONNECTION_STR).sessionTimeoutMs(5000).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    }

    public CuratorApiDemo() {
        curatorFramework.start();


//CRUD
//        curatorFramework.create();
//        curatorFramework.setData(); //修改
//        curatorFramework.delete() ;// 删除
//        curatorFramework.getData(); //查询
    }

    /**
     * 新增
     * @throws Exception
     */
    public void createData() throws Exception {

        //CreateMode.PERSISTENT-->持久节点
        //CreateMode.PERSISTENT_SEQUENTIAL-->持久有序节点
        //CreateMode.EPHEMERAL-->临时节点
        //CreateMode.EPHEMERAL_SEQUENTIAL-->临时有序节点
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                forPath("/data/program","test".getBytes());
    }

    /**
     * 修改
     * @throws Exception
     */
    public void updateData() throws Exception {
        curatorFramework.setData().forPath("/data/program","up".getBytes());

    }


    /**
     * 删除
     * @throws Exception
     */
    public void deleteData() throws Exception {
        Stat stat=new Stat();
        String value=new String(curatorFramework.getData().storingStatIn(stat).forPath("/data/program"));
        curatorFramework.delete().withVersion(stat.getVersion()).forPath("/data/program");
    }

    public static void main(String[] args) throws IOException {
        CuratorApiDemo apiDemo = new CuratorApiDemo();

        try {
            apiDemo.createData();
            //apiDemo.updateData();
            //apiDemo.deleteData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
