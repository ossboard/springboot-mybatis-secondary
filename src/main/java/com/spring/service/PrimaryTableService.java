package com.spring.service;

import com.spring.dao.PrimaryTableDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PrimaryTableService {

    @Autowired
    private PrimaryTableDao primaryTableDao;

    List<Map> selectDB(Map param) {
        return primaryTableDao.selectDB(param);
    }

    int insertDB(int param) {
        return primaryTableDao.insertDB(param);
    }
    int deleteDB(Map param) {
        return primaryTableDao.deleteDB(param);
    }
    int createDB(Map param) {
        return primaryTableDao.createDB(param);
    }
}