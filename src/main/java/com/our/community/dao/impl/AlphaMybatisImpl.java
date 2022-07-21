package com.our.community.dao.impl;

import com.our.community.dao.AlphaDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
//优先调用注解
@Primary
public class AlphaMybatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "Mybatis";
    }
}
