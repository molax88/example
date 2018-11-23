package com.hpb.email.service.impl;

import com.hpb.email.config.EmailConfig;
import com.hpb.email.service.EmailService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Service
public class EmailServiceImpl implements EmailService {
 
	@Autowired
	private EmailConfig emailConfig;
	//注入MailSender
	@Autowired
	private JavaMailSender mailSender;
	@Override
	public void sendSimpleMail(String sendTo, String titel, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(emailConfig.getEmailFrom());
		message.setTo(sendTo);
		message.setSubject(titel);
		message.setText(content);
		mailSender.send(message);
	}
	//发送邮件的模板引擎
    @Autowired
    private FreeMarkerConfigurer configurer;

    @Override
    public void sendMessageMail(Object params, String title,String emailTo, String templateName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailConfig.getEmailFrom());
			helper.setTo(InternetAddress.parse(emailTo));//发送给谁
//            helper.setBcc(InternetAddress.parse("tink.tian@gxn.io"));
            helper.setSubject("【" + title + "】");//邮件标题

            Map<String, Object> model = new HashMap<>();
            model.put("params", params);
            try {
                Template template = configurer.getConfiguration().getTemplate(templateName);
                try {
                    String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

                    helper.setText(text, true);
                    mailSender.send(mimeMessage);
                    System.out.println(title + "邮件已发送："+emailTo);
                } catch (TemplateException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendHpbMessageMail(Map<String, String> params, String title,String emailTo, String templateName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailConfig.getEmailFrom());
            helper.setTo(InternetAddress.parse(emailTo));//发送给谁
//            helper.setBcc(InternetAddress.parse("molax.song@gxn.io"));
            helper.setSubject("【" + title + "】");//邮件标题

            Map<String, Object> model = new HashMap<>();
            model.put("params", params);
            try {
                Template template = configurer.getConfiguration().getTemplate(templateName);
                try {
                    String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

                    helper.setText(text, true);
                    mailSender.send(mimeMessage);
//                    saveEmailToSentMailFolder(mimeMessage);
                    System.out.println(title + "邮件已发送："+emailTo);
                } catch (TemplateException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户的发件箱文件夹
     *
     * @param message
     *            信息
     * @param store
     *            存储
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    private Folder getSentMailFolder(Message message, Store store)
            throws IOException, MessagingException {
        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "get");
        props.setProperty("mail.imap.host", "smtp.exmail.qq.com");
        props.setProperty("mail.imap.port", "143");

        /** QQ邮箱需要建立ssl连接 */
        props.setProperty("mail.imap.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imap.starttls.enable", "true");
        props.setProperty("mail.imap.socketFactory.port", "465");

        // 创建Session实例对象
        Session session = Session.getInstance(props);
        URLName urln = new URLName("get", "smtp.exmail.qq.com", 143, null,
                "molax.song@gxn.io", "Xin2187");
        // 创建IMAP协议的Store对象
        store = session.getStore(urln);
        store.connect();

        // 获得发件箱
        Folder folder = store.getFolder("Sent Messages");
        // 以读写模式打开发件箱
        folder.open(Folder.READ_WRITE);

        return folder;
    }
    /**
     * 保存邮件到发件箱
     *
     * @param message
     *            邮件信息
     */
    private void saveEmailToSentMailFolder(Message message) {

        Store store = null;
        Folder sentFolder = null;
        try {
            sentFolder = getSentMailFolder(message, store);
            message.setFlag(Flags.Flag.SEEN, true); // 设置已读标志
            sentFolder.appendMessages(new Message[] { message });

            System.out.println("已保存到发件箱...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // 判断发件文件夹是否打开如果打开则将其关闭
            if (sentFolder != null && sentFolder.isOpen()) {
                try {
                    sentFolder.close(true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            // 判断邮箱存储是否打开如果打开则将其关闭
            if (store != null && store.isConnected()) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}