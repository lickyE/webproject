package com.nowcoder.community.community.dao;

import org.springframework.stereotype.Repository;

@Repository("mybits")
public class AlphaDaoMybits implements AlphaDao{
    @Override
    public String select() {
        return "mybits";
    }
}
