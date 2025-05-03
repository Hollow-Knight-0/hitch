package com.heima.notice.service.impl;


import com.heima.commons.constant.HtichConstants;
import com.heima.modules.po.NoticePO;
import com.heima.modules.vo.NoticeVO;
import com.heima.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void addNotice(NoticePO noticePO) {
        noticePO.setCreatedTime(new Date());
        //mongoDB 保存消息
        mongoTemplate.save(noticePO, HtichConstants.NOTICE_COLLECTION);
    }

    /**
     * 根据用户ID 获取消息
     *
     * @param receiverIds
     * @return
     */
    @Override
    public List<NoticePO> getNoticeByAccountIds(List<String> receiverIds) {
        // 构造查询条件：查询 receiverId 在 receiverIds 列表中的，并且 read=false（未读）
        Criteria criteria = Criteria.where("receiverId").in(receiverIds);
        criteria.andOperator(Criteria.where("read").is(false));
        Query query = new Query(criteria);

        // 构造更新条件：标记为已读
        Update update = Update.update("read", true);

        // 查询未读消息
        List<NoticePO> noticePOList = mongoTemplate.find(query, NoticePO.class, HtichConstants.NOTICE_COLLECTION);

        // 批量更新，将 read 设置为 true
        if (!noticePOList.isEmpty()) {
            mongoTemplate.updateMulti(query, update, NoticePO.class, HtichConstants.NOTICE_COLLECTION);
        }

        return noticePOList;
    }


    @Override
    public List<NoticePO> queryList(NoticeVO noticeVO) {
        Criteria criteria = new Criteria();
        List<Criteria> orCriterias = new ArrayList<>();
        //查询聊天记录（双向）
        orCriterias.add(Criteria.where("receiverId").in(noticeVO.getReceiverId())
                .andOperator(Criteria.where("senderId").in(noticeVO.getSenderId())));
        orCriterias.add(Criteria.where("senderId").in(noticeVO.getReceiverId())
                .andOperator(Criteria.where("receiverId").in(noticeVO.getSenderId())));
        criteria.orOperator(orCriterias.toArray(new Criteria[0]));
        Query query = new Query(criteria);
        query.limit(20);
        //从新到旧获取20条消息
        query.with(Sort.by(Sort.Order.desc("createdTime")));
        List<NoticePO> noticePOList = mongoTemplate.find(query, NoticePO.class, HtichConstants.NOTICE_COLLECTION);
        //再从旧到新返回
        Collections.reverse(noticePOList);
        return noticePOList;
    }

}
