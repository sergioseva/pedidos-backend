package com.librosmario.pedidos.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Test;

/**
 * /actuator/info es lo unico que dice que version corre en cada ambiente y es publico a proposito
 * para poder consultarlo desde afuera. Estuvo devolviendo {} sin que nadie lo notara: las
 * propiedades info.* estaban definidas, pero desde Spring Boot 2.6 el contribuidor que las publica
 * viene apagado por defecto. Un endpoint que se vacia en silencio no rompe ningun build.
 *
 * La comprobacion es sobre el archivo y no levantando el contexto porque
 * src/test/resources/application.properties tapa al de main -- ocupan el mismo lugar en el
 * classpath -- asi que ningun test con contexto llega a ver la configuracion que se despliega.
 */
public class ActuatorInfoTest {

	private Properties configuracionDesplegada() throws IOException {
		Properties props = new Properties();
		try (var in = Files.newInputStream(Path.of("src/main/resources/application.properties"))) {
			props.load(in);
		}
		return props;
	}

	@Test
	void elInfoDeActuatorPublicaNombreYVersion() throws IOException {
		Properties props = configuracionDesplegada();

		assertThat(props.getProperty("info.app.name")).isNotBlank();
		assertThat(props.getProperty("info.app.version"))
				.as("la version sale de INFO_APP_VERSION, que define el Dockerfile")
				.isEqualTo("${INFO_APP_VERSION:dev}");
	}

	@Test
	void lasPropiedadesInfoSePublican() throws IOException {
		assertThat(configuracionDesplegada().getProperty("management.info.env.enabled"))
				.as("sin esto /actuator/info devuelve {} aunque info.app.* este definido")
				.isEqualTo("true");
	}

	@Test
	void elEndpointDeInfoEstaExpuesto() throws IOException {
		assertThat(configuracionDesplegada().getProperty("management.endpoints.web.exposure.include"))
				.isNotBlank();
	}
}
