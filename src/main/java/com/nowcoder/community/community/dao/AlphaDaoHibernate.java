package com.nowcoder.community.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AlphaDaoHibernate implements AlphaDao{
    @Override
    public String select() {
        return "Hibernate";
    }
}
