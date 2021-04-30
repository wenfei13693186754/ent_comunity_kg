package com.kg.platform.util;

import com.alibaba.fastjson.JSONObject;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.write.DateTime;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HandleData {
    public static void main(String[] args) {
//        createProductData();

//        createEntData();
//        createProductLineToProductRelaData();

        //生成企业--自然人关系数据
//        createEntToPersonRelas();

        //生成企业与行业关系数据
//        createEntToIndustryRelasData();
        //生成企业与地区关系数据
//        createEntToAreaRelasData();

//        createAreaData();
//        createIndustryData();

        //生成自然人突出贡献数据
        //给八分之一的人生成突出贡献数据，其他人没有相关数据
//        createPersonData();

        //构建企业之间的商务关系，也就是企业购买某个企业的商品
//        createEntToProductData();
//
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            ArrayList<String> productInfo = new ArrayList<>();
//            Files.readAllLines(Paths.get("D:\\library\\workplace\\叮当快药\\项目\\问答与推荐\\es库构建\\product.json"))
//                    .forEach(row -> {
//                        if (!row.contains("index")) {
//                            JSONObject obj = JSONObject.parseObject(row);
//                            String component = obj.getString("drugComponent");
//                            String sellPoint = obj.getString("sellPoint");
//                            String productIntroduce = obj.getString("mainFunction");
//                            String createAt = obj.getString("createAt");
//                            String productName = obj.getString("name");
//                            String id = obj.getString("id");
//                            String productType = obj.getString("productType");
//                            String formatDate = "";
//                            formatDate = sdf.format(new Date(Long.parseLong(createAt)));
//                            String product = id + "," + "Product," + productName + "," + productIntroduce + ",v1.0,true," + sellPoint + "," +
//                                    formatDate + "," + formatDate + ",null,0," + CommonUtils.hanziZhuanHuan(productName) + ",TEST";
//                            productInfo.add(product);
//                        }
//
//                    });
//            ArrayList<String> tmpPids = new ArrayList<>();
//            Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\tmp.csv"))
//                    .forEach(x -> {
//                        String[] strs = x.split(",");
//                        tmpPids.add(strs[0]);
//                    });
//            ArrayList<String> productToPlInfo = new ArrayList<>();
//            Random random = new Random();
//            Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\productToProductLineRelas.csv"))
//                    .forEach(x -> {
//                        if(!x.contains("null")){
//                            productToPlInfo.add(x);
//                        }
//                    });
//            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\tmp1.csv"), productToPlInfo, StandardOpenOption.APPEND);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private static void createEntToProductData() {
        try {
            HashMap<String, String> entInfo = new HashMap<>();
            Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\ent.csv"))
                    .forEach(row -> {
                        String[] strs = row.split(",");
                        entInfo.put(strs[2], strs[0]);
                    });

            Random random = new Random();
            List<String> productInfo = Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\product.csv"));
            productInfo.remove(0);
            StringBuilder sbd = new StringBuilder();
            sbd.append(":START_ID,:TYPE,name,:END_ID\n");
            productInfo.forEach(x -> {
                String[] strs = x.split(",");
                String productId = strs[0];
                String entName = strs[12];

                //随机生成该商品关联的客户企业数量
                int relaCustomerNum = random.nextInt(4) + 1;
                for (int i = 0; i < relaCustomerNum; i++) {
                    //随机生成购买该商品的企业entInfo下标
                    int index = random.nextInt(557) + 1001;
                    if (entInfo.containsKey(entName)) {
                        if (entInfo.get(entName).equals(index + "")) {
                            continue;
                        } else {
                            //建立该商品与购买该商品的企业的关系
                            sbd.append(index + ",BUY,购买," + productId + "\n");
                        }
                    } else {
                        //建立该商品与购买该商品的企业的关系
                        sbd.append(index + ",BUY,购买," + productId + "\n");
                    }
                }
            });
            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\tmp.csv"), sbd.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void createPersonData() {
        try {
            List<String> areaInfo = Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\person.csv"))
                    .stream()
                    .skip(1)
                    .map(x -> {
                        String[] strs = x.split(",");
                        StringBuilder sbd = new StringBuilder();
                        for (String str : strs) {
                            sbd.append(str.replace(",", "，") + ",");
                        }
                        String py = CommonUtils.hanziZhuanHuan(strs[2]);
                        sbd.append(py);
                        return sbd.toString();
                    })
                    .collect(Collectors.toList());

            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\person.csv"), areaInfo, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createIndustryData() {
        try {
            List<String> areaInfo = Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\industry.csv"))
                    .stream()
//                    .skip(1)
                    .map(x -> {
                        String[] strs = x.split(",");
                        String s = azConvertToNum(strs[0]);
                        return s + "," + strs[1] + "," + strs[2] + "," + strs[3];
                    })
                    .collect(Collectors.toList());

            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\industry.csv"), areaInfo, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            List<String> result = Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\entToIndustryRelas.csv"))
//                    .stream()
//                    .skip(1)
//                    .map(x -> {
//                        String[] strs = x.split(",");
//                        String startId = strs[0];
//                        String endId = strs[2];
//                        endId = azConvertToNum(endId);
//                        return startId+",BELONG,所属行业,"+endId;
//                    })
//                    .collect(Collectors.toList());
//            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\tmp.csv"),result,StandardOpenOption.APPEND);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private static String azConvertToNum(String x) {
        if (x.startsWith("A")) {
            x = x.replace("A", "400");
        } else if (x.startsWith("B")) {
            x = x.replace("B", "410");
        } else if (x.startsWith("C")) {
            x = x.replace("C", "420");
        } else if (x.startsWith("D")) {
            x = x.replace("D", "430");
        } else if (x.startsWith("E")) {
            x = x.replace("E", "440");
        } else if (x.startsWith("F")) {
            x = x.replace("F", "450");
        } else if (x.startsWith("G")) {
            x = x.replace("G", "460");
        } else if (x.startsWith("H")) {
            x = x.replace("H", "470");
        } else if (x.startsWith("I")) {
            x = x.replace("I", "480");
        } else if (x.startsWith("J")) {
            x = x.replace("J", "490");
        } else if (x.startsWith("K")) {
            x = x.replace("K", "200");
        } else if (x.startsWith("L")) {
            x = x.replace("L", "210");
        } else if (x.startsWith("M")) {
            x = x.replace("M", "220");
        } else if (x.startsWith("N")) {
            x = x.replace("N", "230");
        } else if (x.startsWith("O")) {
            x = x.replace("O", "240");
        } else if (x.startsWith("P")) {
            x = x.replace("P", "250");
        } else if (x.startsWith("Q")) {
            x = x.replace("Q", "260");
        } else if (x.startsWith("R")) {
            x = x.replace("R", "270");
        } else if (x.startsWith("S")) {
            x = x.replace("S", "280");
        } else if (x.startsWith("T")) {
            x = x.replace("T", "290");
        } else if (x.startsWith("U")) {
            x = x.replace("U", "300");
        } else if (x.startsWith("V")) {
            x = x.replace("V", "310");
        } else if (x.startsWith("W")) {
            x = x.replace("W", "320");
        } else {

        }
        return x;
    }

    private static void createAreaData() {
        try {
            List<String> areaInfo = Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\area.csv"))
                    .stream()
                    .skip(1)
                    .map(x -> {
                        String[] strs = x.split(",");
                        String py = CommonUtils.hanziZhuanHuan(strs[2]);
                        return x + "," + py;
                    })
                    .collect(Collectors.toList());

            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\area.csv"), areaInfo, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createEntToAreaRelasData() {
        Random random = new Random();
        ArrayList<String> entToAreaRelas = new ArrayList<>();
        try {
            List<String> areaIds = Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\area.csv"))
                    .stream()
                    .skip(1)
                    .map(x -> {
                        String[] strs = x.split(",");
                        return strs[0];
                    })
                    .collect(Collectors.toList());
            Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\ent.csv"))
                    .forEach(x -> {
                        String[] strs = x.split(",");
                        String entId = strs[0];
                        //随机生成企业对应员工数量
                        int areaIndex = random.nextInt(367);
                        String areaId = areaIds.get(areaIndex);
                        entToAreaRelas.add(entId + ",BUSINESSAREA,运营区域," + areaId);
                    });
            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\entToAreaRelas.csv"), entToAreaRelas, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createEntToIndustryRelasData() {
        Random random = new Random();
        ArrayList<String> entToIndustryRelas = new ArrayList<>();
        List<String> industryIds = Arrays.asList("C27", "C271", "C2710", "C272", "C2720", "C273", "C2730", "C274", "C2740", "C275", "C2750", "C276", "C2760", "C277", "C2770");
        try {
            Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\ent.csv"))
                    .forEach(x -> {
                        String[] strs = x.split(",");
                        String entId = strs[0];
                        //随机生成企业对应员工数量
                        int industryIndex = random.nextInt(15);
                        String industryId = industryIds.get(industryIndex);
                        entToIndustryRelas.add(entId + ",BELONG," + industryId);
                    });
            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\entToIndustryRelas.csv"), entToIndustryRelas, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createEntToPersonRelas() {
        try {
            Random random = new Random();
            List<String> personIds = Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\person.csv"))
                    .stream()
                    .skip(1)
                    .map(x -> {
                        String[] strs = x.split(",");
                        return strs[0];
                    })
                    .collect(Collectors.toList());
            ArrayList<String> personToEntRelas = new ArrayList<>();
            Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\ent.csv"))
                    .forEach(x -> {
                        String[] strs = x.split(",");
                        String entId = strs[0];
                        //随机生成企业对应员工数量
                        int staffNum = random.nextInt(30);
                        for (int i = 0; i < staffNum; i++) {
                            int index = random.nextInt(personIds.size());
                            String personId = personIds.get(index);
                            personToEntRelas.add(personId + ",STAFF," + entId);
                        }
                    });
            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\personToEntRelas.csv"), personToEntRelas, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createProductLineToProductRelaData() {
        //生成企业产品关系数据
        HashMap<String, String> entInfo = new HashMap<>();
        try {
            Random random = new Random();
            ArrayList<String> productLineNodes = new ArrayList<>();
            ArrayList<String> entProductLineRelas = new ArrayList<>();
            Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\ent.csv"))
                    .forEach(x -> {
                        String[] strs = x.split(",");
                        String entId = strs[0];
                        String entName = strs[2];
                        entInfo.put(entName, entId);
                        //生成产品线节点数据和企业与产品线节点关系数据
//                        createProductLineNodeAndEntToPLData(random, productLineNodes, entProductLineRelas, entId);
                    });

            //将产品线数据和产品线与企业关系数据输出
//            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\productLine.csv"),productLineNodes,StandardOpenOption.APPEND);
//            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\entToproductLineRela.csv"),entProductLineRelas,StandardOpenOption.APPEND);

            HashMap<String, ArrayList<String>> entToProductLine = new HashMap<>();
            Files.readAllLines(Paths.get("E:\\\\其它\\\\研究生项目\\\\data\\\\import\\\\新建文件夹\\\\entToproductLineRela.csv"))
                    .forEach(x -> {
                        String[] strs = x.split(",");
                        if (entToProductLine.containsKey(strs[0])) {
                            ArrayList<String> productLineCodes = entToProductLine.get(strs[0]);
                            productLineCodes.add(strs[1]);
                            entToProductLine.put(strs[0], productLineCodes);
                        } else {
                            ArrayList<String> productLineCodes = new ArrayList<>();
                            productLineCodes.add(strs[1]);
                            entToProductLine.put(strs[0], productLineCodes);
                        }
                    });
            List<String> productToPLRela = Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\product.csv"))
                    .stream()
                    .skip(1)
                    .map(x -> {
                        //生成产品和产品线关系数据
                        String[] strs = x.split(",");
                        String productId = strs[0];
                        String entName = strs[strs.length - 1];
                        String entId = entInfo.get(entName);

                        if (entToProductLine.containsKey(entId)) {
                            ArrayList<String> productLineCodes = entToProductLine.get(entId);
                            int index = random.nextInt(productLineCodes.size());
                            String productLineCode = productLineCodes.get(index);
                            return productLineCode + ",PRODUCT," + productId;
                        } else {
                            return entId + ",PRODUCT," + productId;
                        }
                    })
                    .collect(Collectors.toList());
            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\新建文件夹\\productToProductLineRela.csv"), productToPLRela, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createProductLineNodeAndEntToPLData(Random random, ArrayList<String> productLineNodes, ArrayList<String> entProductLineRelas, String entId) {
        //生成企业产品线节点数据
        int productLineNum = random.nextInt(5);
        String productLineName;
        String productLineCode;
        for (int i = 0; i < productLineNum; i++) {
            productLineName = RandomStringUtils.random(4, new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'K', '1', '2', '3', '4', '5', '6', '7', '8', '9'});
            productLineCode = entId + "" + i;
            productLineNodes.add(productLineCode + ",ProductLine," + productLineName);

            //生成企业--生产线关系数据
            entProductLineRelas.add(entId + "," + productLineCode + ",OWN");
        }
    }

    /**
     * 生成企业节点数据
     */
    private static void createEntData() {
        //生成公司投资历程数据
        //1.融资轮次
        List<String> roundNumContent = Arrays.asList("种子轮", "天使轮", "A轮", "B轮", "C轮", "D轮", "E轮");

        //2.投资额度，根据融资轮次随机生成
//		种子轮：融资大致是10万~100万RMB
//		天使轮：融资大致是100万~1000万RMB
//		A轮：融资大致是1000万~1亿RMB。
//		B轮：融资大致在2亿RMB以上。
//		C轮：融资大致在10亿RMB以上。
//		一般来讲C轮是公司上市前的最后一轮融资，D轮、E轮、F轮融资：C轮的升级版本

        //3.投资方
        List<String> invests = Arrays.asList("青云创投", "高盛", "红杉资本", "鼎晖创投", "枫丹国际", "派杰投资银行", "凯雷投资", "长安私人资本", "格林雷斯", "汉能资本", "启明创投", "智基创投", "美国光速创投", "世盈创业投资有限公司", "贝祥投资集团", "德同资本管理有限公司", "凯旋创投", "天泉投资", "祥峰资本", "凯鹏华盈", "联想投资", "赛富亚洲投资资金", "维思投资", "艾库乐森新能源投资有限公司", "北京京能能源科技投资有限公司", "上海裕安投资有限公司", "银杉投资顾问（北京)有限公司", "科桥投资", "开投基金", "四维资本", "英特尔投资", "软银赛富", "中欧投资", "戈壁投资", "厚朴基金", "弘毅投资", "汇丰直投", "必百瑞", "江苏高科投", "深圳高科投", "IDG", "深圳创投", "上海联创", "海纳亚洲", "凯雷", "成为基金", "联创策源", "纪源资本", "兰馨亚洲", "摩根士丹利", "联想风险投资", "软银", "高盛", "idg", "凯雷", "法国巴黎百富勤融资有限公司", "荷银融资亚洲有限公司", "博资财务顾问有限公司", "英高财务顾问有限公司", "亚洲融资有限公司", "倍利证券（香港）有限公司", "贝尔斯登亚洲有限公司", "加拿大怡东融资有限公司", "中银国际亚洲有限公司", "时富融资有限公司", "里昂证券资本市场有限公司", "京华山一企业融资有限公司", "群益亚洲有限公司", "Credit", "Suisse", "德勤企业财务顾问有限公司", "道亨证券有限公司", "安永企业融资顾问有限公司", "新加坡发展亚洲融资有限公司", "第一上海融资有限公司", "德意志银行", "国泰君安融资有限公司", "高盛(亚洲)有限责任公司", "亨达融资有限公司", "荷兰商业银行", "工商东亚融资有限公司", "日盛嘉富国际有限公司", "金利丰财务顾问有限公司", "金英融资(香港)有限公司", "美国雷曼兄弟亚洲投资有限公司", "汇富融资有限公司", "元富证券(香港)有限公司", "摩根士丹利添惠亚洲有限公司", "洛希尔父子(香港)有限公司", "百德能证券有限公司", "宝来证券(香港)有限公司", "御泰融资(香港)有限公司", "所罗门美邦香港有限公司", "软库金汇融资有限公司", "申银万国融资(香港)有限公司", "新百利有限公司4新鸿基国际有限公司", "金鼎综合证券(香港)有限公司", "大福融资有限公司", "和升财务顾问有限公司", "禹铭投资管理有限公司", "新加坡大华亚洲(香港)有限公司", "冠联证券有限公司", "大华证券(香港)有限公司", "老虎基金", "量子基金", "富兰克林邓普顿", "JP摩根基金", "美林投资管理公司", "荷兰国际集团投资公司", "AIG资产管理", "荷银资产管理公司（美国）", "华夏", "博时", "易方达", "南方", "广发", "嘉实", "上投");

        Random random = new Random();

        try {
            List<String> culture = Files.readAllLines(Paths.get("E:\\其它\\研究生项目\\data\\import\\culture.csv"));
            Sheet sheet = FileUtils.readDataFromExcel("E:\\其它\\研究生项目\\data\\import\\造数据.xls", "企业");
            for (int j = 0; j < sheet.getRows(); j++) {
                String entName = sheet.getCell(0, j).getContents();
                String entIntroduce = sheet.getCell(1, j).getContents().replaceAll(",", "，");
                String setUpDate = "";
                if (sheet.getCell(2, j).getType() == CellType.DATE) {
                    DateCell dc = (DateCell) sheet.getCell(2, j);
                    Date date = dc.getDate();    //获取单元格的date类型
                    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
                    setUpDate = dataFormat.format(date);
                }
                //生成一条结果
                StringBuilder sbd = new StringBuilder();
                int roundNum = random.nextInt(7);
                //生成融资时间
                ArrayList<Date> roundDate = new ArrayList<>();
                while (roundDate.size() < roundNum) {
                    Date date = randomDate("2002-01-01", "2021-04-05");
                    roundDate.add(date);
                }

                roundDate.sort(new Comparator<Date>() {
                    @Override
                    public int compare(Date o1, Date o2) {
                        return o1.compareTo(o2);
                    }
                });

                sbd.append("序号 发布日期 融资轮次 融资金额（万元） 投资方&");
                for (int i = 1; i <= roundNum; i++) {

                    String roundContent = roundNumContent.get(roundNum);
                    int roundMoney = 0;
                    switch (roundContent) {
                        case "种子轮":
                            roundMoney = random.nextInt(91) + 10;
                            break;
                        case "天使轮":
                            roundMoney = random.nextInt(901) + 100;
                            break;
                        case "A轮":
                            roundMoney = random.nextInt(9001) + 1000;
                            break;
                        case "B轮":
                            roundMoney = random.nextInt(80001) + 20000;
                            break;
                        case "C轮":
                            roundMoney = random.nextInt(100000) + 100000;
                            break;
                        case "D轮":
                            roundMoney = random.nextInt(100000) + 100000;
                            break;
                        case "E轮":
                            roundMoney = random.nextInt(100000) + 100000;
                            break;
                        default:
                            roundMoney = random.nextInt(100000) + 100000;
                    }

                    int investObjNum = random.nextInt(4) + 1;
                    HashSet<String> investObj = new HashSet<>();
                    for (int k = 0; k < investObjNum; k++) {
                        int investNum = random.nextInt(118);
                        investObj.add(invests.get(investNum));
                    }

                    String formatDate = new SimpleDateFormat("yyyy-MM-dd").format(roundDate.get(i - 1));
                    sbd.append(i + "\t" + formatDate + "\t" + roundNumContent.get(i) + "\t" + roundMoney + "\t" + String.join("，", investObj) + "&");
                }
                if (sbd.toString().endsWith("&")) {
                    sbd = sbd.deleteCharAt(sbd.length() - 1);
                }

                //给包含汉字的输入，生成首字母
                String reg = ".*[\\u4e00-\\u9fa5].*";
                String pinyingHeader = " ";
                if (entName.matches(reg)) {
                    pinyingHeader = CommonUtils.hanziZhuanHuan(entName);
                }
                int cultureIndex = random.nextInt(culture.size());
                int entId = 1000 + j;
                String result = entId + "," + "Enterprise," + entName + "," + entIntroduce + "," + setUpDate + "," + sbd.toString() + "," + culture.get(cultureIndex) + "," + pinyingHeader + "\n";
                try {
                    Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\20210410\\tmp.csv"), result.getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createProductData() {
        Sheet sheet = FileUtils.readDataFromExcel("E:\\其它\\研究生项目\\data\\import\\P_PRODUCT_DATA1087.xls", "Sheet1");
        //用于输出到文件的二三级病症数据
        ArrayList<String> thirdToSecondInfos = new ArrayList<>();
        ArrayList<String> products = new ArrayList<>();
        for (int i = 0; i < sheet.getRows(); i++) {

            //properties:产品id,产品name,产品简介、版本、是否在维护、维护说明、上线时间、版本发布时间线、每个发布版本的更新内容、在用客户数,拼音首字母
            //productId,productName,productIntroduce,productVersion,maintainOrNot,maintainIntro,releaseTime,versionReleaseTime,
            // versionUpdateContent,usedNum,pinyingHeader
            String productId = sheet.getCell(0, i).getContents();
            String productName = sheet.getCell(1, i).getContents();
            if (productName == null || "".equals(productName)) {
                String name = sheet.getCell(2, i).getContents();
                if (name == null || "".equals(name)) {
                    continue;
                } else {
                    productName = name;
                }
            }
            String productIntroduce = sheet.getCell(13, i).getContents().replaceAll("\n", "&").replaceAll(",", "，");
            String productVersion = sheet.getCell(68, i).getContents().replaceAll(",", "，");
            boolean maintainOrNot = true;
            String maintainIntro = sheet.getCell(19, i).getContents().replaceAll("\n", "&").replaceAll(",", "，");
            Date date = randomDate("2002-01-01", "2021-04-05");
            String releaseTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
            String versionReleaseTime = releaseTime;
            String versionUpdateContent = sheet.getCell(52, i).getContents().replaceAll("\n", "&").replaceAll(",", "，");
            String pinyingHeader = sheet.getCell(6, i).getContents().toLowerCase();
            String enterprise = sheet.getCell(53, i).getContents();
            int usedNum = 0;
            String product = productId + "," + "Product," + productName + "," + productIntroduce + "," + productVersion + "," + maintainOrNot + "," + maintainIntro + "," +
                    releaseTime + "," + versionReleaseTime + "," + versionUpdateContent + "," + usedNum + "," + pinyingHeader + "," + enterprise;
            products.add(product);
        }

        try {
            //数据输出到文件
            Files.write(Paths.get("E:\\其它\\研究生项目\\data\\import\\product.csv"), products, StandardOpenOption.CREATE);
            thirdToSecondInfos.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Date randomDate(String beginDate, String endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);

            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }
}
