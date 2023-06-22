package com.devsuperior.dscatalog.resourses;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Para indicar que a classe é um recurso web implementado por um controlador REST
@RequestMapping(value = "/categories") // Para indicar o caminho do recurso
public class CategoryResourse {

    @Autowired
    private CategoryService service;
    @GetMapping // Para indicar que o método responde a requisição do tipo GET do HTTP
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> list = service.findAll(); // Para acessar o serviço
        return ResponseEntity.ok().body(list); // Para retornar a resposta com sucesso do HTTP
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> findbyId(@PathVariable  Long id) {

        CategoryDTO dto = service.findById(id); // Para acessar o serviço
        return ResponseEntity.ok().body(dto); // Para retornar a resposta com sucesso do HTTP
    }



}
