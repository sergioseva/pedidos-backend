package com.librosmario.pedidos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;

import com.librosmario.pedidos.dao.PedidoRepository;
import com.librosmario.pedidos.entity.Pedido;

@RepositoryRestController
public class PedidoController {
	
	private final PedidoRepository repository;
	
    @Autowired
    public PedidoController(PedidoRepository repo) { 
        repository = repo;
    }
    
   // @RequestMapping(method = GET, value = "/scanners/search/listProducers")
	
//    @Autowired
//    private PedidoRepository employeRepository;
//    
//    @Autowired
//    private PagedResourcesAssembler<Pedido> pagedAssembler;
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
