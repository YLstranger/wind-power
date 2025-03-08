package com.wp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MachineDataDTO {

    //模型名字
    private String modelName;

    //风机ID
    private Integer machineId;

    //开始时间
    private String beginTime;

    //结束时间
    private String endTime;

    //时间间隔
    private Integer timeInterval;

}
