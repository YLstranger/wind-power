package com.wp.controller;

import com.wp.annotation.MyLog;
import com.wp.constant.MessageConstant;
import com.wp.dto.MachineDataDTO;
import com.wp.dto.PageDTO;
import com.wp.result.Result;
import com.wp.service.MachineService;
import com.wp.vo.MachineDataVO;
import com.wp.vo.PageResult;
import com.wp.vo.PredictDataVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/machine")
@Api(tags = "风机模块")
@Slf4j
public class MachineController {

    @Autowired
    private MachineService machineService;

    /**
     * 添加风机数据
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加风机数据")
    @MyLog(title = "风机模块", content = "添加风机数据操作")
    public Result addMachineData(MultipartFile file) throws IOException {
        log.info("文件名字为：{}",file.getOriginalFilename());
        //返回风机列表
        machineService.addMachineData(file);

        //封装VO对象
        return Result.success();
    }

    /**
     * 分页查询获取风机列表
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页获取风机列表")
    @MyLog(title = "风机模块", content = "分页查询获取风机列表操作")
    public Result<PageResult> pageQuery(PageDTO pageDTO){
        log.info("分页查询风机列表：{}", pageDTO);
        PageResult pageResult = machineService.pageQuery(pageDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除风机信息
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除风机信息")
    @MyLog(title = "风机模块", content = "删除风机信息操作")
    public Result delete(@PathVariable Integer id){
        machineService.delete(id);
        return Result.success();
    }

    /**
     * 导出风机数据报表
     * @param response
     */
    @GetMapping("/export")
    @ApiOperation("导出风机数据报表")
    @MyLog(title = "风机模块", content = "导出风机数据操作")
    public void export(HttpServletResponse response, HttpServletRequest request){
        Integer machineId = Integer.parseInt(request.getHeader("machineId"));
        log.info("风机id为：{}",machineId);
        machineService.exportMachineData(machineId,response);
    }


    /**
     * 获取风机数据
     * @param machineDataDTO
     * @return
     */
    @GetMapping("/getMachineData")
    @ApiOperation("根据风机id获取风机数据")
    @MyLog(title = "风机模块", content = "获取风机数据操作")
    // TODO 根据前端进行修改
    public Result getById(MachineDataDTO machineDataDTO){
        log.info("获取风机数据相关参数为：{}",machineDataDTO.toString());
        PredictDataVO predictDataVO=machineService.getById(machineDataDTO);
        return Result.success(predictDataVO);
    }
}
