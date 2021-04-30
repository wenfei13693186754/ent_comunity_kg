package com.kg.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kg.platform.config.Neo4jConfig;
import com.kg.platform.entities.*;
import com.kg.platform.service.EntKgService;
import com.kg.platform.util.CommonUtils;
import com.kg.platform.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.internal.shaded.io.netty.util.internal.StringUtil;
import org.neo4j.driver.internal.value.ListValue;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Created by xyf on 2017/6/27.
 */
@Service("entKgServiceImp")
public class EntKgServiceImp implements EntKgService {

    private static final Logger LOG = LoggerFactory.getLogger(EntKgServiceImp.class);
    @Autowired
    private Neo4jConfig neo4jConfig;

    String currentToken = null;

    Map<String, Long> users;

    @Autowired
    ThreadPoolTaskExecutor neo4jThreadPool;

    @Value("${server.url}")
    private String serverUrl;

    @Value("${server.port}")
    private String port;

    @Value("${query-neo4j-max-wait-time.sync:1200}")
    private Long syncMaxWaitTime;

    @Value("${query-neo4j-max-wait-time.async:1200}")
    private Long asyncMaxWaitTime;

    /**
     * 初始化neo4jOperations对象，同时调用apoc.warmup.run来热缓存数据，提高查询效率。
     */
    @PostConstruct
    public void init() {
        //对neo4j中数据进行热缓存，提高检索响应速度
        executeQuery("call apoc.warmup.run(true)", new HashMap<>(), true);
        LOG.info("neo4j中数据热缓存完毕");
        users = new HashMap<String, Long>() {{
            put("d8c7731a5afd18a2ee6347c7305dcd9b", 1019L);
        }};
    }

    @Override
    public String login(String username, String password) {
        JSONObject result = new JSONObject();
        currentToken = MD5Util.getMd5(username + "&" + password);
        if (users.containsKey(currentToken)) {//登录成功
            Long currentEntId = users.get(currentToken);
            result.put("success", true);
            result.put("token", currentToken);
//            result.put("content", serverUrl + ":" + port + "/entKg/main/?entId=" + currentEntId);
            result.put("content", currentEntId);
        } else {//登录失败
            result.put("success", false);
            result.put("content", "用户或密码错误，请重新输入！");
        }
        return result.toJSONString();
    }

    @Override
    public String queryEntMainInfo(long entId) {
        JSONObject result = new JSONObject();
        String unionCypher = "match(ent:Enterprise {id:" + entId + "})-[:OWN|:OWNPRODUCT|:PRODUCT*..3]-(product:Product) return product.id as nodeId, product.name as nodeName,labels(product) as label " +
                "union all " +
                "match(ent:Enterprise {id:" + entId + "})-[*..3]-(person:Person) return person.id as nodeId,person.name as nodeName,labels(person) as label " +
                "union all " +
                "match(ent:Enterprise {id:" + entId + "})-[:OWN|:OWNPRODUCT|:BUSINESSCONTACT|:PRODUCT|:BUY*..4]-(customerEnt:Enterprise)-[r:BELONG]-(industry:Industry) return industry.id as nodeId,industry.name as nodeName,labels(industry) as label " +
                "union all " +
                "match(n1:Enterprise{id:" + entId + "})-[r1:BELONG]-(n2:Industry)-[r2:BELONG]-(n3:Enterprise) return n3.id as nodeId,n3.name as nodeName,labels(n3) as label " +
                "union all " +
                "match(n4:Enterprise{id:" + entId + "})-[r3:BUSINESSAREA|:AREA|:UPPERAREA*..3]-(n5:Area)-[r4:BUSINESSAREA|:AREA|:UPPERAREA*..2]-(n6:Enterprise) return n6.id as nodeId,n6.name as nodeName,labels(n6) as label";

        List<Record> records = executeQuery(unionCypher, new HashMap<>(), true);
        //对推荐产品、推荐人物、推荐行业查询结果进行封装
        JSONArray productArr = new JSONArray();
        JSONArray personArr = new JSONArray();
        JSONArray inductryArr = new JSONArray();
        JSONArray enterpriseArr = new JSONArray();
        HashSet<Integer> ids = new HashSet<>();
        if (records.size() > 0) {
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                int nodeId = record.get("nodeId").asInt();
                if (ids.contains(nodeId)) {
                    continue;
                }
                ids.add(nodeId);
                String nodeName = record.get("nodeName").asString();
                String label = record.get("label").asList().get(0).toString();
                JSONObject obj = new JSONObject();
                obj.put("id", nodeId);
                obj.put("name", nodeName);
                String url = serverUrl + ":" + port + "/entKg/content?targetNodelLabel=" + label + "&nodeId=" + nodeId + "&degree=2";
                obj.put("url", url);
                if (NodeEnum.Person.name().equals(label)) {
                    personArr.add(obj);
                } else if (NodeEnum.Product.name().equals(label)) {
                    productArr.add(obj);
                } else if (NodeEnum.Industry.name().equals(label)) {
                    inductryArr.add(obj);
                } else if (NodeEnum.Enterprise.name().equals(label)) {
                    enterpriseArr.add(obj);
                } else {

                }
            }
        }

