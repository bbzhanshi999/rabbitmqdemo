package com.example.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class NewTask {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.194.130");
        factory.setUsername("admin");
        factory.setPassword("1234");

        try (Connection conn = factory.newConnection();
             Channel channel = conn.createChannel()) {
            //声明队列 durable设置为true实现消息持久化
            channel.queueDeclare(TASK_QUEUE_NAME,true,false,false,null);

            String message = String.join(" ", args);

            //发布消息
            channel.basicPublish("",TASK_QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));
            System.out.println(" [x] sent '"+message+"'");
        }
    }
}
