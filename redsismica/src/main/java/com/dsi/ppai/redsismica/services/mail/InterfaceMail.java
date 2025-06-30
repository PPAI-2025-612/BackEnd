package com.dsi.ppai.redsismica.services.mail;

import java.util.List;

public interface InterfaceMail {
    void enviarmail(List<String> to, String subject, String body);
}
