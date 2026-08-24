insert into users(id,name,username, email, password) values (1,'test','test','test@test.com','$2a$10$jkSu5R0yZPteOd9yY5J/p.gFnr1XVGIGmisaE.6MhNE84Bcu7Woc.');
-- Same bcrypt hash as 'test' (password 12345678), but ROLE_USER only. 'test' holds both roles, so
-- without this account an admin-only endpoint cannot be proven to reject a plain user.
insert into users(id,name,username, email, password) values (2,'vendedor','vendedor','vendedor@test.com','$2a$10$jkSu5R0yZPteOd9yY5J/p.gFnr1XVGIGmisaE.6MhNE84Bcu7Woc.');
insert into roles (id,name) values(1,'ROLE_ADMIN');
insert into roles (id,name) values(2,'ROLE_USER');
insert into user_roles(user_id,role_id) values (1,1);
insert into user_roles(user_id,role_id) values (1,2);
insert into user_roles(user_id,role_id) values (2,2);

insert into ed_editorial(ed_editorial_k,ed_descripcion) values (1,'Distribuidora test');
insert into ed_editorial(ed_editorial_k,ed_descripcion) values (2,'Distribuidora test 2');

insert into cl_cliente(cl_cliente_k,cl_nombre,CL_TELEFONO_MOVIL) values (1,'Cliente 1','1111111');
insert into cl_cliente(cl_cliente_k,cl_nombre,CL_TELEFONO_MOVIL) values (2,'Cliente 2','1111111');
insert into cl_cliente(cl_cliente_k,cl_nombre,CL_TELEFONO_MOVIL) values (3,'Cliente 3','1111111');



insert into pe_pedido(pe_pedido_k,pe_cliente_cl,pe_adomicilio) values (1,1,false);

insert into pi_pedido_item(pi_pedido_item_k,pi_pedido_pe,pi_cantidad,pi_nombre_libro,pi_pendiente,pi_editorial_ed,pi_ensucursal,pi_retirado) 
		values (1,1,1,'libro1',true,1,false,false);
insert into pi_pedido_item(pi_pedido_item_k,pi_pedido_pe,pi_cantidad,pi_nombre_libro,pi_pendiente,pi_editorial_ed,pi_ensucursal,pi_retirado) 
		values (2,1,1,'libro2',false,1,false,false);


insert into pe_pedido(pe_pedido_k,pe_cliente_cl,pe_adomicilio) values (2,2,false);
insert into pi_pedido_item(pi_pedido_item_k,pi_pedido_pe,pi_cantidad,pi_nombre_libro,pi_pendiente,pi_editorial_ed,pi_ensucursal,pi_retirado)
		values (3,1,1,'libro3',true,1,false,false);
insert into pi_pedido_item(pi_pedido_item_k,pi_pedido_pe,pi_cantidad,pi_nombre_libro,pi_pendiente,pi_editorial_ed,pi_ensucursal,pi_retirado)
		values (4,1,1,'libro4',true,1,false,false);

-- Comercio test data (destinatarios de remitos de consignacion)
insert into cm_comercio(cm_comercio_k, cm_descripcion, cm_direccion, cm_contacto, cm_telefono, cm_cuit, cm_comision) values (1, 'Hotel Costa Azul', 'Costanera 100', 'Recepcion', '03446-100100', '30-11111111-1', 20.0);
-- Sin comision cargada: el comercio paga precio de tapa completo.
insert into cm_comercio(cm_comercio_k, cm_descripcion, cm_direccion, cm_contacto, cm_telefono, cm_cuit) values (2, 'Almacen Don Pedro', 'San Martin 250', 'Pedro', '03446-200200', '20-22222222-2');

