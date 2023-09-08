package com.ruoyi.common.redis.listener;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.system.api.RemoteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    private static final Logger log = LoggerFactory.getLogger(RedisKeyExpirationListener.class);

    @Autowired
    private RemoteUserService remoteUserService;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 针对redis数据失效事件，进行数据处理
     *
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
        String expiredKey = message.toString();
        log.info("onMessage --> redis 过期的key是：{}", expiredKey);
        try {
            // 对过期key进行处理
            System.out.println("================= 处理中 ===================");
            remoteUserService.expirationKey(expiredKey, SecurityConstants.INNER);
            log.info("过期key处理完成：{}", expiredKey);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("处理redis 过期的key异常：{}", expiredKey, e);
        }
    }
}
