package com.gendml.kutang.utils;


import com.gendml.kutang.Entity.R;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author Зөндөө
 * @create 2021-09-08 16:43
 */
public class SendEmailUtil {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("mail");
    private static final String sendFrom = bundle.getString("email.from");
    private static final String username = bundle.getString("username");
    private static final String password = bundle.getString("password");
    private static final String host = bundle.getString("email.host");

    public static R sendEmail(String someone, String subject, String content){
        Properties props = new Properties();
        props.setProperty("mail.host", host);
        props.setProperty("mail.smtp.auth", "true");

        Authenticator authenticator = new Authenticator(){
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username,password);
            }
        };
        Session session = Session.getDefaultInstance(props, authenticator);
        session.setDebug(true);
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(sendFrom));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(someone));
            //message.setRecipients(RecipientType.TO,InternetAddress.parse("测试的接收的邮件多个以逗号隔开"));
            try {
                message.setSubject(subject);
                message.setContent(content,"text/html;charset=UTF-8");
                Transport.send(message);
                return new R(200,"操作成功！","邮件发送成功！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return new R(500,"操作成功！","邮件发送失败！");
    }
/*    public  void  tsetemail(){
        String content ="您的库塘水位已达阈值，请及时处理！";

        //参数分别为接收者邮箱、title、内容body
        sendEmail("2457870242@qq.com", "库塘监测提醒", content);
    }*/
}

