package org.jeecf.kong.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * 配置信息实体
 * 
 * @author jianyiming
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultiConfigEntity {
    /**
     * 当前路径
     */
    private String path;
    /**
     * 全路径
     */
    private String rootPath;
    /**
     * 节点值
     */
    private String value;
    /**
     * 子节点
     */
    private List<MultiConfigEntity> childrens;
    /**
     * 父节点路径
     */
    private String parent;

}
