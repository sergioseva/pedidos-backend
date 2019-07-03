package com.librosmario.pedidos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.librosmario.pedidos.dao.ClienteRepository;
import com.librosmario.pedidos.entity.Cliente;

@RepositoryRestController
public class ClienteController {
	
	private final ClienteRepository repository;
	
    @Autowired
    public ClienteController(ClienteRepository repo) { 
        repository = repo;
    }
    
    @GetMapping( value = "/clientes/search/findByCriteria")
    public @ResponseBody ResponseEntity<?> findByCriteria(@Param("libro") String libro) {
    	
    	List<Cliente> clientes=repository.findByNombreContainsAllIgnoreCase(libro);
    	
    	Resources<Cliente> resources = new Resources<Cliente>(clientes);
    	//ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllStudents());
    	//resources.add(linkTo(methodOn(ClienteController.class).findByCriteria()).withSelfRel());
    	
    	return ResponseEntity.ok(resources); 
    	
    }
//    @Autowired
//    private ClienteRepository employeRepository;
//    
//    @Autowired
//    private PagedResourcesAssembler<Cliente> pagedAssembler;
//    
//    @RequestMapping(value = "/employees/search/all/search/all", method = RequestMethod.GET)
//    public ResponseEntity<Resources<Resource<Employee>>> getEmployees(EmployeeCriteria filterCriteria, Pageable pageable) {
//
//        //EmployeeSpecification uses CriteriaAPI to form dynamic query with the fields from filterCriteria
//        Specification<Employee> specification = new EmployeeSpecification(filterCriteria);
//
//        Page<Employee> employees = employeeRepository.findAll(specification, pageable);
//        return assembler.toResource(employees);
//    }

}
