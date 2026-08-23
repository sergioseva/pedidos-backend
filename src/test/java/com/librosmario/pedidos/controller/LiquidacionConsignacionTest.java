package com.librosmario.pedidos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.librosmario.pedidos.security.JwtTokenProvider;

/**
 * El comercio 1 tiene 5 'El Principito' a 1000 (dos entregas de 3 y 2) y comision del 20%.
 * El comercio 2 tiene 4 'Rayuela' a 3000 y no tiene comision cargada.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class LiquidacionConsignacionTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	private String token;

	@BeforeEach
	void setUp() {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken("test", "12345678"));
		token = tokenProvider.generateToken(auth);
	}

	private String linea(int vendidos, int devueltos) {
		return "{\"comercioId\":1,\"registrarPago\":false,\"lineas\":[{"
				+ "\"isbn\":\"978-1234567890\",\"nombreLibro\":\"El Principito\","
				+ "\"autor\":\"Saint-Exupery\",\"editorial\":\"Salamandra\",\"precio\":1000.0,"
				+ "\"cantidadVendida\":" + vendidos + ",\"cantidadDevuelta\":" + devueltos + "}]}";
	}

	private org.springframework.test.web.servlet.ResultActions liquidar(String body) throws Exception {
		return mockMvc.perform(post("/remitos/consignacion/liquidar")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body));
	}

	@Test
	void liquidarVendidosEmiteRemitoDeVentaConComisionCongelada() throws Exception {
		liquidar(linea(3, 0))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.remitoVentaId").isNumber())
				.andExpect(jsonPath("$.remitoRetiroId").doesNotExist())
				.andExpect(jsonPath("$.totalTapa").value(3000.0))
				.andExpect(jsonPath("$.comision").value(20.0))
				.andExpect(jsonPath("$.netoAPagar").value(2400.0));
	}

	@Test
	void liquidarDevueltosEmiteSoloRemitoDeRetiro() throws Exception {
		liquidar(linea(0, 2))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.remitoRetiroId").isNumber())
				.andExpect(jsonPath("$.remitoVentaId").doesNotExist())
				.andExpect(jsonPath("$.netoAPagar").value(0.0));
	}

	@Test
	void liquidarMixtoEmiteLosDosRemitos() throws Exception {
		liquidar(linea(3, 2))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.remitoRetiroId").isNumber())
				.andExpect(jsonPath("$.remitoVentaId").isNumber())
				.andExpect(jsonPath("$.netoAPagar").value(2400.0));
	}

	/** Sin comision cargada el comercio paga el precio de tapa entero. */
	@Test
	void comercioSinComisionPagaTapaCompleta() throws Exception {
		liquidar("{\"comercioId\":2,\"registrarPago\":false,\"lineas\":[{"
				+ "\"isbn\":\"978-5555555555\",\"nombreLibro\":\"Rayuela\",\"precio\":3000.0,"
				+ "\"cantidadVendida\":2,\"cantidadDevuelta\":0}]}")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.totalTapa").value(6000.0))
				.andExpect(jsonPath("$.netoAPagar").value(6000.0));
	}

	@Test
	void registrarPagoEmiteElRecibo() throws Exception {
		liquidar("{\"comercioId\":1,\"registrarPago\":true,\"medioPago\":\"Efectivo\",\"lineas\":[{"
				+ "\"isbn\":\"978-1234567890\",\"nombreLibro\":\"El Principito\",\"precio\":1000.0,"
				+ "\"cantidadVendida\":5,\"cantidadDevuelta\":0}]}")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.reciboId").isNumber())
				.andExpect(jsonPath("$.netoAPagar").value(4000.0));
	}

	@Test
	void sinRegistrarPagoNoHayRecibo() throws Exception {
		liquidar(linea(3, 0))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.reciboId").doesNotExist());
	}

	/**
	 * Cada remito lleva SOLO su lado de la liquidacion. Si el de retiro arrastrara los vendidos,
	 * el comercio firmaria la devolucion de libros que en realidad vendio.
	 */
	@Test
	void cadaRemitoLlevaSoloSusItems() throws Exception {
		// 3 vendidos de 'El Principito' y 2 devueltos de 'Rayuela' (comercio 1 tiene los dos).
		String respuesta = liquidar("{\"comercioId\":1,\"registrarPago\":false,\"lineas\":["
				+ "{\"isbn\":\"978-1234567890\",\"nombreLibro\":\"El Principito\",\"precio\":1000.0,"
				+ "\"cantidadVendida\":3,\"cantidadDevuelta\":0},"
				+ "{\"isbn\":\"978-7777777777\",\"nombreLibro\":\"Martin Fierro\",\"precio\":2000.0,"
				+ "\"cantidadVendida\":0,\"cantidadDevuelta\":2}]}")
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		String retiroId = respuesta.replaceAll(".*\"remitoRetiroId\":(\\d+).*", "$1");
		String ventaId = respuesta.replaceAll(".*\"remitoVentaId\":(\\d+).*", "$1");

		mockMvc.perform(get("/remitos/" + retiroId)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].ri_nombre_libro").value("Martin Fierro"))
				.andExpect(jsonPath("$.items[0].ri_cantidad").value(2));

		mockMvc.perform(get("/remitos/" + ventaId)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].ri_nombre_libro").value("El Principito"))
				.andExpect(jsonPath("$.items[0].ri_cantidad").value(3));
	}

	/** Un titulo con vendidos Y devueltos aparece en los dos remitos, con su cantidad propia. */
	@Test
	void unTituloPartidoVaALosDosRemitosConSuCantidad() throws Exception {
		String respuesta = liquidar(linea(3, 2))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		String retiroId = respuesta.replaceAll(".*\"remitoRetiroId\":(\\d+).*", "$1");
		String ventaId = respuesta.replaceAll(".*\"remitoVentaId\":(\\d+).*", "$1");

		mockMvc.perform(get("/remitos/" + retiroId).header("Authorization", "Bearer " + token))
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].ri_cantidad").value(2));
		mockMvc.perform(get("/remitos/" + ventaId).header("Authorization", "Bearer " + token))
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].ri_cantidad").value(3));
	}

	// --- El saldo es lo que no se puede romper ---

	@Test
	void noSePuedeLiquidarMasDeLoQueTieneElComercio() throws Exception {
		liquidar(linea(6, 0))
				.andExpect(status().isBadRequest());
	}

	/** Vendidos y devueltos se suman: 3 + 3 sobre un saldo de 5 no entra. */
	@Test
	void vendidosYDevueltosSeSumanContraElSaldo() throws Exception {
		liquidar(linea(3, 3))
				.andExpect(status().isBadRequest());
	}

	/** Dos filas del mismo titulo tambien se suman entre si. */
	@Test
	void lineasRepetidasDelMismoTituloSeAgregan() throws Exception {
		liquidar("{\"comercioId\":1,\"registrarPago\":false,\"lineas\":["
				+ "{\"isbn\":\"978-1234567890\",\"nombreLibro\":\"El Principito\",\"precio\":1000.0,"
				+ "\"cantidadVendida\":3,\"cantidadDevuelta\":0},"
				+ "{\"isbn\":\"978-1234567890\",\"nombreLibro\":\"El Principito\",\"precio\":1000.0,"
				+ "\"cantidadVendida\":3,\"cantidadDevuelta\":0}]}")
				.andExpect(status().isBadRequest());
	}

	/**
	 * Dos libros distintos con el mismo ISBN no comparten saldo. Con la clave armada solo con el
	 * ISBN, el saldo de 'El Principito' (5) y el de 'Zz Libro Clonado' (1) se sumaban y liquidar
	 * 6 del primero pasaba, dejando al otro en negativo.
	 */
	@Test
	void tituloDistintoConElMismoIsbnNoPrestaSuSaldo() throws Exception {
		liquidar(linea(6, 0))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/remitos/consignacion/estadocuenta")
				.header("Authorization", "Bearer " + token)
				.param("comercioId", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.nombreLibro == 'Zz Libro Clonado')].cantidad").value(1));
	}

	@Test
	void noSePuedeLiquidarUnTituloQueElComercioNoTiene() throws Exception {
		liquidar("{\"comercioId\":1,\"registrarPago\":false,\"lineas\":[{"
				+ "\"isbn\":\"978-0000000000\",\"nombreLibro\":\"Libro Ajeno\",\"precio\":100.0,"
				+ "\"cantidadVendida\":1,\"cantidadDevuelta\":0}]}")
				.andExpect(status().isBadRequest());
	}

	@Test
	void cantidadNegativaEsRechazada() throws Exception {
		liquidar(linea(-1, 0))
				.andExpect(status().isBadRequest());
	}

	@Test
	void liquidacionVaciaEsRechazada() throws Exception {
		liquidar(linea(0, 0))
				.andExpect(status().isBadRequest());
	}

	/** Una liquidacion invalida no debe dejar ningun documento a medio emitir. */
	@Test
	void liquidacionInvalidaNoEmiteNingunDocumento() throws Exception {
		liquidar("{\"comercioId\":1,\"registrarPago\":false,\"lineas\":["
				+ "{\"isbn\":\"978-1234567890\",\"nombreLibro\":\"El Principito\",\"precio\":1000.0,"
				+ "\"cantidadVendida\":0,\"cantidadDevuelta\":2},"
				+ "{\"isbn\":\"978-9999999999\",\"nombreLibro\":\"Inexistente\",\"precio\":100.0,"
				+ "\"cantidadVendida\":1,\"cantidadDevuelta\":0}]}")
				.andExpect(status().isBadRequest());

		// El saldo quedo intacto: si el retiro se hubiera emitido, serian 3.
		mockMvc.perform(get("/remitos/consignacion/estadocuenta")
				.header("Authorization", "Bearer " + token)
				.param("comercioId", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cantidad").value(5));
	}

	// --- El estado de cuenta refleja los movimientos ---

	@Test
	void elEstadoDeCuentaDescuentaLoVendidoYLoDevuelto() throws Exception {
		liquidar(linea(1, 2)).andExpect(status().isCreated());

		mockMvc.perform(get("/remitos/consignacion/estadocuenta")
				.header("Authorization", "Bearer " + token)
				.param("comercioId", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].entregado").value(5))
				.andExpect(jsonPath("$[0].vendido").value(1))
				.andExpect(jsonPath("$[0].devuelto").value(2))
				.andExpect(jsonPath("$[0].cantidad").value(2))
				.andExpect(jsonPath("$[0].subtotal").value(2000.0));
	}

	/** Saldado por completo, el titulo desaparece; los demas del comercio siguen ahi. */
	@Test
	void elTituloSaldadoDesapareceDelEstadoDeCuenta() throws Exception {
		liquidar(linea(5, 0)).andExpect(status().isCreated());

		mockMvc.perform(get("/remitos/consignacion/estadocuenta")
				.header("Authorization", "Bearer " + token)
				.param("comercioId", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.nombreLibro == 'El Principito')]").isEmpty())
				.andExpect(jsonPath("$[?(@.nombreLibro == 'Martin Fierro')]").isNotEmpty());
	}

	/** Liquidar en dos tandas tiene que dar lo mismo que liquidar de una. */
	@Test
	void liquidacionesSucesivasAcumulan() throws Exception {
		liquidar(linea(2, 0)).andExpect(status().isCreated());
		liquidar(linea(2, 0)).andExpect(status().isCreated());
		liquidar(linea(2, 0)).andExpect(status().isBadRequest());

		mockMvc.perform(get("/remitos/consignacion/estadocuenta")
				.header("Authorization", "Bearer " + token)
				.param("comercioId", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cantidad").value(1));
	}

	// --- Recibo diferido ---

	/** La consulta necesita distinguir lo cobrado de lo pendiente para poder ir a cobrarlo. */
	@Test
	void elRemitoDeVentaSabeSiEstaPagado() throws Exception {
		String respuesta = liquidar(linea(3, 0)).andReturn().getResponse().getContentAsString();
		String ventaId = respuesta.replaceAll(".*\"remitoVentaId\":(\\d+).*", "$1");

		mockMvc.perform(get("/remitos/" + ventaId).header("Authorization", "Bearer " + token))
				.andExpect(jsonPath("$.pagado").value(false))
				.andExpect(jsonPath("$.recibo").doesNotExist());

		mockMvc.perform(post("/remitos/" + ventaId + "/recibo")
				.header("Authorization", "Bearer " + token).param("medioPago", "Efectivo"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/remitos/" + ventaId).header("Authorization", "Bearer " + token))
				.andExpect(jsonPath("$.pagado").value(true))
				.andExpect(jsonPath("$.recibo.rc_medio_pago").value("Efectivo"))
				.andExpect(jsonPath("$.recibo.rc_monto").value(2400.0));
	}

	/** Los remitos de entrega y retiro no son cobrables, asi que nunca figuran como impagos. */
	@Test
	void soloElRemitoDeVentaTieneEstadoDePago() throws Exception {
		liquidar(linea(0, 2)).andExpect(status().isCreated());

		mockMvc.perform(get("/remitos/search/findByAny")
				.header("Authorization", "Bearer " + token)
				.param("parametro", "")
				.param("tipo", "RETIRO"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].recibo").doesNotExist());
	}

	@Test
	void sePuedeEmitirElReciboMasTarde() throws Exception {
		String respuesta = liquidar(linea(5, 0))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		String remitoVentaId = respuesta.replaceAll(".*\"remitoVentaId\":(\\d+).*", "$1");

		mockMvc.perform(post("/remitos/" + remitoVentaId + "/recibo")
				.header("Authorization", "Bearer " + token)
				.param("medioPago", "Transferencia"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.rc_monto").value(4000.0))
				.andExpect(jsonPath("$.rc_medio_pago").value("Transferencia"));
	}

	@Test
	void noSePuedeEmitirDosVecesElMismoRecibo() throws Exception {
		String respuesta = liquidar(linea(5, 0)).andReturn().getResponse().getContentAsString();
		String remitoVentaId = respuesta.replaceAll(".*\"remitoVentaId\":(\\d+).*", "$1");

		mockMvc.perform(post("/remitos/" + remitoVentaId + "/recibo")
				.header("Authorization", "Bearer " + token).param("medioPago", "Efectivo"))
				.andExpect(status().isCreated());
		mockMvc.perform(post("/remitos/" + remitoVentaId + "/recibo")
				.header("Authorization", "Bearer " + token).param("medioPago", "Efectivo"))
				.andExpect(status().isBadRequest());
	}

	/** Un remito de devolucion a distribuidora no es cobrable. */
	@Test
	void noSePuedeEmitirReciboDeUnRemitoQueNoEsDeVenta() throws Exception {
		mockMvc.perform(post("/remitos/1/recibo")
				.header("Authorization", "Bearer " + token).param("medioPago", "Efectivo"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void requiereAutenticacion() throws Exception {
		mockMvc.perform(post("/remitos/consignacion/liquidar")
				.contentType(MediaType.APPLICATION_JSON).content(linea(1, 0)))
				.andExpect(status().isUnauthorized());
	}
}
