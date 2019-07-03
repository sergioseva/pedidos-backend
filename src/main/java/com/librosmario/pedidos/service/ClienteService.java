package com.librosmario.pedidos.service;

import java.util.List;

import com.librosmario.pedidos.entity.Cliente;

public interface ClienteService {
	
	public List<Cliente> findAll();
	
	public Cliente findById(int theId);
	
	public void save(Cliente theEmployee);
	
	public void deleteById(int theId);

	public List<Cliente> searchBy(String theFirstName, String thePhone);

}
