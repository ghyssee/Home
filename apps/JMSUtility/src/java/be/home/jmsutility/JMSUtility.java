package be.home.jmsutility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ghyssee on 5/10/2015.
 */
public class JMSUtility extends Component {
    private JTextField queueDestination;
    private JPanel panel1;
    private JTextPane message;
    private JTextField textField1;
    private JTextPane parameterList;
    private JButton sendMessageButton;
    private JFileChooser directoryChooser;

    public JMSUtility() {
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showHelpDialog();
                } catch (Exception ex) {
                    //showErrorPane(ex.getMessage(), ExceptionUtils.getFullStackTrace(ex));
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("JMSUtility");
        frame.setContentPane(new JMSUtility().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void showHelpDialog() {
        String message = "This tool let's you send a JMS messsage to a queue. " +
                "\nSupply the input fields with correct values and click send button." +
                "\n\nThere is a special feature for the 'message properties' and 'parameter list' input fields." +
                "\nThese fields work together, so if you supply the message with a placeholder, namely %s, " +
                "\nthen the tool will attempt to fill the placeholder with values from the parameter list. If you have one %s in you message, " +
                "\nthen you can send several messages with each message being given a new value for %s with the next value in the list. " +
                "\nThe parameter list entries need to be separated by a carriage return/new line feed. " +
                "\n------" +
                "\nExample parameter list for input with one %s:" +
                "\nparam1" +
                "\nparam2" +
                "\nparam3" +
                "\n------" +
                "\nIf you have more than one placeholder, then the values should be separated by a colon (:) in the parameter list. " +
                "\nSo message 'hello %s %s' could be given the parameter list 'mr:duke' and the resulting message would be 'hello mr duke'." +
                "\n------" +
                "\nExample parameter list for input with two %s:" +
                "\nmr:duke" +
                "\nms:daisy" +
                "\nmrs:robinson" +
                "\n------";
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(this, "Help!");
        dialog.setAlwaysOnTop(true);
        System.out.println(queueDestination.getText());
        dialog.setVisible(true);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
