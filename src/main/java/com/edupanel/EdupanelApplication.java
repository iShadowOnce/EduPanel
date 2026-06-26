package com.edupanel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EdupanelApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdupanelApplication.class, args);
	}
	//acuerdate crear en arranque el perfil "firebase"
	//si quieres usar el arranque a memoria usar cualquiera distinto de firebase
	// ya que esta con "!firebase"

}
