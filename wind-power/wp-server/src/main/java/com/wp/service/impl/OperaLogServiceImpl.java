package com.wp.service.impl;

import com.wp.entity.OperaLog;
import com.wp.mapper.OperaLogMapper;
import com.wp.service.OperaLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperaLogServiceImpl implements OperaLogService {

    @Autowired
    private OperaLogMapper operaLogMapper;

    /**
     * 将日志记录数据进行插入
     * @param operaLog
     */
    @Override
    public void insert(OperaLog operaLog) {
        operaLogMapper.insert(operaLog);
    }
}
