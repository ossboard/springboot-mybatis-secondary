package com.spring.service;

import com.spring.dao.SecondaryTableDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SecondaryTableService {

    @Autowired
    private SecondaryTableDao secondaryTableDao;

    List<Map> selectDB(Map param) {
        return secondaryTableDao.selectDB(param);
    }

    int insertDB(int param) {
        return secondaryTableDao.insertDB(param);
    }
    int deleteDB(Map param) {
        return secondaryTableDao.deleteDB(param);
    }
    int createDB(Map param) {
        return secondaryTableDao.createDB(param);
    }
}