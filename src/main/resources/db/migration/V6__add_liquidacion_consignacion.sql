-- Liquidacion de consignacion: el comercio devuelve lo que no vendio (remito de RETIRO) y paga
-- lo que vendio (remito de VENTA_CONSIGNACION), con un recibo opcional que prueba el pago.
--
-- Los movimientos son remitos y viven en re_remito, asi que el saldo en la calle sale de una
-- sola tabla: entregado - retirado - vendido.
--
-- Los ALTER van detras de un chequeo a information_schema porque MySQL no tiene
-- "ADD COLUMN IF NOT EXISTS". No es una precaucion teorica: en desarrollo, ddl-auto=update crea
-- las columnas en cuanto la entidad existe, y si eso pasa antes de que la migracion se escriba,
-- el ALTER a secas falla, Flyway marca la version como fallida y la aplicacion ya no arranca.

DROP PROCEDURE IF EXISTS pedidos_add_column;

CREATE PROCEDURE pedidos_add_column(IN tabla VARCHAR(64), IN columna VARCHAR(64), IN definicion VARCHAR(255))
BEGIN
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE()
                   AND TABLE_NAME = tabla
                   AND COLUMN_NAME = columna) THEN
    SET @ddl = CONCAT('ALTER TABLE `', tabla, '` ADD COLUMN `', columna, '` ', definicion);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END;

-- Porcentaje que el comercio retiene de cada venta.
CALL pedidos_add_column('cm_comercio', 'cm_comision', 'DOUBLE');

-- Comision congelada al liquidar, solo en remitos de venta. Sin esto, renegociar el porcentaje
-- reescribiria la plata de liquidaciones ya emitidas.
CALL pedidos_add_column('re_remito', 're_comision', 'DOUBLE');

DROP PROCEDURE pedidos_add_column;

-- La unica sobre rc_remito_re es lo que garantiza un solo recibo por remito de venta.
CREATE TABLE IF NOT EXISTS rc_recibo (
  rc_recibo_k INT NOT NULL AUTO_INCREMENT,
  rc_remito_re INT,
  rc_fecha DATETIME,
  rc_monto DOUBLE,
  rc_medio_pago VARCHAR(40),
  rc_observaciones VARCHAR(255),
  PRIMARY KEY (rc_recibo_k),
  CONSTRAINT uk_rc_remito UNIQUE (rc_remito_re),
  CONSTRAINT fk_rc_remito FOREIGN KEY (rc_remito_re) REFERENCES re_remito(re_remito_k)
);
