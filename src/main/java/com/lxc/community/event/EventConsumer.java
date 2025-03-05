package com.lxc.community.event;
//消费者
import com.alibaba.fastjson.JSONObject;
import com.lxc.community.entity.Event;
import com.lxc.community.entity.Message;
import com.lxc.community.service.MessageService;
import com.lxc.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("消息内容为空!");
            return;
        }

        //把相关的字符串解析恢复为对象  指定恢复的具体类型
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            logger.error("消息格式错误!");
            return;
        }

        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());//取的是通知类型 帖子 点赞 关注
        message.setCreateTime(new Date());

        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        //判断event里有没有数据,有则都取出来,存放到map里
        if (!event.getData().isEmpty()){
            //遍历一个key:value集合 每次一个key:value
            for (Map.Entry<String,Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }

        //把content里的信息转化成json,存到message里
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

}
