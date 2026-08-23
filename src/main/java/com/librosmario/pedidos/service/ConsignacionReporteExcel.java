package com.librosmario.pedidos.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.librosmario.pedidos.entity.Comercio;
import com.librosmario.pedidos.payload.ConsignacionEstadoCuentaDTO;

/**
 * Arma el detalle de consignacion de un comercio como .xlsx: una fila por titulo con lo que tiene
 * en su poder, mas una fila de totales. Es el mismo contenido del papel que se le entrega, en un
 * formato que el negocio puede cruzar contra sus propias existencias.
 *
 * Sigue el molde de {@link VentaReporteExcel}: POI ya esta en el classpath por la importacion de
 * catalogo y los tamanos son chicos, asi que armarlo en memoria alcanza.
 */
public final class ConsignacionReporteExcel {

	private static final int COL_LIBRO = 0;
	private static final int COL_AUTOR = 1;
	private static final int COL_EDITORIAL = 2;
	private static final int COL_ISBN = 3;
	private static final int COL_CANTIDAD = 4;
	private static final int COL_PRECIO = 5;
	private static final int COL_SUBTOTAL = 6;

	private static final String[] TITULOS = {
			"Libro", "Autor", "Editorial", "ISBN", "Cantidad", "Precio", "Subtotal"
	};

	private ConsignacionReporteExcel() {
	}

	public static byte[] build(Comercio comercio, List<ConsignacionEstadoCuentaDTO> filas) {
		try (Workbook workbook = new XSSFWorkbook();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet("Consignacion");
			CellStyle header = headerStyle(workbook);
			CellStyle money = moneyStyle(workbook);
			CellStyle totalLabel = totalLabelStyle(workbook);
			CellStyle totalMoney = totalMoneyStyle(workbook);

			int fila = 0;

			// Encabezado con el negocio: la planilla viaja suelta y tiene que decir de quien es.
			Row titulo = sheet.createRow(fila++);
			Cell celdaTitulo = titulo.createCell(0);
			celdaTitulo.setCellValue("Libros en consignacion — " + nombre(comercio));
			celdaTitulo.setCellStyle(header);
			fila++;

			Row cabecera = sheet.createRow(fila++);
			for (int i = 0; i < TITULOS.length; i++) {
				Cell c = cabecera.createCell(i);
				c.setCellValue(TITULOS[i]);
				c.setCellStyle(header);
			}

			long unidades = 0;
			double total = 0d;
			for (ConsignacionEstadoCuentaDTO f : filas) {
				Row r = sheet.createRow(fila++);
				r.createCell(COL_LIBRO).setCellValue(texto(f.getNombreLibro()));
				r.createCell(COL_AUTOR).setCellValue(texto(f.getAutor()));
				r.createCell(COL_EDITORIAL).setCellValue(texto(f.getEditorial()));
				r.createCell(COL_ISBN).setCellValue(texto(f.getIsbn()));
				r.createCell(COL_CANTIDAD).setCellValue(f.getCantidad());
				Cell precio = r.createCell(COL_PRECIO);
				precio.setCellValue(f.getPrecio());
				precio.setCellStyle(money);
				Cell subtotal = r.createCell(COL_SUBTOTAL);
				subtotal.setCellValue(f.getSubtotal());
				subtotal.setCellStyle(money);

				unidades += f.getCantidad();
				total += f.getSubtotal();
			}

			Row totales = sheet.createRow(fila);
			Cell etiqueta = totales.createCell(COL_ISBN);
			etiqueta.setCellValue("TOTALES");
			etiqueta.setCellStyle(totalLabel);
			Cell celdaUnidades = totales.createCell(COL_CANTIDAD);
			celdaUnidades.setCellValue(unidades);
			celdaUnidades.setCellStyle(totalLabel);
			Cell celdaTotal = totales.createCell(COL_SUBTOTAL);
			celdaTotal.setCellValue(total);
			celdaTotal.setCellStyle(totalMoney);

			for (int i = 0; i < TITULOS.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException("Error armando el reporte de consignacion", e);
		}
	}

	private static String nombre(Comercio comercio) {
		return comercio == null || comercio.getDescripcion() == null ? "" : comercio.getDescripcion();
	}

	private static String texto(String valor) {
		return valor == null ? "" : valor;
	}

	private static CellStyle headerStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);
		return style;
	}

	private static CellStyle moneyStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
		return style;
	}

	private static CellStyle totalLabelStyle(Workbook workbook) {
		CellStyle style = headerStyle(workbook);
		style.setAlignment(HorizontalAlignment.RIGHT);
		return style;
	}

	private static CellStyle totalMoneyStyle(Workbook workbook) {
		CellStyle style = moneyStyle(workbook);
		Font font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);
		return style;
	}
}
