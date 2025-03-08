package com.wp.mapper;

import com.github.pagehelper.Page;
import com.wp.dto.PageDTO;
import com.wp.entity.Machine;
import com.wp.entity.MachineData;
import com.wp.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MachineMapper  {
    /**
     * 插入数据
     * @param machine
     */
    void insert(Machine machine);

    /**
     * 向wp_data表中插入数据
     * @param machineData
     */
    void insertData(MachineData machineData);

    /**
     * 分页查询风机列表数据
     * @param pageDTO
     * @return
     */
    @Select("select * from machine")
    Page<Machine> pageQuery(PageDTO pageDTO);

    /**
     * 根据id删除machine表数据
     * @param id
     */
    @Delete("delete from machine where id=#{id}")
    void delete(Integer id);

    /**
     * 删除wp_data表中数据
     * @param id
     */
    @Delete("delete from wp_data where machine_id=#{id}")
    void deleteMachineData(Integer id);

    /**
     * 根据风机id查询时间段风机数据
     * @param id
     */
    List<MachineData> getByMachineId(Integer id, LocalDateTime begin,LocalDateTime end);

    /**
     * 根据id查询风机列表
     */
    @Select("select * from machine where id=#{id}")
    Machine getById(Integer id);
}
