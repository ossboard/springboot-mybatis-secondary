package com.spring.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TableMapper {

    List<Map> selectDB(Map param);
    int insertDB(int idx);
    int deleteDB(Map param);
    int createDB(Map tablename);
}