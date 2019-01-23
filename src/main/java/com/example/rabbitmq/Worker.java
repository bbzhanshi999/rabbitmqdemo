package com.example.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 接收请求
 */
public class Worker {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.194.130");
        factory.setUsername("admin");
        factory.setPassword("1234");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();


        channel.queueDeclare(TASK_QUEUE_NAME,true,false,false,null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //每次只给一个worker发送一条数据，直到其回送ack之前，不会发送第二条
        channel.basicQos(1);


        //编写处理信息的回调
        DeliverCallback deliverCallback =  (consumerTag, delivery)->{
            String message = new String(delivery.getBody(),"UTF-8");

            System.out.println("[x] Received '"+message+"'");

            try{
                doWork(message);
            }finally {
                System.out.println(" [x] Done");
                //处理完信息后，向rabbitmq server 发送acknowledgment
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            }

        };

        //声明消费
        channel.basicConsume(TASK_QUEUE_NAME,false,deliverCallback,consumerTag -> {});
    }

    /**
     * 模拟处理信息的时间
     * @param task
     */
    private static void doWork(String task){
        for(char ch: task.toCharArray()){
            if('.' == ch){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
