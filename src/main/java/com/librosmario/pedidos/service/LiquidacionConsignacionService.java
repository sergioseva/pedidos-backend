package com.librosmario.pedidos.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.librosmario.pedidos.entity.Comercio;
import com.librosmario.pedidos.entity.Recibo;
import com.librosmario.pedidos.entity.Remito;
import com.librosmario.pedidos.entity.RemitoItem;
import com.librosmario.pedidos.exception.BadRequestException;
import com.librosmario.pedidos.exception.ResourceNotFoundException;
import com.librosmario.pedidos.payload.ConsignacionEstadoCuentaDTO;
import com.librosmario.pedidos.payload.LiquidacionConsignacionDTO;
import com.librosmario.pedidos.payload.LiquidacionConsignacionDTO.LineaLiquidacionDTO;
import com.librosmario.pedidos.payload.LiquidacionResultadoDTO;
import com.librosmario.pedidos.repository.ComercioRepository;
import com.librosmario.pedidos.repository.ReciboRepository;
import com.librosmario.pedidos.repository.RemitoItemRepository;
import com.librosmario.pedidos.repository.RemitoRepository;

/**
 * Cierra la cuenta de un comercio: lo que no vendio vuelve (remito de retiro) y lo que vendio
 * pasa a deber (remito de venta), con su recibo si paga en el acto.
 *
 * Los dos remitos son movimientos que descuentan saldo; la diferencia es a donde fue el libro.
 */
@Service
public class LiquidacionConsignacionService {

	private static final Logger logger = LogManager.getLogger(LiquidacionConsignacionService.class);

	@Autowired
	RemitoRepository remitoRepository;

	@Autowired
	RemitoItemRepository remitoItemRepository;

	@Autowired
	ComercioRepository comercioRepository;

	@Autowired
	ReciboRepository reciboRepository;

	/**
	 * Una liquidacion es todo o nada: si una sola linea excede el saldo, no se emite ningun
	 * documento. Emitir el retiro y fallar en la venta dejaria la cuenta a mitad de camino y sin
	 * forma obvia de retomarla.
	 */
	@Transactional
	public LiquidacionResultadoDTO liquidar(LiquidacionConsignacionDTO liquidacion) {
		if (liquidacion.getComercioId() == null) {
			throw new BadRequestException("Falta el comercio a liquidar");
		}
		Comercio comercio = comercioRepository.findById(liquidacion.getComercioId())
				.orElseThrow(() -> new ResourceNotFoundException("Comercio", "id", liquidacion.getComercioId()));

		List<LineaLiquidacionDTO> lineas = lineasConMovimiento(liquidacion.getLineas());
		if (lineas.isEmpty()) {
			throw new BadRequestException("No hay nada para liquidar: marque ejemplares vendidos o devueltos");
		}
		validarContraSaldo(comercio, lineas);

		Date fecha = new Date();
		Remito retiro = crearRemito(comercio, fecha, Remito.TIPO_RETIRO, liquidacion.getObservaciones(),
				lineas, LineaLiquidacionDTO::getCantidadDevuelta);
		Remito venta = crearRemito(comercio, fecha, Remito.TIPO_VENTA_CONSIGNACION, liquidacion.getObservaciones(),
				lineas, LineaLiquidacionDTO::getCantidadVendida);

		double totalTapa = 0d;
		double neto = 0d;
		Integer reciboId = null;
		if (venta != null) {
			// La comision se copia del comercio y queda congelada en el remito.
			venta.setRe_comision(comercio.getComision());
			venta = remitoRepository.save(venta);
			totalTapa = venta.getTotal();
			neto = venta.getNetoAPagar();
			if (liquidacion.isRegistrarPago()) {
				reciboId = emitirRecibo(venta, neto, liquidacion.getMedioPago()).getRc_recibo_k();
			}
		}

		logger.info("Liquidacion comercio '{}': retiro={} venta={} recibo={} neto={}",
				comercio.getId(),
				retiro == null ? null : retiro.getRe_remito_k(),
				venta == null ? null : venta.getRe_remito_k(),
				reciboId, neto);

		return new LiquidacionResultadoDTO(
				retiro == null ? null : retiro.getRe_remito_k(),
				venta == null ? null : venta.getRe_remito_k(),
				reciboId,
				totalTapa,
				comercio.getComision() == null ? 0d : comercio.getComision(),
				neto);
	}

	/** Emite el recibo de un remito de venta que quedo impago. */
	@Transactional
	public Recibo pagarRemito(Integer remitoId, String medioPago) {
		Remito remito = remitoRepository.findById(remitoId)
				.orElseThrow(() -> new ResourceNotFoundException("Remito", "id", remitoId));
		if (!Remito.TIPO_VENTA_CONSIGNACION.equals(remito.getRe_tipo())) {
			throw new BadRequestException("Solo se puede pagar un remito de venta de consignacion");
		}
		if (reciboRepository.findByRemitoId(remitoId).isPresent()) {
			throw new BadRequestException("El remito ya tiene un recibo emitido");
		}
		return emitirRecibo(remito, remito.getNetoAPagar(), medioPago);
	}

