package com.hpb.email.service;

import java.util.Map;

public interface EmailService {

	/**
	 * 发送简单邮件
	 * 
	 * @param sendTo
	 *            收件人地址
	 * @param titel
	 *            邮件标题
	 * @param content
	 *            邮件内容
	 */
	void sendSimpleMail(String sendTo, String titel, String content);

	/**
	 * @param params 发送邮件的主题对象 object
	 * @param title 邮件标题
	 * @param emailTo 发送给谁
	 * @param templateName 模板名称
	 */

	void sendMessageMail(Object params, String title, String emailTo, String templateName);

	void sendHpbMessageMail(Map<String, String> params, String title, String sendTo, String templateName);
}
