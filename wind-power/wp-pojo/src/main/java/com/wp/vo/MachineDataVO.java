package com.wp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineDataVO {

    //记录时间
    private String recordTime;

    //功率
    private double power;

}
