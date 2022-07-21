package com.our.community.dao.impl;

import com.our.community.dao.AlphaDao;
import org.springframework.stereotype.Repository;

//可通过该注解后的括号的名称强制返回该bean
@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
