package com.wp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Machine {

    //主键
    private int id;

    //风机编号
    private int serialNumber;

    //风机名
    private String name;

    //风机创建时间
    private LocalDate createTime;
}
