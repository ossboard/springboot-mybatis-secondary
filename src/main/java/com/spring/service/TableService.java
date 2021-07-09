package com.spring.service;

import com.spring.mapper.TableMapper;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TableService {

    @Autowired
    private TableMapper tableMapper;

    List<Map> selectDB(Map param) {
        return tableMapper.selectDB(param);
    }

    int insertDB(int param) {
        return tableMapper.insertDB(param);
    }
    int deleteDB(Map param) {
        return tableMapper.deleteDB(param);
    }
    int createDB(Map tablename) {
        return tableMapper.createDB(tablename);
    }
}