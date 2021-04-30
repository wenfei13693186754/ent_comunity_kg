package com.kg.platform.entities;

public enum NodeEnum {

    Person("Person", "自然人"),
    Enterprise("Enterprise", "企业"),
    Area("Area", "地域"),
    Industry("Industry", "行业"),
    Product("Product", "产品"),
    ProductLine("ProductLine", "产品线"),
    Category("Category", "分类节点");

    private String enName;
    private String cnName;

    NodeEnum(String enName, String cnName) {
        this.enName = enName;
        this.cnName = cnName;
    }

    public String getCnName() {
        return cnName;
    }

    public String getCnNameByEnName(String enName) {
        String cnName;
        switch (enName) {
            case "Person":
                cnName = Person.cnName;
                break;
            case "Enterprise":
                cnName = Enterprise.cnName;
                break;
            case "Area":
                cnName = Area.cnName;
                break;
            case "Industry":
                cnName = Industry.cnName;
                break;
            case "Product":
                cnName = Product.cnName;
                break;
            case "ProductLine":
                cnName = ProductLine.cnName;
                break;
            case "Category":
                cnName = Category.cnName;
                break;
            default:
                cnName = Enterprise.cnName;
        }
        return cnName;
    }
}