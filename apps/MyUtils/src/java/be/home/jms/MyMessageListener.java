package be.home.jms;

import javax.jms.MessageListener;

/**
 * Created by ghyssee on 1/09/2015.
 */
public class MyMessageListener implements MessageListener {

    public void onMessage( javax.jms.Message aMessage)
    {
        try
        {
            // Cast the message as a text message.
            javax.jms.TextMessage textMessage = (javax.jms.TextMessage) aMessage;

            // This handler reads a single String from the
            // message and prints it to the standard output.
            try
            {
                String string = textMessage.getText();
                System.out.println( "[Request] " + string );

                // Check for a ReplyTo Queue
                javax.jms.Queue replyQueue = (javax.jms.Queue) aMessage.getJMSReplyTo();
                if (replyQueue != null)
                {
                    /*
                    // Send the modified message back.
                    javax.jms.TextMessage reply =  session.createTextMessage();
                    if (imode == UPPERCASE)
                        reply.setText("Uppercasing-" + string.toUpperCase());
                    else
                        reply.setText("Lowercasing-" + string.toLowerCase());
                    reply.setJMSCorrelationID(aMessage.getJMSMessageID());
                    replier.send (replyQueue, reply);
                    session.commit();*/
                }
            }
            catch (javax.jms.JMSException jmse)
            {
                jmse.printStackTrace();
            }
        }
        catch (java.lang.RuntimeException rte)
        {
            rte.printStackTrace();
        }
    }

}
