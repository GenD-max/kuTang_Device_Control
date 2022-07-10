package com.gendml.kutang.utils;

import com.gendml.kutang.Entity.CpuInfoBean;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.StringTokenizer;

/**
 * @author Зөндөө
 * @create 2021-09-02 12:58
 */
@Slf4j
public class RaspiUtil {
    private static final String CPU_FILE = "/proc/stat";
    private static final String LOAD_COMMAND = "uptime";

    /**
     * 获得Linux cpu使用率 pcpu =100* (total-idle)/total
     * total = total2-total1
     * idle = idle2 -idle1
     * total1 = user1+nice1+system1+idle1+iowait1+irq1+softirq1+stealstolen1+guest1+guest_nice1
     */
    public static BigDecimal getCpuInfo() {
        try {
            File file = new File(CPU_FILE);
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringTokenizer procStatFirstLine = new StringTokenizer(br.readLine());
            CpuInfoBean cpuInfoBean1 = new CpuInfoBean(procStatFirstLine);
            BigDecimal total1 = cpuInfoBean1.getCpuTotal();
            Thread.sleep(1000);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            procStatFirstLine = new StringTokenizer(br.readLine());
            CpuInfoBean cpuInfoBean2 = new CpuInfoBean(procStatFirstLine);
            BigDecimal total2 = cpuInfoBean2.getCpuTotal();
            BigDecimal total = total2.subtract(total1);
            BigDecimal idle = cpuInfoBean2.getIdle().subtract(cpuInfoBean1.getIdle());
            BigDecimal pcpu = new BigDecimal(100).multiply(total.subtract(idle)).divide(total, 0, BigDecimal.ROUND_HALF_UP);
            br.close();
            return pcpu;
        } catch (Exception e) {
            log.info(e.toString());
            return new BigDecimal(0);
        }

    }

    // 获取内存使用率
    public static BigDecimal getMemory() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        BigDecimal total = new BigDecimal(osmxb.getTotalPhysicalMemorySize());
        BigDecimal free = new BigDecimal(osmxb.getFreePhysicalMemorySize());
        BigDecimal pmem = new BigDecimal(100).multiply(total.subtract(free)).divide(total, 0, BigDecimal.ROUND_HALF_UP);
        return pmem;
    }

    /**
     * 获取系统负载:top的15分钟平均值/(逻辑cpu核数*0.7)
     */
    public static BigDecimal getLoad() {
        try {
            Runtime r = Runtime.getRuntime();
            BigDecimal cpuPerformance = new BigDecimal(0.7).multiply(new BigDecimal(r.availableProcessors()));
            Process pro = r.exec(LOAD_COMMAND);
            BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String topLoad = br.readLine();
            BigDecimal load = new BigDecimal(topLoad.substring(topLoad.lastIndexOf(" ") + 1));
            BigDecimal pload = new BigDecimal(100).multiply(load).divide(cpuPerformance, 0, BigDecimal.ROUND_HALF_UP);
            br.close();
            pro.destroy();
            return pload;
        } catch (Exception e) {
            log.info(e.toString());
            return new BigDecimal(0);
        }
    }
}
