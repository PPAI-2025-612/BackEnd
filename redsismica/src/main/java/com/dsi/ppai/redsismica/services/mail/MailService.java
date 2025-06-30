package com.dsi.ppai.redsismica.services.mail;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MailService implements InterfaceMail {


	@Override
	public void enviarmail(List<String> to, String subject, String body) {
		for (String mail : to) {
			 System.out.println("Simulando envío de email:");
		     System.out.println("Para: " + mail);
		     System.out.println("Asunto: " + subject);
		     System.out.println("Cuerpo: " + body);
		}
		
	}

}
