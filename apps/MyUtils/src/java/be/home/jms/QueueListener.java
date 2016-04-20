/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.home.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;

public class QueueListener implements MessageListener {

    private Connection connection;
    private MessageProducer producer;
    private Session session;
    private int count;
    private long start;
    private Queue queue;
    private Queue control;
    private String url = "tcp://localhost:61616";

    public static void main(String []args) throws JMSException {
        QueueListener l = new QueueListener();
        l.run2();
    }

    public void run2() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = session.createQueue("exampleQueue");
        control = session.createQueue("controlQueue");

        MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(this);

        connection.start();

        producer = session.createProducer(control);
        System.out.println("Waiting for messages...");
    }



    public void run() throws JMSException {

        String user = "admin"; //env("ACTIVEMQ_USER", "admin");
        String password = "password"; //env("ACTIVEMQ_PASSWORD", "password");
        String host = "localhost"; //env("ACTIVEMQ_HOST", "localhost");
        int port = 61616; // Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
        String destination = "exampleQueue"; //arg(args, 0, "event");
        javax.jms.MessageProducer replier = null;

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);

        Connection connection = factory.createConnection(user, password);
        Session receiveSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = new ActiveMQQueue(destination);
        javax.jms.Queue receiveQueue = receiveSession.createQueue(destination);
        javax.jms.Queue controlQueue = receiveSession.createQueue("controlQueue");
        javax.jms.MessageConsumer qReceiver = receiveSession.createConsumer(receiveQueue);
        qReceiver.setMessageListener(this);
        connection.start();
        MessageProducer producer = receiveSession.createProducer(controlQueue);
        System.out.println("Waiting for messages...");
        connection.close();
    }

    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if( rc== null )
            return defaultValue;
        return rc;
    }

    private static String arg(String []args, int index, String defaultValue) {
        if( index < args.length )
            return args[index];
        else
            return defaultValue;
    }


    private static boolean checkText(Message m, String s) {
        try {
            return m instanceof TextMessage && ((TextMessage)m).getText().equals(s);
        } catch (JMSException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }

    public void onMessage(Message message) {
        if (checkText(message, "SHUTDOWN")) {

            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

        } else if (checkText(message, "REPORT")) {
            // send a report:
            try {
                long time = System.currentTimeMillis() - start;
                String msg = "Received " + count + " in " + time + "ms";
                producer.send(session.createTextMessage(msg));
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            count = 0;

        } else {

            if (count == 0) {
                start = System.currentTimeMillis();
            }
            if (message instanceof TextMessage) {
                javax.jms.TextMessage textMessage = (javax.jms.TextMessage) message;
                try {
                    String string = ((TextMessage) message).getText();
                    System.out.println("[Received] " + string);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println(message.gett)

            if (++count % 1000 == 0) {
                System.out.println("Received " + count + " messages.");
            }
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

}