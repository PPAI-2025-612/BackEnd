package com.dsi.ppai.redsismica.services.monitorccrs;

import org.springframework.stereotype.Service;

@Service
public class MonitorCCRSService implements InterfaceCCRS {


	@Override
	public void publicarEnMonitor(String mensaje) {
		System.out.println("Publicacion Monitor: " + mensaje);
		
	}

}
