package com.hefy.gucboot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2022-05-17
 */
@TableName("stock_log")
public class StockLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private LocalDateTime createtime;

    private String product;

    private Integer qty;

    private Integer version;

    private Boolean flag;

    private Integer userid;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "StockLog{" +
        "id=" + id +
        ", createtime=" + createtime +
        ", product=" + product +
        ", qty=" + qty +
        ", version=" + version +
        ", flag=" + flag +
        "}";
    }
}
