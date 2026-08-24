-- Precio vigente de un libro entregado en consignacion, para poder actualizarlo sin reescribir el
-- remito de entrega: ese documento ya se firmo y tiene que seguir diciendo con que precio salio.
-- El saldo y la liquidacion usan este cuando esta cargado.
--
-- El ALTER va detras del chequeo a information_schema por lo mismo que en V6: MySQL no tiene
-- ADD COLUMN IF NOT EXISTS y en desarrollo ddl-auto crea la columna apenas existe la entidad.

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

CALL pedidos_add_column('ri_remito_item', 'ri_precio_actual', 'DOUBLE');

DROP PROCEDURE pedidos_add_column;
