package com.mymongotest.springmongtest3.service;


import com.mymongotest.springmongtest3.DTO.SearchDB;
import com.mymongotest.springmongtest3.document.LunchMenu;
import com.mymongotest.springmongtest3.document.Memo;
import com.mymongotest.springmongtest3.document.User2;
import com.mymongotest.springmongtest3.document.Users;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LunchMenuService {

    private final MongoTemplate mongoTemplate;

    // Convert Date to String
    public String dateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public void lunchMenuInsert(LunchMenu lunchMenu) {
        Date date = new Date();
        String converDate = dateToString(date);
        // 기존에 , 화면에서 음식 메뉴, 작성자 문자열은 이미 받아 왔고,
        // 날짜 , 시간 만 추가
        lunchMenu.setDateField(converDate);
        // 실제 몽고디비에 반영함.
        mongoTemplate.insert(lunchMenu);
    }


    //전체 검색
    public List<LunchMenu> lunchMenuFindAll() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "id"));

        List<LunchMenu> lunchMenuList=mongoTemplate.find(query,LunchMenu.class);
        return lunchMenuList;

    }

    //하나 찾기
    public LunchMenu lunchMenuFindOne(Long id) {
        LunchMenu lunchMenu = mongoTemplate.findById(id, LunchMenu.class);
        return lunchMenu;
    }

    //하나 수정하기.
    public void lunchMenuUpdate(LunchMenu lunchMenu) {
        Query query = new Query();
        Update update = new Update();

        // where절 조건
        query.addCriteria(Criteria.where("_id").is(lunchMenu.getId()));
        update.set("lunchMenu",lunchMenu.getLunchMenu());
        update.set("lunchWriter", lunchMenu.getLunchWriter());


        mongoTemplate.updateMulti(query, update, "lunchmenu");

    }

    // 삭제
    public void lunchMenuDeleteDb(String key, String value) {
        Criteria criteria = new Criteria(key);
        criteria.is(value);

        Query query = new Query(criteria);
        mongoTemplate.remove(query, "lunchmenu");
    }


}

