package com.wp.service;

import com.wp.dto.MachineDataDTO;
import com.wp.dto.PageDTO;
import com.wp.vo.MachineDataVO;
import com.wp.vo.PageResult;
import com.wp.vo.PredictDataVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface MachineService {
    /**
     * 添加风机数据
     * @param file
     * @return
     */
    void addMachineData(MultipartFile file) throws IOException;

    /**
     * 分页查询风机数据
     * @param pageDTO
     * @return
     */
    PageResult pageQuery(PageDTO pageDTO);

    /**
     * 删除风机信息
     * @param id
     */
    void delete(Integer id);

    /**
     * 导出风机数据
     * @param machineId
     * @param response
     */
    void exportMachineData(Integer machineId,HttpServletResponse response);

    /**
     * 获取风机数据
     * @param machineDataDTO
     * @return
     */
    PredictDataVO getById(MachineDataDTO machineDataDTO);
}
