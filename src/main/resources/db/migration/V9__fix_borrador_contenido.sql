-- br_contenido quedo como TINYTEXT (255 bytes) y un remito de unos pocos libros no entra.
--
-- La V8 declaraba MEDIUMTEXT pero nunca llego a crear la tabla: Hibernate la habia creado antes
-- con ddl-auto, y el CREATE TABLE IF NOT EXISTS la encontro hecha y no hizo nada. El IF NOT
-- EXISTS evito que la migracion fallara y con eso convirtio el problema en algo silencioso.
--
-- MODIFY es idempotente: correrlo sobre una columna que ya es MEDIUMTEXT no cambia nada.
ALTER TABLE br_borrador MODIFY COLUMN br_contenido MEDIUMTEXT;

-- La unica que la V8 tampoco llego a crear. Se limpian primero los duplicados que pudieran
-- haberse colado sin ella, conservando el borrador mas reciente de cada usuario y tipo.
DELETE b FROM br_borrador b
JOIN (SELECT br_usuario, br_tipo, MAX(br_borrador_k) AS conservar
      FROM br_borrador GROUP BY br_usuario, br_tipo HAVING COUNT(*) > 1) d
  ON d.br_usuario = b.br_usuario AND d.br_tipo = b.br_tipo
WHERE b.br_borrador_k <> d.conservar;

DROP PROCEDURE IF EXISTS pedidos_add_unique;

CREATE PROCEDURE pedidos_add_unique(IN tabla VARCHAR(64), IN indice VARCHAR(64), IN columnas VARCHAR(255))
BEGIN
  IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = tabla AND INDEX_NAME = indice) THEN
    SET @ddl = CONCAT('ALTER TABLE `', tabla, '` ADD CONSTRAINT `', indice, '` UNIQUE (', columnas, ')');
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END;

CALL pedidos_add_unique('br_borrador', 'uk_br_usuario_tipo', 'br_usuario, br_tipo');

DROP PROCEDURE pedidos_add_unique;
