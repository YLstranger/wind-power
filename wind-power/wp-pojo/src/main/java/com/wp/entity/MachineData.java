package com.wp.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineData {
    //机器id
    private int machineId;

    //记录时间
    private LocalDateTime recordTime;

    //风速——10m
    private double speedTen;

    //风向——10m
    private double dircTen;

    //风速——30m
    private double speedThirty;

    //风向——30m
    private double dircThirty;

    //风速——50m
    private double speedFifty;

    //风向——50m
    private double dircFifty;

    //测量高度
    private double measureHeight;

    //测量方向
    private double measureDirc;

    //温度
    private double temperature;

    //气压
    private double pressure;

    //湿度
    private double humidity;

    //功率
    private double power;
}
