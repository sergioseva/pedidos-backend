ALTER TABLE `librosmario`.`pi_pedido_item` 
ADD COLUMN `pi_ensucursal` BIT(1) NULL DEFAULT 0 AFTER `pi_pendiente`,
ADD COLUMN `pi_retirado` BIT(1) NULL DEFAULT 0 AFTER `pi_ensucursal`,
ADD COLUMN `pi_fecha_retiro` DATETIME NULL AFTER `pi_retirado`;

ALTER TABLE `librosmario`.`pi_pedido_item` 
DROP FOREIGN KEY `pi_pedido_pe`;
ALTER TABLE `librosmario`.`pi_pedido_item` 
CHANGE COLUMN `pi_pedido_pe` `pi_pedido_pe` INT(10) NULL ,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`pi_pedido_item_k`);
;
ALTER TABLE `librosmario`.`pi_pedido_item` 
ADD CONSTRAINT `pi_pedido_pe`
  FOREIGN KEY (`pi_pedido_pe`)
  REFERENCES `librosmario`.`pe_pedido` (`pe_pedido_k`);
  

SET SQL_SAFE_UPDATES = 0;

UPDATE `librosmario`.`pi_pedido_item`
SET
`pi_ensucursal` = 1,
`pi_retirado` = 1;

commit;