-- Remito test data
insert into re_remito(re_remito_k, re_fecha, re_tipo, re_distribuidora_ed, re_observaciones) values (1, '2025-01-15', 'DEVOLUCION', 1, 'Remito de prueba');
insert into re_remito(re_remito_k, re_fecha, re_tipo, re_distribuidora_ed, re_observaciones) values (2, '2025-02-20', 'DEVOLUCION', 2, 'Segundo remito');
-- Remitos de consignacion: 3 y 4 al mismo comercio, para que el estado de cuenta tenga algo que agrupar.
insert into re_remito(re_remito_k, re_fecha, re_tipo, re_comercio_cm, re_observaciones) values (3, '2025-03-05', 'CONSIGNACION', 1, 'Consignacion hotel');
insert into re_remito(re_remito_k, re_fecha, re_tipo, re_comercio_cm, re_observaciones) values (4, '2025-03-25', 'CONSIGNACION', 1, 'Reposicion hotel');
insert into re_remito(re_remito_k, re_fecha, re_tipo, re_comercio_cm, re_observaciones) values (5, '2025-04-01', 'CONSIGNACION', 2, 'Consignacion almacen');
-- Sin re_tipo: simula un remito anterior a la migracion, que debe seguir contando como devolucion.
insert into re_remito(re_remito_k, re_fecha, re_distribuidora_ed, re_observaciones) values (6, '2024-12-01', 1, 'Remito heredado');

insert into ri_remito_item(ri_remito_item_k, ri_remito_re, ri_cantidad, ri_nombre_libro, ri_autor, ri_editorial, ri_isbn, ri_precio) values (1, 1, 2, 'El Principito', 'Saint-Exupery', 'Salamandra', '978-1234567890', 1500.00);
insert into ri_remito_item(ri_remito_item_k, ri_remito_re, ri_cantidad, ri_nombre_libro, ri_autor, ri_editorial, ri_isbn, ri_precio) values (2, 1, 1, 'Cien anos de soledad', 'Garcia Marquez', 'Sudamericana', '978-0987654321', 2500.00);
-- El mismo titulo en dos remitos al mismo comercio: 3 + 2 = 5 ejemplares en una sola fila.
insert into ri_remito_item(ri_remito_item_k, ri_remito_re, ri_cantidad, ri_nombre_libro, ri_autor, ri_editorial, ri_isbn, ri_precio) values (3, 3, 3, 'El Principito', 'Saint-Exupery', 'Salamandra', '978-1234567890', 1000.00);
insert into ri_remito_item(ri_remito_item_k, ri_remito_re, ri_cantidad, ri_nombre_libro, ri_autor, ri_editorial, ri_isbn, ri_precio) values (4, 4, 2, 'El Principito', 'Saint-Exupery', 'Salamandra', '978-1234567890', 1000.00);
insert into ri_remito_item(ri_remito_item_k, ri_remito_re, ri_cantidad, ri_nombre_libro, ri_autor, ri_editorial, ri_isbn, ri_precio) values (5, 5, 4, 'Rayuela', 'Cortazar', 'Alfaguara', '978-5555555555', 3000.00);
-- Segundo titulo del comercio 1, para probar que cada remito lleva solo su lado.
insert into ri_remito_item(ri_remito_item_k, ri_remito_re, ri_cantidad, ri_nombre_libro, ri_autor, ri_editorial, ri_isbn, ri_precio) values (6, 3, 2, 'Martin Fierro', 'Hernandez', 'Losada', '978-7777777777', 2000.00);
-- Otro libro con el MISMO ISBN que 'El Principito': es lo que pasa en la base real, donde medio
-- catalogo quedo con el ISBN en notacion cientifica y titulos ajenos comparten la cadena.
insert into ri_remito_item(ri_remito_item_k, ri_remito_re, ri_cantidad, ri_nombre_libro, ri_autor, ri_editorial, ri_isbn, ri_precio) values (7, 3, 1, 'Zz Libro Clonado', 'Otro Autor', 'Otra Ed', '978-1234567890', 500.00);
-- Con espacios adelante, como viene buena parte del catalogo real: debe ordenar por 'Martin', no primero.
insert into ri_remito_item(ri_remito_item_k, ri_remito_re, ri_cantidad, ri_nombre_libro, ri_autor, ri_editorial, ri_isbn, ri_precio) values (8, 3, 1, '  Martin Fierro', 'Hernandez', 'Losada', '978-8888888888', 900.00);