	private Recibo emitirRecibo(Remito remito, double monto, String medioPago) {
		Recibo recibo = new Recibo();
		recibo.setRc_remito_re(remito);
		recibo.setRc_fecha(new Date());
		recibo.setRc_monto(monto);
		recibo.setRc_medio_pago(medioPago);
		recibo = reciboRepository.save(recibo);
		// Los dos lados de la asociacion se sincronizan a mano: guardar solo el dueño deja al
		// remito ya cargado en la sesion creyendose impago hasta que alguien lo recargue.
		remito.setRecibo(recibo);
		return recibo;
	}

	private List<LineaLiquidacionDTO> lineasConMovimiento(List<LineaLiquidacionDTO> lineas) {
		List<LineaLiquidacionDTO> conMovimiento = new ArrayList<>();
		if (lineas == null) {
			return conMovimiento;
		}
		for (LineaLiquidacionDTO linea : lineas) {
			if (linea.getCantidadVendida() < 0 || linea.getCantidadDevuelta() < 0) {
				throw new BadRequestException("Cantidad negativa en '" + linea.getNombreLibro() + "'");
			}
			if (linea.getCantidadVendida() > 0 || linea.getCantidadDevuelta() > 0) {
				conMovimiento.add(linea);
			}
		}
		return conMovimiento;
	}

	/**
	 * Nadie puede devolver ni vender mas de lo que tiene. El saldo se relee de la base y no se
	 * confia en el que trajo la pantalla, que pudo quedar viejo si alguien liquido en el medio.
	 */
	private void validarContraSaldo(Comercio comercio, List<LineaLiquidacionDTO> lineas) {
		// Ambos lados se agregan por clave antes de comparar: dos filas del mismo titulo pidiendo
		// 3 cada una contra un saldo de 5 tienen que fallar, y comparadas de a una pasarian.
		Map<String, Long> saldos = new HashMap<>();
		for (ConsignacionEstadoCuentaDTO fila
				: remitoItemRepository.estadoCuentaConsignacion(comercio.getId(), null, null)) {
			saldos.merge(claveTitulo(fila.getIsbn(), fila.getNombreLibro()), fila.getCantidad(), Long::sum);
		}

		Map<String, Long> pedidos = new HashMap<>();
		Map<String, String> nombres = new HashMap<>();
		for (LineaLiquidacionDTO linea : lineas) {
			String clave = claveTitulo(linea.getIsbn(), linea.getNombreLibro());
			pedidos.merge(clave, (long) (linea.getCantidadVendida() + linea.getCantidadDevuelta()), Long::sum);
			nombres.putIfAbsent(clave, linea.getNombreLibro());
		}

		for (Map.Entry<String, Long> pedido : pedidos.entrySet()) {
			long saldo = saldos.getOrDefault(pedido.getKey(), 0L);
			if (pedido.getValue() > saldo) {
				throw new BadRequestException(String.format(
						"'%s': se intenta liquidar %d ejemplares y el comercio tiene %d",
						nombres.get(pedido.getKey()), pedido.getValue(), saldo));
			}
		}
	}

	/**
	 * Misma clave con la que agrupa el estado de cuenta: ISBN Y titulo, no uno u otro.
	 *
	 * Con ISBN solo, dos libros distintos que comparten ISBN caen en la misma bolsa y la
	 * validacion suma sus saldos, dejando pasar que se liquide de mas uno a costa del otro. No es
	 * teorico: media catalogo tiene el ISBN guardado en notacion cientifica ('9.78987E+12'), asi
	 * que titulos que no tienen nada que ver comparten la misma cadena.
	 */
	private String claveTitulo(String isbn, String nombreLibro) {
		return normalizar(isbn) + "|" + normalizar(nombreLibro);
	}

	private String normalizar(String valor) {
		return valor == null ? "" : valor.trim().toLowerCase();
	}

	private Remito crearRemito(Comercio comercio, Date fecha, String tipo, String observaciones,
			List<LineaLiquidacionDTO> lineas, java.util.function.Function<LineaLiquidacionDTO, Integer> cantidad) {
		List<RemitoItem> items = new ArrayList<>();
		for (LineaLiquidacionDTO linea : lineas) {
			int cant = cantidad.apply(linea);
			if (cant <= 0) {
				continue;
			}
			RemitoItem item = new RemitoItem();
			item.setRi_nombre_libro(linea.getNombreLibro());
			item.setRi_autor(linea.getAutor());
			item.setRi_editorial(linea.getEditorial());
			item.setRi_isbn(linea.getIsbn());
			item.setRi_precio(linea.getPrecio());
			item.setRi_cantidad(cant);
			items.add(item);
		}
		if (items.isEmpty()) {
			return null;
		}

		Remito remito = new Remito();
		remito.setRe_tipo(tipo);
		remito.setRe_fecha(fecha);
		remito.setRe_comercio_cm(comercio);
		remito.setRe_observaciones(observaciones);
		items.forEach(i -> i.setRi_remito_re(remito));
		remito.setItems(items);
		return remitoRepository.save(remito);
	}
}
