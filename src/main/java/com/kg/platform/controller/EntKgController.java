package com.kg.platform.controller;

import com.kg.platform.service.impl.EntKgServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sun.net.www.http.HttpClient;

import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/entKg")
@CrossOrigin()
public class EntKgController {

    @Autowired
    private EntKgServiceImp entKgServiceImp;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String login(String username, String password) {
        return entKgServiceImp.login(username, password);
    }

    //首页
    @RequestMapping(value = "/main", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String getRelatedNode(long entId) {
        String result = entKgServiceImp.queryEntMainInfo(entId);
        return result;
    }

    //节点内容页
    @RequestMapping(value = "/content", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String getNodeContent(String targetNodelLabel, long nodeId, int degree) {
        String result = entKgServiceImp.queryNodeContent(targetNodelLabel, nodeId,2);
        return result;
    }

    //潜在客户挖掘
    @RequestMapping(value = "/discovery", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String getPotentialCustomers(String entId,String industryId) {
        String result = entKgServiceImp.queryPotentialCustomers(entId,industryId);
        return result;
    }

    //搜索框
    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String getQueryInfo(String content) {
        String result = entKgServiceImp.query(content);
        return result;
    }

    //竞品企业信息
    @RequestMapping(value = "/getCompetingGoods", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String getCompetingGoods(String entId) {
        String result = entKgServiceImp.getCompetingGoods(entId);
        return result;
    }
}
