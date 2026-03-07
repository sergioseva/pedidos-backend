insert into users(id,name,username, email, password) values (1,'test','test','test@test.com','$2a$10$jkSu5R0yZPteOd9yY5J/p.gFnr1XVGIGmisaE.6MhNE84Bcu7Woc.');
insert into roles (id,name) values(1,'ROLE_ADMIN');
insert into roles (id,name) values(2,'ROLE_USER');
insert into user_roles(user_id,role_id) values (1,1);
insert into user_roles(user_id,role_id) values (1,2);

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

-- Remito test data
insert into re_remito(re_remito_k, re_fecha, re_distribuidora_ed, re_observaciones) values (1, '2025-01-15', 1, 'Remito de prueba');
insert into re_remito(re_remito_k, re_fecha, re_distribuidora_ed, re_observaciones) values (2, '2025-02-20', 2, 'Segundo remito');

insert into ri_remito_item(ri_remito_item_k, ri_remito_re, ri_cantidad, ri_nombre_libro, ri_autor, ri_editorial, ri_isbn, ri_precio) values (1, 1, 2, 'El Principito', 'Saint-Exupery', 'Salamandra', '978-1234567890', 1500.00);
insert into ri_remito_item(ri_remito_item_k, ri_remito_re, ri_cantidad, ri_nombre_libro, ri_autor, ri_editorial, ri_isbn, ri_precio) values (2, 1, 1, 'Cien anos de soledad', 'Garcia Marquez', 'Sudamericana', '978-0987654321', 2500.00);

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

-- Reset identity counters after explicit ID inserts (required for H2 2.x)
ALTER TABLE users ALTER COLUMN id RESTART WITH 10;
ALTER TABLE roles ALTER COLUMN id RESTART WITH 10;
ALTER TABLE ed_editorial ALTER COLUMN ed_editorial_k RESTART WITH 10;
ALTER TABLE cl_cliente ALTER COLUMN cl_cliente_k RESTART WITH 10;
ALTER TABLE pe_pedido ALTER COLUMN pe_pedido_k RESTART WITH 10;
ALTER TABLE pi_pedido_item ALTER COLUMN pi_pedido_item_k RESTART WITH 10;
ALTER TABLE re_remito ALTER COLUMN re_remito_k RESTART WITH 10;
ALTER TABLE ri_remito_item ALTER COLUMN ri_remito_item_k RESTART WITH 10;
ALTER TABLE co_configuracion ALTER COLUMN co_configuracion_k RESTART WITH 10;
ALTER TABLE cr_configuracion_remito ALTER COLUMN cr_configuracion_remito_k RESTART WITH 10;
ALTER TABLE cg_catalogo ALTER COLUMN cg_catalogo_k RESTART WITH 10;
ALTER TABLE pd_pedido_a_distribuidora ALTER COLUMN pd_pedido_a_distribuidora_k RESTART WITH 10;