-- PedidoDistribuidora test data (1 per item)
insert into pd_pedido_a_distribuidora(pd_pedido_a_distribuidora_k, pd_fecha, pd_distribuidora_ed, pd_pedido_realizado, pd_pedido_item_pi) values (1, '2025-03-10 10:00:00', 1, false, 1);
insert into pd_pedido_a_distribuidora(pd_pedido_a_distribuidora_k, pd_fecha, pd_distribuidora_ed, pd_pedido_realizado, pd_pedido_item_pi) values (2, '2025-04-15 14:30:00', 2, true, 3);

-- Configuracion test data
insert into co_configuracion(co_configuracion_k, co_nombre, co_direccion, co_telefono) values (1, 'Libros Mario', 'Calle Falsa 123', '011-1234567');

-- ConfiguracionRemito test data
insert into cr_configuracion_remito(cr_configuracion_remito_k, cr_remitente) values (1, 'Libros Mario - Remitente');

-- Catalogo test data
insert into cg_catalogo(cg_catalogo_k, cg_codigo_luongo, cg_descripcion, cg_autor, cg_precio, cg_editorial, cg_isbn, cg_observaciones) values (1, 'LU001', 'Sara y las estrellas', 'Lark Rise', 100.0, 'Planeta', '978-1111111111', 'novela juvenil');
insert into cg_catalogo(cg_catalogo_k, cg_codigo_luongo, cg_descripcion, cg_autor, cg_precio, cg_editorial, cg_isbn, cg_observaciones) values (2, 'LU002', 'El arte de programar', 'Donald Knuth', 200.0, 'Addison', '978-2222222222', 'referencia tecnica');
insert into cg_catalogo(cg_catalogo_k, cg_codigo_luongo, cg_descripcion, cg_autor, cg_precio, cg_editorial, cg_isbn, cg_observaciones) values (3, 'LU003', 'Sara en el bosque', 'Maria Lopez', 150.0, 'Santillana', '978-3333333333', 'cuento infantil');
-- A plain-digit ISBN: this is what a barcode reader actually emits, and what the real Luongo
-- catalog stores. Rows 1-3 above keep their dashes so the normalizing fallback stays covered.
insert into cg_catalogo(cg_catalogo_k, cg_codigo_luongo, cg_descripcion, cg_autor, cg_precio, cg_editorial, cg_isbn, cg_observaciones) values (4, 'LU004', 'Pan y manteca', 'Cocinero Anonimo', 6477.27, 'Sudamericana', '9789871051014', 'cocina');
-- Mismo ISBN que 'El Principito' en consignacion, para probar que se trae el precio del catalogo.
insert into cg_catalogo(cg_catalogo_k, cg_codigo_luongo, cg_descripcion, cg_autor, cg_precio, cg_editorial, cg_isbn, cg_observaciones) values (5, 'LU005', 'El Principito', 'Saint-Exupery', 4000.0, 'Salamandra', '978-1234567890', 'infantil');

-- Reset identity counters after explicit ID inserts (required for H2 2.x)
ALTER TABLE users ALTER COLUMN id RESTART WITH 10;
ALTER TABLE roles ALTER COLUMN id RESTART WITH 10;
ALTER TABLE ed_editorial ALTER COLUMN ed_editorial_k RESTART WITH 10;
ALTER TABLE cm_comercio ALTER COLUMN cm_comercio_k RESTART WITH 10;
ALTER TABLE cl_cliente ALTER COLUMN cl_cliente_k RESTART WITH 10;
ALTER TABLE pe_pedido ALTER COLUMN pe_pedido_k RESTART WITH 10;
ALTER TABLE pi_pedido_item ALTER COLUMN pi_pedido_item_k RESTART WITH 10;
ALTER TABLE re_remito ALTER COLUMN re_remito_k RESTART WITH 10;
ALTER TABLE ri_remito_item ALTER COLUMN ri_remito_item_k RESTART WITH 10;
ALTER TABLE co_configuracion ALTER COLUMN co_configuracion_k RESTART WITH 10;
ALTER TABLE cr_configuracion_remito ALTER COLUMN cr_configuracion_remito_k RESTART WITH 10;
ALTER TABLE cg_catalogo ALTER COLUMN cg_catalogo_k RESTART WITH 10;
ALTER TABLE pd_pedido_a_distribuidora ALTER COLUMN pd_pedido_a_distribuidora_k RESTART WITH 10;