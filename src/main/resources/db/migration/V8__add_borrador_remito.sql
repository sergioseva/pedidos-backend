-- Remito a medio cargar, del lado del servidor: guardarlo solo en el navegador no alcanza porque
-- se pierde si el navegador limpia los datos del sitio al cerrarse, y ademas no acompana al
-- operador si cambia de maquina.
--
-- Tabla propia y no un estado dentro de re_remito: un borrador que compartiera tabla con los
-- remitos de verdad habria que excluirlo en cada consulta, y olvidarse en una sola contaminaria
-- los saldos de consignacion.

CREATE TABLE IF NOT EXISTS br_borrador (
  br_borrador_k INT NOT NULL AUTO_INCREMENT,
  br_usuario VARCHAR(100),
  br_tipo VARCHAR(20),
  br_contenido MEDIUMTEXT,
  br_fecha DATETIME,
  PRIMARY KEY (br_borrador_k),
  CONSTRAINT uk_br_usuario_tipo UNIQUE (br_usuario, br_tipo)
);
