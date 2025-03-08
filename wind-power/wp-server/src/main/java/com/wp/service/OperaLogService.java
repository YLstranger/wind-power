package com.wp.service;

import com.wp.entity.OperaLog;

public interface OperaLogService {


    /**
     * 将日志记录数据进行插入
     * @param operaLog
     */
    void insert(OperaLog operaLog);
}
