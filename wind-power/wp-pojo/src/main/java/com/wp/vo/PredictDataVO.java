package com.wp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class PredictDataVO {

    //原始数据
    private List<MachineDataVO> originalData;

    //预测数据
    private List<MachineDataVO> predictData;
}
