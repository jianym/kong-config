package org.jeecf.kong.config.controller;

import org.jeecf.common.model.Response;
import org.jeecf.kong.config.MultiConfigEntity;
import org.jeecf.kong.config.server.DataSourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 配置中心 mvc操作入口
 * 
 * @author jianyiming
 *
 */
@Controller
@RequestMapping("kong/config")
public class ConfigController {

    @Autowired
    private DataSourceManager dataSource;
    
    @RequestMapping({"","index"})
    public String index() {
        return "redirect:/kong/config/index.html";
    }

    @PostMapping(value = "query")
    @ResponseBody
    public Response<MultiConfigEntity> query(@RequestBody NodeVo node) throws Exception {
        Response<MultiConfigEntity> res = new Response<>();
        try {
            res.setData(dataSource.query(node.getPath()));
            res.setSuccess(true);
            res.setErrorCode(200);
        } catch (Exception e) {
            res.setErrorCode(1000);
            res.setSuccess(false);
            res.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    @PostMapping(value = "add")
    @ResponseBody
    public Response<String> add(@RequestBody NodeVo node) throws Exception {
        Response<String> res = new Response<>();
        try {
            res.setData(dataSource.add(node.getPath(), node.getValue()));
            res.setSuccess(true);
            res.setErrorCode(200);
        } catch (Exception e) {
            res.setErrorCode(1000);
            res.setSuccess(false);
            res.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    @PostMapping(value = "update")
    @ResponseBody
    public Response<String> update(@RequestBody NodeVo node) throws Exception {
        Response<String> res = new Response<>();
        try {
            res.setData(dataSource.update(node.getPath(), node.getValue()));
            res.setSuccess(true);
            res.setErrorCode(200);
        } catch (Exception e) {
            res.setErrorCode(1000);
            res.setSuccess(false);
            res.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    @PostMapping(value = "delete")
    @ResponseBody
    public Response<String> delete(@RequestBody NodeVo node) {
        Response<String> res = new Response<>();
        try {
            dataSource.remove(node.getPath());
            res.setSuccess(true);
            res.setErrorCode(200);
        } catch (Exception e) {
            res.setErrorCode(1000);
            res.setSuccess(false);
            res.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

}
