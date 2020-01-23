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

INSERT INTO `librosmario`.`roles` (`id`, `name`) VALUES ('1', 'ROLE_ADMIN');
INSERT INTO `librosmario`.`roles` (`id`, `name`) VALUES ('2', 'ROLE_USER');

commit;

CREATE TABLE `librosmario`.`bt_batchstatistics` (
  `id` INT NOT NULL,
  `bt_proceso` VARCHAR(45) NOT NULL,
  `bt_starttime` DATETIME NULL,
  `bt_endtime` DATETIME NULL,
  `bt_registros` INT NULL,
  `bt_errores` INT NULL,
  PRIMARY KEY (`id`));
  
ALTER TABLE `librosmario`.`bt_batchstatistics` 
CHANGE COLUMN `id` `id` INT(11) NOT NULL AUTO_INCREMENT ,
ADD UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE;
;
  
