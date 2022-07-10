package com.gendml.kutang.Entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.StringTokenizer;

@Data
public class CpuInfoBean {
    // /proc/stat中cpu数据10元组
    private BigDecimal user, nice, system, idle, iowait, irq, softirq, stealstolen, guest, guest_nice;

    public CpuInfoBean(StringTokenizer procStatFirstLine) {
        procStatFirstLine.nextToken();
        this.user = procStatFirstLine.hasMoreTokens() ? new BigDecimal(procStatFirstLine.nextToken()) : BigDecimal.ZERO;
        this.nice = procStatFirstLine.hasMoreTokens() ? new BigDecimal(procStatFirstLine.nextToken()) : BigDecimal.ZERO;
        this.system = procStatFirstLine.hasMoreTokens() ? new BigDecimal(procStatFirstLine.nextToken()) : BigDecimal.ZERO;
        this.idle = procStatFirstLine.hasMoreTokens() ? new BigDecimal(procStatFirstLine.nextToken()) : BigDecimal.ZERO;
        this.iowait = procStatFirstLine.hasMoreTokens() ? new BigDecimal(procStatFirstLine.nextToken()) : BigDecimal.ZERO;
        this.irq = procStatFirstLine.hasMoreTokens() ? new BigDecimal(procStatFirstLine.nextToken()) : BigDecimal.ZERO;
        this.softirq = procStatFirstLine.hasMoreTokens() ? new BigDecimal(procStatFirstLine.nextToken()) : BigDecimal.ZERO;
        this.stealstolen = procStatFirstLine.hasMoreTokens() ? new BigDecimal(procStatFirstLine.nextToken()) : BigDecimal.ZERO;
        this.guest = procStatFirstLine.hasMoreTokens() ? new BigDecimal(procStatFirstLine.nextToken()) : BigDecimal.ZERO;
        this.guest_nice = procStatFirstLine.hasMoreTokens() ? new BigDecimal(procStatFirstLine.nextToken()) : BigDecimal.ZERO;
    }

    public BigDecimal getCpuTotal() {
        return user.add(nice).add(system).add(idle).add(iowait).add(irq).add(softirq).add(stealstolen).add(guest).add(guest_nice);
    }
}