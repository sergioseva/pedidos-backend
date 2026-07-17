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

import com.librosmario.pedidos.entity.Venta;
import com.librosmario.pedidos.entity.VentaItem;

/**
 * Builds the ventas report as a real .xlsx: one row per book sold (a sale's line items are flattened
 * out, each carrying its ticket's context) with a bold header and a totals row. Uses the POI already
 * on the classpath for catalog import. Report sizes are modest, so building in memory is fine.
 */
public final class VentaReporteExcel {

	/** Fixed column layout; the header labels and the totals row both key off these positions. */
	private static final int COL_VENTA = 0;
	private static final int COL_FECHA = 1;
	private static final int COL_CLIENTE = 2;
	private static final int COL_VENDEDOR = 3;
	private static final int COL_ISBN = 4;
	private static final int COL_LIBRO = 5;
	private static final int COL_AUTOR = 6;
	private static final int COL_CANTIDAD = 7;
	private static final int COL_PRECIO = 8;
	private static final int COL_SUBTOTAL = 9;

	private static final String[] TITULOS = {
			"Venta", "Fecha", "Cliente", "Vendedor", "ISBN", "Libro", "Autor", "Cantidad", "Precio", "Subtotal"
	};

	private VentaReporteExcel() {
	}

	public static byte[] build(List<Venta> ventas) {
		try (Workbook workbook = new XSSFWorkbook();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet("Ventas");
			CellStyle header = headerStyle(workbook);
			CellStyle fecha = dateStyle(workbook);
			CellStyle money = moneyStyle(workbook);
			CellStyle totalLabel = totalLabelStyle(workbook);
			CellStyle totalMoney = totalMoneyStyle(workbook);

			Row cabecera = sheet.createRow(0);
			for (int i = 0; i < TITULOS.length; i++) {
				Cell c = cabecera.createCell(i);
				c.setCellValue(TITULOS[i]);
				c.setCellStyle(header);
			}

			int fila = 1;
			long unidadesTotales = 0;
			double montoTotal = 0;
			for (Venta venta : ventas) {
				if (venta.getItems() == null) {
					continue;
				}
				for (VentaItem item : venta.getItems()) {
					long cantidad = item.getCantidad() == null ? 0 : item.getCantidad();
					double precio = item.getPrecio() == null ? 0d : item.getPrecio();
					double subtotal = cantidad * precio;
					unidadesTotales += cantidad;
					montoTotal += subtotal;

					Row row = sheet.createRow(fila++);
					row.createCell(COL_VENTA).setCellValue(venta.getId() == null ? 0 : venta.getId());

					Cell celFecha = row.createCell(COL_FECHA);
					if (venta.getFecha() != null) {
						celFecha.setCellValue(venta.getFecha());
						celFecha.setCellStyle(fecha);
					}

					row.createCell(COL_CLIENTE).setCellValue(
							venta.getCliente() == null ? "" : venta.getCliente().getNombre());
					row.createCell(COL_VENDEDOR).setCellValue(venta.getUsuario() == null ? "" : venta.getUsuario());
					row.createCell(COL_ISBN).setCellValue(item.getIsbn() == null ? "" : item.getIsbn());
					row.createCell(COL_LIBRO).setCellValue(item.getLibro() == null ? "" : item.getLibro());
					row.createCell(COL_AUTOR).setCellValue(item.getAutor() == null ? "" : item.getAutor());
					row.createCell(COL_CANTIDAD).setCellValue(cantidad);

					Cell celPrecio = row.createCell(COL_PRECIO);
					celPrecio.setCellValue(precio);
					celPrecio.setCellStyle(money);

					Cell celSubtotal = row.createCell(COL_SUBTOTAL);
					celSubtotal.setCellValue(subtotal);
					celSubtotal.setCellStyle(money);
				}
			}

			Row totales = sheet.createRow(fila);
			Cell etiqueta = totales.createCell(COL_VENTA);
			etiqueta.setCellValue("TOTAL");
			etiqueta.setCellStyle(totalLabel);
			totales.createCell(COL_CANTIDAD).setCellValue(unidadesTotales);
			totales.getCell(COL_CANTIDAD).setCellStyle(totalLabel);
			Cell celMonto = totales.createCell(COL_SUBTOTAL);
			celMonto.setCellValue(montoTotal);
			celMonto.setCellStyle(totalMoney);

			for (int i = 0; i < TITULOS.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			// In-memory workbook: an IOException here is not recoverable by the caller.
			throw new UncheckedIOException("No se pudo generar el reporte de ventas", e);
		}
	}

	private static CellStyle headerStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		style.setFont(font);
		return style;
	}

	private static CellStyle dateStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setDataFormat(wb.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		return style;
	}

	private static CellStyle moneyStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
		return style;
	}

	private static CellStyle totalLabelStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.LEFT);
		return style;
	}

	private static CellStyle totalMoneyStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		style.setFont(font);
		style.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
		return style;
	}
}
