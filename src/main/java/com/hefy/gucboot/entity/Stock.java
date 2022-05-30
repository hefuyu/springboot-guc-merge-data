package com.hefy.gucboot.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2022-05-17
 */
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String product;

    private Integer qty;

    private String name;

    private Integer version;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Stock{" +
        "id=" + id +
        ", product=" + product +
        ", qty=" + qty +
        ", name=" + name +
        ", version=" + version +
        "}";
    }
}
