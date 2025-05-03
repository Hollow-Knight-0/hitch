package com.heima.commons.helper;

import com.heima.commons.entity.SessionContext;
import com.heima.commons.template.SessionTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.Map;

/**
 * 对 Redis 的具体读写逻辑
 */
public class RedisSessionHelper extends SessionTemplate {

    //处理accountId -> sessionId 映射
    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    //存储会话详情
    @Autowired
    private RedisTemplate<String, SessionContext> redisSessionTemplate;

    @Override
    public void storeSession(SessionContext sessionContext) {
        redisSessionTemplate.opsForHash().putAll(sessionContext.getSessionID(), sessionContext.toHash());
        stringRedisTemplate.opsForValue().set(sessionContext.getAccountTokenKey(), sessionContext.getSessionID());
    }


    @Override
    public void delayExpirTime(String sessionId, Date expirDate) {
        redisSessionTemplate.opsForHash().put(sessionId, "lastVisitTime", String.valueOf(expirDate.getTime()));
        String accountId = (String) redisSessionTemplate.opsForHash().get(sessionId, "accountID");
        redisSessionTemplate.expireAt(sessionId, expirDate);
        redisSessionTemplate.expireAt(SessionContext.getAccountTokenKey(accountId), expirDate);
    }

    @Override
    public SessionContext getSession(String sessionId) {
        HashOperations<String, String, String> hashOperations = redisSessionTemplate.opsForHash();
        Map<String, String> map = hashOperations.entries(sessionId);
        if (null != map && !map.isEmpty()) {
            SessionContext sessionContext = new SessionContext(map);
            if (null != sessionContext && sessionId.equals(sessionContext.getSessionID())) {
                return sessionContext;
            }
        }
        return null;
    }

    @Override
    public SessionContext getSessionByAccount(String accountId) {
        String sessionId = stringRedisTemplate.opsForValue().get(SessionContext.getAccountTokenKey(accountId));
        if (StringUtils.isEmpty(sessionId)) {
            return null;
        }
        return getSession(sessionId);
    }

    @Override
    public void deleteSession(String sessionId) {
        SessionContext context = getSession(sessionId);
        if (null != context) {
            redisSessionTemplate.delete(context.getAccountTokenKey());
        }
        redisSessionTemplate.delete(sessionId);
    }
}