        //限制返回数据量
        if (personArr.size() > 6) {
            personArr = JSONArray.parseArray(JSON.toJSONString(personArr.subList(0, 6)));
        }
        if (productArr.size() > 6) {
            productArr = JSONArray.parseArray(JSON.toJSONString(productArr.subList(0, 6)));
        }
        if (inductryArr.size() > 6) {
            inductryArr = JSONArray.parseArray(JSON.toJSONString(inductryArr.subList(0, 6)));
        }
        if (enterpriseArr.size() > 6) {
            enterpriseArr = JSONArray.parseArray(JSON.toJSONString(enterpriseArr.subList(0, 6)));
        }
        result.put("person", personArr);
        result.put("product", productArr);
        result.put("industry", inductryArr);
        result.put("enterprise", enterpriseArr);
        String entKgInfo = queryEntKgInfo("Enterprise", entId, 4);
        result.put("entKg", entKgInfo);
        return result.toJSONString();
    }


    public String queryNodeContent(String targetNodeLabel, long nodeId, int degree) {
        JSONObject result = new JSONObject();
        //查询目标节点的图谱信息
        String nodeKg = queryEntKgInfo(targetNodeLabel, nodeId, degree);
        JSONObject obj = JSON.parseObject(nodeKg);
        JSONArray nodeArr = JSON.parseArray(obj.get("nodes").toString());
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < nodeArr.size(); i++) {
            BaseNode baseNode = JSONObject.parseObject(nodeArr.get(i).toString(), BaseNode.class);
            if (baseNode.getJxId().equals(nodeId + "")) {
                Map<String, Object> properties = baseNode.getProperties();
                String nodeType = baseNode.getNodeType();
                //节点简介
                //1.企业节点    公司名称、公司创立时间、公司简介、公司历程、企业文化
                if (NodeEnum.Enterprise.name().equals(nodeType)) {
                    LinkedHashMap<String, String> entProperties = PropertiesMapping.entProperties;
                    for (Map.Entry<String, String> entry : entProperties.entrySet()) {
                        String key = entry.getKey();
                        if (properties.containsKey(key)) {
                            String cnKey = entry.getValue();
                            if ("history".equals(key)) {
                                String history = properties.get(key).toString();
                                history = history.replaceAll("&", "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                                content.append(cnKey + "&nbsp;:&nbsp;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + history + "<br/>");
                            } else {
                                content.append(cnKey + "&nbsp;:&nbsp;" + properties.get(key) + "<br/>");
                            }
                        }
                    }
                } else if (NodeEnum.Person.name().equals(nodeType)) {
                    //2.人员节点
                    LinkedHashMap<String, String> personProperties = PropertiesMapping.personProperties;
                    for (Map.Entry<String, String> entry : personProperties.entrySet()) {
                        String key = entry.getKey();
                        String cnKey = entry.getValue();
                        if (properties.containsKey(key)) {
                            String personInfo = properties.get(key).toString();
                            if ("personIntroduce".equals(key)) {
                                StringBuilder sbd = new StringBuilder();
                                String[] infos = personInfo.split("，");
                                for (String info : infos) {
                                    if (info.contains("姓名：") || info.contains("入职时间：")) {
                                        continue;
                                    } else {
                                        sbd.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + info + "<br/>");
                                    }
                                }
                                content.append(cnKey + ":<br/>" + sbd.toString());
                            } else {
                                content.append(cnKey + "&nbsp;:&nbsp;" + personInfo + "<br/>");
                            }
                        }
                    }
                } else if (NodeEnum.Industry.name().equals(nodeType)) {
                    //3.产品线节点
                    LinkedHashMap<String, String> industryProperties = PropertiesMapping.industryProperties;
                    for (Map.Entry<String, String> entry : industryProperties.entrySet()) {
                        String key = entry.getKey();
                        if (properties.containsKey(key)) {
                            String cnKey = entry.getValue();
                            content.append(cnKey + "&nbsp;:&nbsp;" + properties.get(key) + "<br/>");
                        }
                    }
                } else if (NodeEnum.Product.name().equals(nodeType)) {
                    //4.产品节点
                    LinkedHashMap<String, String> productProperties = PropertiesMapping.productProperties;
                    for (Map.Entry<String, String> entry : productProperties.entrySet()) {
                        String key = entry.getKey();
                        if (properties.containsKey(key)) {
                            String cnKey = entry.getValue();
                            if ("maintainOrNot".equals(key)) {
                                String value = properties.get(key).toString();
                                if ("true".equals(value)) {
                                    content.append(cnKey + "&nbsp;:&nbsp;是<br/>");
                                } else {
                                    content.append(cnKey + "&nbsp;:&nbsp;否<br/>");
                                }
                            } else {
                                content.append(cnKey + "&nbsp;:&nbsp;" + properties.get(key) + "<br/>");
                            }
                        }
                    }
                } else if (NodeEnum.Category.name().equals(nodeType)) {
                    //5.分类节点
                    LinkedHashMap<String, String> categoryProperties = PropertiesMapping.categoryProperties;
                    for (Map.Entry<String, String> entry : categoryProperties.entrySet()) {
                        String key = entry.getKey();
                        if (properties.containsKey(key)) {
                            String cnKey = entry.getValue();
                            content.append(cnKey + "&nbsp;:&nbsp;" + properties.get(key) + "<br/>");
                        }
                    }
                } else if (NodeEnum.ProductLine.name().equals(nodeType)) {
                    //5.分类节点
                    LinkedHashMap<String, String> productLineProperties = PropertiesMapping.productLineProperties;
                    for (Map.Entry<String, String> entry : productLineProperties.entrySet()) {
                        String key = entry.getKey();
                        if (properties.containsKey(key)) {
                            String cnKey = entry.getValue();
                            content.append(cnKey + "&nbsp;:&nbsp;" + properties.get(key) + "<br/>");
                        }
                    }
                } else if (NodeEnum.Area.name().equals(nodeType)) {
                    //5.分类节点
                    LinkedHashMap<String, String> areaProperties = PropertiesMapping.areaProperties;
                    for (Map.Entry<String, String> entry : areaProperties.entrySet()) {
                        String key = entry.getKey();
                        if (properties.containsKey(key)) {
                            String cnKey = entry.getValue();
                            content.append(cnKey + "&nbsp;:&nbsp;" + properties.get(key) + "<br/>");
                        }
                    }
                }
                break;
            }
        }
        result.put("success", true);
        result.put("nodeKg", nodeKg);
        result.put("content", content);
        return result.toJSONString();
    }

    @Override
    public String queryPotentialCustomers(String entId, String industryId) {
        //1. 根据企业id查询到该企业签约客户所在行业所关联的企业（包括企业的签约客户和未签约客户）；
        String allRelatedEntCypher = " match(n:Industry{id:" + industryId + "})-[r2:BELONG]-(relatedEnt:Enterprise) return relatedEnt.id as entId,relatedEnt.name as entName limit 5";

        List<Record> allRelatedEntRecords = executeQuery(allRelatedEntCypher, new HashMap<>(), false);
        HashMap<String, Enterprise> relatedEnts = new HashMap<>();
        if (allRelatedEntRecords.size() > 0) {
            for (int i = 0; i < allRelatedEntRecords.size(); i++) {
                Record record = allRelatedEntRecords.get(i);
                String relatedEntId = record.get("entId").asLong() + "";
                if (relatedEnts.containsKey(relatedEntId)) {
                    Enterprise preEnterprise = relatedEnts.get(relatedEntId);
                    int num = preEnterprise.getNum() + 1;
                    preEnterprise.setNum(num);
                    relatedEnts.put(relatedEntId, preEnterprise);
                } else {
                    Enterprise enterprise = new Enterprise();
                    enterprise.setEntId(relatedEntId + "");
                    enterprise.setEntName(record.get("entName").asString());
                    enterprise.setNum(1);
                    relatedEnts.put(relatedEntId, enterprise);
                }
            }
        }
        //2. 查询该企业已签约客户
        String signedEntCypher = "match(ent:Enterprise {id:" + entId + "})-[:OWN|:BUY|:PRODUCT*3]-(customerEnt:Enterprise) return distinct(customerEnt.id) as entId,customerEnt.name as entName";
        List<Record> signedEntRecords = executeQuery(signedEntCypher, new HashMap<>(), true);
        ArrayList<Enterprise> signedEnts = new ArrayList<>();
        if (signedEntRecords.size() > 0) {
            for (int i = 0; i < signedEntRecords.size(); i++) {
                Record record = signedEntRecords.get(i);
                Enterprise enterprise = new Enterprise();
                long signedEntId = record.get("entId").asLong();
                enterprise.setEntId(signedEntId + "");
                enterprise.setEntName(record.get("entName").asString());
                signedEnts.add(enterprise);
            }
        }
        //3.两个集合取差集，得到目标企业的未签约的潜在客户
        for (Enterprise signedEnt : signedEnts) {
            String signedEntId = signedEnt.getEntId();
            if (relatedEnts.containsKey(signedEntId)) {
                relatedEnts.remove(signedEntId);
            }
        }
        //去除relatedEents中可能存在的目标企业本身
        if (relatedEnts.containsKey(entId)) relatedEnts.remove(entId);

        //4.对结果进行排序
        LinkedHashMap<String, Enterprise> sortedResult = relatedEnts.entrySet().stream().sorted(Map.Entry.comparingByValue(new Comparator<Enterprise>() {
            @Override
            public int compare(Enterprise o1, Enterprise o2) {
                return o1.getNum() - o2.getNum();
            }
        }))
                .limit(40)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("customer", JSON.toJSONString(sortedResult.values()));
        return result.toJSONString();
    }

    @Override
    public String query(String content) {

        content = content.toLowerCase();
        JSONObject result = new JSONObject();
        ArrayList<SearchNode> searchNodes = new ArrayList<>();
        String processStr = content;
        if (StringUtils.isNotEmpty(content)) {
            if (CommonUtils.hanziCunZai(content)) {
                //如果存在汉字，那么获取汉字首字母，生成待检索字段
                processStr = CommonUtils.hanziZhuanHuan(content);
            }
            //进行检索
            String cypher = "match(n) where (n:Enterprise or n:Product or n:Area) and n.name =~'(?i)" + content + ".*' return n.id as id,n.name as name,n.pinyingHeader as pinyingHeader,labels(n) as label,2 as source " +
                    "union all " +
                    "match(n) where (n:Enterprise or n:Product or n:Area) and n.pinyingHeader=~'(?i)" + processStr + ".*' return n.id as id,n.name as name,n.pinyingHeader as pinyingHeader,labels(n) as label,1 as source";

            List<Record> records = executeQuery(cypher, new HashMap<>(), true);
            ArrayList<Integer> nodeIds = new ArrayList<>();
            if (records.size() == 0) {
                result.put("success", false);
                result.put("content", "未找到相关信息，换个词试试吧~");
            } else {
                for (int i = 0; i < records.size(); i++) {
                    Record record = records.get(i);
                    int id = record.get("id").asInt();
                    if (nodeIds.contains(id)) {
                        continue;
                    } else {
                        nodeIds.add(id);
                        SearchNode searchNode = new SearchNode();
                        String name = record.get("name").asString();
                        int source = record.get("source").asInt();
                        String pinyingHeader = record.get("pinyingHeader").asString();
                        String label = record.get("label").asList().get(0).toString();
                        double score = 0;
                        String hightLightContent = "";
                        if (source == 1) {//根据拼音首字母搜索到的结果
                            //获取高亮显示的index
                            int startIndex = pinyingHeader.indexOf(processStr);
                            int endIndex = startIndex + content.length();
                            hightLightContent = name.substring(startIndex, endIndex);
                            score = 1 / CommonUtils.levenshtein(2f, 1.5f, content, pinyingHeader);
                        } else if (source == 2) {//根据名称搜索到的结果
                            int startIndex = name.toLowerCase().indexOf(content);
                            int endIndex = startIndex + content.length();
                            hightLightContent = name.substring(startIndex, endIndex);
                            score = (1 / CommonUtils.levenshtein(2f, 1.5f, content, name)) * 5;
                        }

                        JSONObject urlInfo = new JSONObject();
                        urlInfo.put("label", label);
                        urlInfo.put("nodeId", id);
                        urlInfo.put("degree", 2);
                        searchNode.setUrlInfo(urlInfo.toJSONString());
                        searchNode.setNodeId(id);
                        searchNode.setName(name);
                        searchNode.setLabel(label);
                        searchNode.setScore(score);
                        searchNode.setHighLightContent(hightLightContent);
                        searchNodes.add(searchNode);
                    }
                }

                //对结果按照score进行排序
                Collections.sort(searchNodes, new Comparator<SearchNode>() {
                    @Override
                    public int compare(SearchNode o1, SearchNode o2) {
                        if (o1.getScore() - o2.getScore() > 0) {
                            return -1;
                        } else if (o1.getScore() - o2.getScore() == 0) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });

                result.put("content", searchNodes);
                result.put("success", true);
            }
        } else {
            result.put("success", false);
            result.put("content", "未找到相关信息，换个词试试吧~");
        }
        return result.toJSONString();
    }

    @Override
    public String getCompetingGoods(String entId) {
        String relaIndustryEntCypher = "match(n1:Enterprise{id:" + entId + "})-[r1:BELONG]-(n2:Industry)-[r2:BELONG]-(n3:Enterprise) return n3";
        String relaAreaEntCypher = "match(n4:Enterprise{id:" + entId + "})-[r3:BUSINESSAREA|:UPPERAREA*..2]-(n5:Area)-[r4:BUSINESSAREA|:UPPERAREA*..2]-(n6:Enterprise) return n6 as n3";
        String allCypher = relaIndustryEntCypher + " union all " + relaAreaEntCypher;

        ArrayList nodeList = new ArrayList<BaseNode>();
        ArrayList<Long> tmpNodeIds = new ArrayList<>();
        List<Record> records = executeQuery(allCypher, new HashMap<>(), false);
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            //先处理节点信息
            org.neo4j.driver.Value value = record.get("n3");
            Node node = value.asNode();
            long id = node.id();
            if (!tmpNodeIds.contains(id)) {
                tmpNodeIds.add(id);
                BaseNode baseNode = packageBaseNodeInfo(node);
                nodeList.add(baseNode);
            }
        }

        JSONObject result = new JSONObject();
        result.put("nodes", nodeList);
        return result.toJSONString();
    }

    public String queryEntKgInfo(String targetNodeLabel, long nodeId, int degree) {
        String cypher;
        if (degree > 1) {
            if(NodeEnum.Person.name().equals(targetNodeLabel)){
                cypher = "match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[*..2]-(relaNode) return nodes(path) as nodes,relationships(path) as relas limit 50 ";
            }else if(NodeEnum.ProductLine.name().equals(targetNodeLabel)){
                cypher = "match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:OWN]-()-[]-() return nodes(path) as nodes,relationships(path) as relas " +
                        "union all match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:PRODUCT]-() return nodes(path) as nodes,relationships(path) as relas limit 50 ";
            }else if(NodeEnum.Product.name().equals(targetNodeLabel)){
                cypher = "match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:BUY]-()-[:BUSINESSCONTACT]-() return nodes(path) as nodes,relationships(path) as relas " +
                        "union all match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:OWNPRODUCT|:PRODUCT|:OWN]-()-[*1..2]-() return nodes(path) as nodes,relationships(path) as relas limit 50 ";
            }else if(NodeEnum.Enterprise.name().equals(targetNodeLabel)){
                cypher = "match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:BUSINESSCONTACT]-()-[:BUY]-() return nodes(path) as nodes,relationships(path) as relas " +
                        "union all match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:OWNPRODUCT]-()-[*1..2]-() return nodes(path) as nodes,relationships(path) as relas " +
                        "union all match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:BUSINESSAREA]-()-[*1..2]-() return nodes(path) as nodes,relationships(path) as relas  " +
                        "union all match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:STAFFPERSON]-()-[:STAFF]-() return nodes(path) as nodes,relationships(path) as relas limit 50 ";
            }else if(NodeEnum.Area.name().equals(targetNodeLabel)){
                cypher = "match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:AREA]-()-[:BUSINESSAREA]-() return nodes(path) as nodes,relationships(path) as relas " +
                        "union all match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:UPPERAREA*1..2]-() return nodes(path) as nodes,relationships(path) as relas limit 50 ";
            }else if(NodeEnum.Industry.name().equals(targetNodeLabel)){
                cypher = "match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:BELONG]-() return nodes(path) as nodes,relationships(path) as relas " +
                        "union all match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[:UPPERLEVEL*1..2]-() return nodes(path) as nodes,relationships(path) as relas limit 50";
            }else{
                cypher = "match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[*.." + degree + "]-(relaNode) where (relaNode:Person or relaNode:Product or relaNode:ProductLine or relaNode:Area or relaNode:Enterprise or relaNode:Industry)" +
                        "return nodes(path) as nodes,relationships(path) as relas limit 50 ";
            }

        } else {
            cypher = "match path = (ent:" + targetNodeLabel + " {id:" + nodeId + "})-[*.." + degree + "]-(relaNode) return nodes(path) as nodes,relationships(path) as relas limit 50";
        }
        HashMap<String, Object> param = new HashMap<>();
        List<Record> records = executeQuery(cypher, param, true);
        LOG.info("企业关系探查查询完毕，开始封装结果");

        ArrayList nodeList = new ArrayList<BaseNode>();
        ArrayList<BaseRela> relaList = new ArrayList<>();
        HashSet<String> labels = new HashSet<>();
        ArrayList<Long> tmpNodeIds = new ArrayList<>();
        ArrayList<Long> tmpRelaIds = new ArrayList<>();
        if (records.size() == 0) {
            cypher = "match(sourceNode:Enterprise {id:'$entId'}) return sourceNode";
            try {
                records = executeQuery(cypher, param, true);
                Record record = records.get(0);
                Node node = record.get("sourceNode").asNode();
                BaseNode baseNode = packageBaseNodeInfo(node);
                nodeList.add(baseNode);
                labels.add(NodeEnum.Enterprise.getCnName());
            } catch (Exception e) {
                LOG.error("查询结果异常！！");
            }
            LOG.info("企业关联节点为空，返回被查询节点！");
        } else {
            //将结果封装为Arraylist<AssociatedNode>对象
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                //先处理节点信息
                ListValue nodeListValue = (ListValue) record.get("nodes");
                for (int j = 0; j < nodeListValue.size(); j++) {
                    Node node = nodeListValue.get(j).asNode();
                    long id = node.id();
                    String label = node.labels().iterator().next();
                    labels.add(NodeEnum.Enterprise.getCnNameByEnName(label));
                    if (!tmpNodeIds.contains(id)) {
                        tmpNodeIds.add(id);
                        BaseNode baseNode = packageBaseNodeInfo(node);
                        nodeList.add(baseNode);
                    }
                }

                //处理关系信息，并将关系添加到对应节点上
                ListValue relaListValue = (ListValue) record.get("relas");
                for (int k = 0; k < relaListValue.size(); k++) {
                    Relationship relationship = relaListValue.get(k).asRelationship();
                    long relaId = relationship.id();
                    if (!tmpRelaIds.contains(relaId)) {
                        tmpRelaIds.add(relaId);
                        BaseRela baseRela = new BaseRela();
                        baseRela.setSource(relationship.startNodeId() + "");
                        baseRela.setTarget(relationship.endNodeId() + "");
                        baseRela.setRelaType(relationship.type());
                        String name = relationship.get("name").asString();
                        baseRela.setRelaName(name);
                        if (relationship.asMap().size() > 0) {
                            baseRela.setProperties(relationship.asMap());
                        }
                        relaList.add(baseRela);
                    }
                }
            }
        }

        JSONObject result = new JSONObject();
        result.put("nodes", nodeList);
        result.put("relas", relaList);
        result.put("nodeLabels",labels);
        LOG.info("企业关系探查关联节点封装完毕！");
        return JSON.toJSONString(result);
    }

    private BaseNode packageBaseNodeInfo(Node node) {
        BaseNode baseNode = new BaseNode();
        String nodeType = node.labels().iterator().next();
        baseNode.setNodeType(nodeType);
        if (nodeType.equals(NodeEnum.Industry.name())) {
            baseNode.setCategory("行业");
        } else if (nodeType.equals(NodeEnum.Person.name())) {
            baseNode.setCategory("自然人");
        } else if (nodeType.equals(NodeEnum.Product.name())) {
            baseNode.setCategory("产品");
        } else if (nodeType.equals(NodeEnum.Enterprise.name())) {
            baseNode.setCategory("企业");
        } else if (nodeType.equals(NodeEnum.Area.name())) {
            baseNode.setCategory("地域");
        } else if (nodeType.equals(NodeEnum.ProductLine.name())) {
            baseNode.setCategory("产品线");
        } else if (nodeType.equals(NodeEnum.Category.name())) {
            baseNode.setCategory("分类节点");
        }
        baseNode.setId(node.id() + "");
        baseNode.setJxId(node.get("id").asLong() + "");
        baseNode.setName(node.get("name").asString());
        baseNode.setProperties(node.asMap());

        return baseNode;
    }

    private List<Record> executeQuery(String cypher, Map<String, Object> params, boolean async) {
        if (StringUtil.isNullOrEmpty(cypher)) {
            return null;
        }
        Session session = neo4jConfig.getDriver().session();
        Future<List<Record>> future = neo4jThreadPool.submit(() -> {
            org.neo4j.driver.Result result = session.run(cypher, params);
            List<Record> recordList = new LinkedList<>();
            while (result.hasNext()) {
                recordList.add(result.next());
            }
            return recordList;
        });
        long waitTime = async ? asyncMaxWaitTime : syncMaxWaitTime;
        try {
            List<Record> records = future.get();
            return future.get(waitTime, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            LOG.warn("查询异常，async:{}, cypher:{}， params:{}, msg:{}", async, cypher, params, e.getMessage());
            future.cancel(true);
            throw new RuntimeException();
        } catch (TimeoutException e) {
            LOG.warn("查询超时，async:{}, cypher:{}， params:{}", async, cypher, params);
            session.reset();
            future.cancel(true);
            throw new RuntimeException();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
    }
}
