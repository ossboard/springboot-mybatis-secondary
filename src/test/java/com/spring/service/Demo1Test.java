package com.spring.service;

import com.spring.dao.PrimaryTableDao;
import com.spring.dao.SecondaryTableDao;
import com.spring.mapper.TableMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

class Demo1Test extends _ServiceTest {

    @Autowired
    private TableService tableService;

    @Autowired
    private TableMapper tableMapper;

    @Autowired
    private PrimaryTableService primaryTableService;

    @Autowired
    private PrimaryTableDao primaryTableDao;

    @Autowired
    private SecondaryTableService secondaryTableService;

    @Autowired
    private SecondaryTableDao secondaryTableDao;


    @Autowired
    @Qualifier("primarySqlSessionTemplate")
    private SqlSessionTemplate sessionTemplate1;

    @Autowired
    @Qualifier("secondarySqlSessionTemplate")
    private SqlSessionTemplate sessionTemplate2;

    @Autowired
    @Qualifier("primarySqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory1;

    @Autowired
    @Qualifier("secondarySqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory2;

    @Test
    void test1() {
        System.out.println("01 = " + primaryTableService.createDB(null));
        System.out.println("02 = " + secondaryTableService.createDB(null));

        System.out.println("03 = " + primaryTableService.deleteDB(null));
        System.out.println("04 = " + primaryTableService.insertDB(1));
        System.out.println("05 = " + primaryTableService.insertDB(2));

        System.out.println("06 = " + secondaryTableService.deleteDB(null));
        System.out.println("07 = " + secondaryTableService.insertDB(3));
        System.out.println("08 = " + secondaryTableService.insertDB(4));
    }

    @Test
    void test2() {
        Map<String, Object> param = new HashMap<>();

        System.out.println("01 = " + tableService.selectDB(param));
        System.out.println("02 = " + tableMapper.selectDB(param));

        System.out.println("03 = " + primaryTableService.selectDB(param));
        System.out.println("04 = " + primaryTableDao.selectDB(param));

        System.out.println("05 = " + secondaryTableService.selectDB(param));
        System.out.println("06 = " + secondaryTableDao.selectDB(param));

        Object total1 = sessionTemplate1.selectList("com.spring.mapper.TableMapper.selectDB", param);
        System.out.println("07 = " + total1);
        sessionTemplate1.flushStatements(); //배치모드에서 flush
        sessionTemplate1.clearCache(); //세션레벨 캐시지우기(SqlSession은 update,commit,rollback,close 할때 지워지는데 강제로할때)
        // close,commit,rollback 수행불가!!

        Object total2 = sessionTemplate2.selectList("com.spring.mapper.TableMapper.selectDB", param);
        System.out.println("08 = " + total2);

        SqlSession sqlSession1 = sqlSessionFactory1.openSession(ExecutorType.BATCH);
        try {
            Object total3 = sqlSession1.selectList("com.spring.mapper.TableMapper.selectDB", param);
            System.out.println("09 = " + total3);
        } finally {
            sqlSession1.rollback();
            sqlSession1.flushStatements();
            sqlSession1.close();
            sqlSession1.clearCache();
        }

        SqlSession sqlSession2 = sqlSessionFactory2.openSession(ExecutorType.BATCH);
        try {
            Object total4 = sqlSession2.selectList("com.spring.mapper.TableMapper.selectDB", param);
            System.out.println("10 = " + total4);
        } finally {
            sqlSession1.rollback();
            sqlSession2.flushStatements();
            sqlSession2.close();
            sqlSession2.clearCache();
        }
    }

}