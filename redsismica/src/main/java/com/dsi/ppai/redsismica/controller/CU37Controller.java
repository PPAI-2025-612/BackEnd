package com.dsi.ppai.redsismica.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsi.ppai.redsismica.Constantes;

@RestController
public class CU37Controller {

	@PostMapping(Constantes.URL_PATH_CERRAR_ORDEN_INSPECCION)
	public void cerrarOrdenInspeccion() {
		
	}
	
}
