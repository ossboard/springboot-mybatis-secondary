package com.spring.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PrimaryTableDao {

    private final SqlSession sqlSession;

    public PrimaryTableDao(@Qualifier("primarySqlSessionTemplate") SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public List<Map> selectDB(Map param) {
        return this.sqlSession.selectList("com.spring.mapper.TableMapper.selectDB", param);
    }

    public int insertDB(int param) {
        return this.sqlSession.insert("com.spring.mapper.TableMapper.insertDB", param);
    }

    public int deleteDB(Map param) {
        return this.sqlSession.delete("com.spring.mapper.TableMapper.deleteDB", param);
    }

    public int createDB(Map param) {
        return this.sqlSession.update("com.spring.mapper.TableMapper.createDB", param);
    }

}
