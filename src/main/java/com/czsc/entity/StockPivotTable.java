package com.czsc.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StockPivotTable implements Serializable {
    private String id;

    private String symbol;

    private String type;

    private Date startTime;

    private BigDecimal startPrice;

    private Date endTime;

    private BigDecimal endPrice;

    private Integer endFlag;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(BigDecimal endPrice) {
        this.endPrice = endPrice;
    }

    public Integer getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(Integer endFlag) {
        this.endFlag = endFlag;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", symbol=").append(symbol);
        sb.append(", type=").append(type);
        sb.append(", startTime=").append(startTime);
        sb.append(", startPrice=").append(startPrice);
        sb.append(", endTime=").append(endTime);
        sb.append(", endPrice=").append(endPrice);
        sb.append(", endFlag=").append(endFlag);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}