insert into users(id,name,username, email, password) values (1,'test','test','test@test.com','12345678');
insert into roles (id,name) values(1,'ADMIN');
insert into user_roles(user_id,role_id) values (1,1); 

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
		