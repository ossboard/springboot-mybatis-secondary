package com.spring.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    static ObjectMapper mapper = new ObjectMapper();

    public static <T>T string2Object(String condition,Class<T> valueType) throws JsonProcessingException {
        return mapper.readValue(condition, valueType);
    }

    public static String object2String(Object o) throws JsonProcessingException {
        if (o == null) {
            return "{\"\"}";
        }
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S"));
        return mapper.writeValueAsString(o);
    }

    public static Map json2MapNoException(String s) {
        try {
            return json2Map(s);
        } catch (Exception e) {
            return null;
        }
    }
    public static Map json2Map(String s) throws IOException {
        if (StringUtils.isEmpty(s)) {
            return new HashMap();
        }
        return mapper.readValue(s,HashMap.class);
    }

    public static Object json2Object(String s, Class<?> c) throws IOException {
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        return mapper.readValue(s, c );
    }

    public static Map object2Map(Object o) throws IOException {
        if (o == null) {
            return null;
        }
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.convertValue(o, Map.class);
    }

    public static String printing(Object o) throws IOException {
        if (o == null) {
            return null;
        }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }

    public static String clob2String(Clob clob) throws IOException, SQLException {
        if (clob == null) {
            return null;
        }
        StringBuffer strOut = new StringBuffer();
        String str = "";
        BufferedReader br = new BufferedReader(clob.getCharacterStream());
        while ((str = br.readLine()) != null) {
            strOut.append(str);
        }
        return strOut.toString();
    }

    public static Map andConditionMap( Map<String, Object> param) throws Exception{
        Map<String, Object> condition = new HashMap<>();
        if(param != null) {
            List list = setParam(param);
            if (list.size() > 0) {
                condition.put("$and", list);
            }
        }
        return condition;
    }

    public static Map orConditionMap(Map<String, Object> param) throws Exception{
        Map<String, Object> condition = new HashMap<>();
        if(param != null) {
            List list = setParam(param);
            if (list.size() > 0) {
                condition.put("$or", list);
            }
        }
        return condition;
    }

    /**
     * 파라미터명 네이밍 포멧에 따라 검색조건 변경
     * default '$eq' "name=data"
     * name_from '$ge' "name<=data"
     * name_to '$le' "name>=data"
     * name_keyword '$like' "name like '%data%'"
     * name_name2_keyword '$like' "name like '%data%' or name2 like '%data%'"
    * */
    private static List setParam(Map<String, Object> param) throws Exception{
        List<Map<String, Object>> list = new ArrayList<>();
        for( String key : param.keySet() ){
            if("limit".equals(key) || "offset".equals(key)){
                continue;
            }
            if("$and".equals(key) || "$or".equals(key)){
                list.add(param);
                continue;
            }
            Map eq = new HashMap<String,Object>();
            HashMap dataMap = new HashMap<String,Object>();
            if(!ObjectUtils.isEmpty(param.get(key))) {
                if (key.endsWith("_from")) {
                    dataMap.put(key.replace("_from", ""), param.get(key));
                    eq.put("$ge", dataMap);
                } else if (key.endsWith("_to")) {
                    dataMap.put(key.replace("_to", ""), param.get(key));
                    eq.put("$le", dataMap);
                } else if (key.endsWith("_keyword")) {
                    String keyword = key.replace("_keyword", "");
                    String data = param.get(key).toString();
                    if(keyword.indexOf("_") > 0){
                        String keys[] = keyword.split("_");
                        HashMap dataMap1 = new HashMap<String,Object>();
                        for(String k : keys){
                            dataMap1.put(k+"_keyword", data);
                        }
                        eq = orConditionMap(dataMap1);
                    } else {
                        dataMap.put(keyword, "%" + data + "%");
                        eq.put("$like", dataMap);
                    }
                } else if (key.endsWith("_in")) {
                    //상태값 검색시 진행중 포함일 경우(status_in = 1000,2000...)일 경우 검색조건 변경 (status in (1000,2000,...) or (status>=2000 and status<=2100))
                    if(key.indexOf("status") > -1) {
                        String data = param.get(key).toString();
                        if (data.indexOf("2000") > -1) {
                            HashMap dataMap1 = new HashMap<String,Object>();
                            dataMap1.put(key.replace("_in","_from"), "2000");
                            dataMap1.put(key.replace("_in","_to"), "2100");
                            Map andMap = andConditionMap(dataMap1);
                            andMap.put(key, data.replace(",2000","").replace("2000,","").replace("2000",""));
                            eq = orConditionMap(andMap);
                            list.add(eq);
                            continue;
                        }
                    }
                    dataMap.put(key.replace("_in", ""), param.get(key).toString());
                    eq.put("$in", dataMap);
                } else {
                    //상태값 검색시 진행중(status = 2000)일 경우 검색조건 변경 (status>=2000 and status<=2100)
                    if(key.indexOf("status") > -1){
                        String data = MapUtils.getString(param, key);
                        if ("2000".equals(data)) {
                            dataMap.put(key, "2000");
                            eq.put("$ge", dataMap);
                            list.add(eq);

                            HashMap eq2 = new HashMap<String,Object>();
                            HashMap dataMap2 = new HashMap<String,Object>();
                            dataMap2.put(key, "2100");
                            eq2.put("$le", dataMap2);
                            list.add(eq2);
                            continue;
                        }
                    }
                    dataMap.put(key, param.get(key));
                    eq.put("$eq", dataMap);
                }
                list.add(eq);
            }
        }
        return list;
    }
}
