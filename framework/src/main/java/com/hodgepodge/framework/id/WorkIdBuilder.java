package com.hodgepodge.framework.id;

import com.hodgepodge.framework.lock.DistributedLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WorkIdBuilder {
    private static final Logger logger = LoggerFactory.getLogger(WorkIdBuilder.class);
    private static String address;
    private static final int MAX_ORDER = 1024;
    private static final String ROOT_NAME = "/zookeeper";
    private static final String NODE_NAME = "/snowflakeWorkId";
    private static int workerId = 0;
    private static CuratorFramework client;
    private static final String SEPARATOR = "/";

    private WorkIdBuilder() {
    }

    public static synchronized int getWorkerId(String zkAddress) {
        if (workerId > 0) {
            return workerId;
        }
        address = zkAddress;

        // 初始化zk服务
        init();
        // 根据序号创建zk子节点，workId为叶子节点名称
        buildWorkId();
        // 返回workId
        logger.info("snowflake workId: {}", workerId);
        return workerId;
    }

    private static void init() {
        if (client != null) {
            close();
        }
        client = CuratorFrameworkFactory.builder()
                .connectString(address)
                .connectionTimeoutMs(5000)
                // session超时后，zookeeper临时节点被删，下个获取workId的进程会占用该节点，监听到该进程workId节点时间变化后会重新创建workId节点
                // 为了减少workid频繁变更，时间设置为7天
                .sessionTimeoutMs(7 * 24 * 3600 * 1000)
                .canBeReadOnly(false)
                .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
                .build();

        client.start();
    }

    /**
     * 序号集，当前最大支持 1023 个节点，每个节点去占用编号，通过InterProcessLock来控制分布式环境下的获取
     */
    private static Set<Integer> orderIdSet;

    static {
        orderIdSet = new HashSet<>();
        for (int i = 1; i < MAX_ORDER; i++) {
            orderIdSet.add(i);
        }
    }

    /***
     * 获取workId
     */
    private static void buildWorkId() {
        // 检测client是否已经连接上
        if (null == client) {
            throw new RuntimeException("本节点注册到ZK异常。");
        }
        // lockPath,用于加锁，注意要与nodePath区分开
        final String lockPath = ROOT_NAME + SEPARATOR + "snowflake";
        // nodePath 用于存放集群各节点初始路径
        final String nodePath = lockPath + NODE_NAME;
        DistributedLock distributedLock = new DistributedLock(address, "node");
        try {
            // 加锁 此处逻辑非常重要
            distributedLock.lock();
            // nodePath 第一次需初始化，永久保存, 或者节点路径为临时节点，则设置为永久节点
            if (null == client.checkExists().forPath(nodePath)) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(nodePath);
            }
            // 获取nodePath下已经创建的子节点
            Set<Integer> nodeIdSet = getNodeIds(nodePath);
            // 遍历所有id，构建workId，主要是判断可用id是否已经被集群中其他节点占用
            for (Integer order : orderIdSet) {
                if (nodeIdSet.contains(order)) {
                    continue;
                }
                final String currentNodePath = nodePath + SEPARATOR + order;
                String nodeDate = String.format("[ip:%s,hostname:%s,pid:%s]",
                        InetAddress.getLocalHost().getHostAddress(),
                        InetAddress.getLocalHost().getHostName(),
                        ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
                // 事务提交, 应用断开zk连接时候,删除该节点数据,此处CreateMode = EPHEMERAL  (非常重要)
                // 当本节点zk断开时，其他client.getChildren().forPath(nodePath)进行操作时，子节点逻辑已释放，orderId可复用
                client.inTransaction()
                        .create().withMode(CreateMode.EPHEMERAL).forPath(currentNodePath)
                        .and().setData().forPath(currentNodePath, nodeDate.getBytes("UTF-8"))
                        .and().commit();
                listenNodeStatus(currentNodePath);
                workerId = order;
                logger.info("基于ZK成功构建 workId:{}", workerId);
                return;
            }
            throw new RuntimeException("获取WorkId失败，共[" + MAX_ORDER + "]个可用WorkId, 已全部用完。 ");
        } catch (Exception e) {
            logger.error("获取分布式WorkId异常", e);
        } finally {
            if (null != distributedLock) {
                try {
                    distributedLock.unlock();
                } catch (Exception e) {
                    logger.warn("释放锁失败", e);
                }
            }
        }
    }

    /**
     * 监听节点，如果创建时间变化了，则从zk断掉重新注册
     *
     * @param currentNodePath
     * @throws Exception
     */
    private static void listenNodeStatus(String currentNodePath) throws Exception {
        long pathCreateTime = client.checkExists().forPath(currentNodePath).getCtime();
        // 以下逻辑主要用于检测断开重连情况
        TreeCache treeCache = new TreeCache(client, currentNodePath);
        // 添加监听器
        treeCache.getListenable().addListener((curatorFramework, treeCacheEvent) -> {
            long pathTime;
            try {
                pathTime = curatorFramework.checkExists().forPath(currentNodePath).getCtime();
            } catch (Exception e) {
                pathTime = 0;
            }
            // 如果pathTime != pathCreateTime, 那么只能一种情况:
            // 当前应用与zk失去联系,且/nodePath/{currentNodePath}不存在或者被其它应用占据了(表象为pathCreateTime变化)
            // 无论哪种情况,当前应用都要重新注册节点
            if (pathCreateTime != pathTime) {
                logger.info("从ZK断开，再次注册...");
                // 关闭之前旧的treeCache
                try {
                    treeCache.close();
                } catch (Exception e) {
                    logger.warn("treeCache关闭失败");
                }
                // 再次注册
                finally {
                    buildWorkId();
                }
            }
        });
        treeCache.start();
    }

    /**
     * 获取nodePath下已经创建的子节点
     *
     * @param nodePath
     * @return
     * @throws Exception
     */
    private static Set<Integer> getNodeIds(String nodePath) throws Exception {
        List<String> childPath = client.getChildren().forPath(nodePath);
        Set<Integer> nodeIdSet = new LinkedHashSet<>();
        if (!CollectionUtils.isEmpty(childPath)) {
            for (String path : childPath) {
                try {
                    nodeIdSet.add(Integer.valueOf(path));
                } catch (Exception e) {
                    logger.warn("路径由不合法操作创建，注意[{}]仅用于构建workId", nodePath);
                    // ignore
                }
            }
        }
        return nodeIdSet;
    }

    private static void close() {
        if (null != client && null == client.getState()) {
            client.close();
        }
        client = null;
    }

}
