package com.wp.mapper;


import com.wp.entity.OperaLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperaLogMapper {

    /**
     * 将日志记录数据进行插入
     * @param operaLog
     */
    void insert(OperaLog operaLog);
}
