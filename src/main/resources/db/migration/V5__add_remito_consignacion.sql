-- Remitos de consignacion: la libreria entrega libros a un punto de venta (hotel, almacen,
-- kiosco) en vez de devolverlos a una distribuidora.
--
-- Las columnas se crean aca y no se dejan a ddl-auto porque Flyway corre ANTES de Hibernate:
-- el backfill del tipo necesita que re_tipo ya exista. Hibernate encuentra despues las columnas
-- puestas y no vuelve a tocarlas.

CREATE TABLE IF NOT EXISTS cm_comercio (
  cm_comercio_k INT NOT NULL AUTO_INCREMENT,
  cm_descripcion VARCHAR(255),
  cm_direccion VARCHAR(255),
  cm_contacto VARCHAR(255),
  cm_telefono VARCHAR(255),
  cm_cuit VARCHAR(255),
  PRIMARY KEY (cm_comercio_k)
);

ALTER TABLE re_remito ADD COLUMN re_tipo VARCHAR(20);
ALTER TABLE re_remito ADD COLUMN re_comercio_cm INT;

ALTER TABLE re_remito
  ADD CONSTRAINT fk_re_comercio FOREIGN KEY (re_comercio_cm) REFERENCES cm_comercio(cm_comercio_k);

-- Todo lo que existe hoy es una devolucion a distribuidora.
UPDATE re_remito SET re_tipo = 'DEVOLUCION' WHERE re_tipo IS NULL;
