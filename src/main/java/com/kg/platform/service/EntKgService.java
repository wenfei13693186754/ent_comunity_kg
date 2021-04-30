package com.kg.platform.service;


/**
 * Created by xyf on 2018/12/4.
 */
public interface EntKgService {

    /**
     * 模拟用户登录
     * @param username
     * @param password
     */
    String login(String username, String password);

    /**
     * 首页功能接口
     * @param entId 企业id
     * @return  封装了公司产品、推荐人物、推荐行业的json串
     */
    String queryEntMainInfo(long entId);

    /**
     * 节点内容页接口
     * @param targetNodeLabel 被查询目标节点label
     * @param nodeId 节点id
     * @param degree 查询度数
     * @return  点击首页的相关节点，进入到内容页，用来展示该节点的简介和图谱。
     */
    String queryNodeContent(String targetNodeLabel,long nodeId, int degree);

    /**
     * 查询行业潜在客户，查询逻辑为：
     * 1. 根据企业id查询到该企业签约数量topN的客户所在行业；
     * 2. 查询该行业的相关的但是没有成为当前企业客户的企业并返回。
     * @param entId
     * @param industryId
     * @return
     */
    String queryPotentialCustomers(String entId,String industryId);

    /**
     * 搜索框功能，具体业务需求看接口文档。
     * 处理流程：
     * 1.获取需要搜索的数据；
     * 2.盘点搜索数据中是否有中文；
     *      如果有英文和中文，那么获取该中文的汉语拼音的首字母与英文组成待检索字段；
     *      如果只有中文，那么获取该中文的汉语拼音的首字母组成待检索字段；
     *      如果只有英文，不进行处理。
     * 3.使用源检索字段和新组成的待检索字段去neo4j库中进行检索
     *      从name字段和pinyingHeader字段进行检索。
     *      采用模糊检索，同时不区分大小写的方式，如下：
     *          match(n:Enterprise) where n.name =~'(?i)tI.*' return n
     * @param content
     * @return
     */
    String query(String content);

    /**
     * 获取竞品信息
     * @param entId 被查询企业id
     * @return  返回竞品节点信息组成的json串
     */
    String getCompetingGoods(String entId);
}
