package com.librosmario.pedidos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.librosmario.pedidos.dao.ClienteRepository;
import com.librosmario.pedidos.entity.Cliente;


@Service
public class ClienteServiceImpl implements ClienteService {
	
	private ClienteRepository clienteRepository;
	
	@Autowired
	public ClienteServiceImpl(ClienteRepository cr) {
		clienteRepository = cr;
	}

	@Override
	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}

	@Override
	public Cliente findById(int theId) {
		
		Optional<Cliente> result = clienteRepository.findById(theId);
		
		Cliente c = null;
		
		if (result.isPresent()) {
			c = result.get();
		}
		else {
			// we didn't find the employee
			throw new RuntimeException("Did not find client id - " + theId);
		}
		
		return c;
	}

	@Override
	public void save(Cliente c) {

		clienteRepository.save(c);
	}

	@Override
	public void deleteById(int theId) {
		clienteRepository.deleteById(theId);

	}

	@Override
	public List<Cliente> searchBy(String theFirstName, String thePhone) {
		return clienteRepository.
				findByNombreContainsAndTelefonoMovilContainsAllIgnoreCase(
						theFirstName, thePhone);	
	}

}
