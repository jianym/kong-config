package org.jeecf.kong.config.server;

import org.jeecf.kong.config.MultiConfigEntity;

/**
 * 配置信息操作service接口
 * 
 * @author jianyiming
 *
 */
public interface ConfigInfoServer {
    /**
     * 查询当前路径下所有子节点
     * 
     * @param path
     * @return 查询多叉树
     * @throws Exception
     */
    public MultiConfigEntity query(String path) throws Exception;

    /**
     * 添加节点
     * 
     * @param path
     * @param value
     * @return 返回添加路径
     * @throws Exception
     */
    public String add(String path, String value) throws Exception;

    /**
     * 移除节点
     * 
     * @param path
     * @throws Exception
     */
    public void remove(String path) throws Exception;

    /**
     * 更新节点
     * 
     * @param path
     * @param value
     * @return 更新路径
     * @throws Exception
     */
    public String update(String path, String value) throws Exception;

    /**
     * 监听
     * 
     * @param rootPath
     * @throws Exception
     */
    public void listener(String rootPath) throws Exception;

}
