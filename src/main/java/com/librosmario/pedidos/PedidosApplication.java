package com.librosmario.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.SpringDataWebSettings;

@SpringBootApplication(exclude = {BatchAutoConfiguration.class})
public class PedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(PedidosApplication.class, args);
	}

	@Bean
	public SpringDataWebSettings springDataWebSettings() {
		return new SpringDataWebSettings(EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO);
	}

}
