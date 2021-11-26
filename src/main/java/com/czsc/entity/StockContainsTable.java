package com.czsc.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StockContainsTable implements Serializable {
    private String id;

    private String symbol;

    private Date dt;

    private Integer type;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal volume;

    private Integer fenxingType;

    private Integer fenxingPower;

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

    public Date getDt() {
        return dt;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public Integer getFenxingType() {
        return fenxingType;
    }

    public void setFenxingType(Integer fenxingType) {
        this.fenxingType = fenxingType;
    }

    public Integer getFenxingPower() {
        return fenxingPower;
    }

    public void setFenxingPower(Integer fenxingPower) {
        this.fenxingPower = fenxingPower;
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
        sb.append(", dt=").append(dt);
        sb.append(", type=").append(type);
        sb.append(", high=").append(high);
        sb.append(", low=").append(low);
        sb.append(", volume=").append(volume);
        sb.append(", fenxingType=").append(fenxingType);
        sb.append(", fenxingPower=").append(fenxingPower);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}