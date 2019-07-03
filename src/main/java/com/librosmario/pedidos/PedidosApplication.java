package com.librosmario.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
//@EnableSpringDataWebSupport //para queryDSL
public class PedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(PedidosApplication.class, args);
	}

}
