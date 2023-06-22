package com.devsuperior.dscatalog.resourses;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @PostMapping // Para indicar que o método responde a requisição do tipo POST do HTTP
    public ResponseEntity<CategoryDTO> insert(@RequestBody CategoryDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}") // Para retornar o código 201
        .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}") // Para indicar que o método responde a requisição do tipo PUT do HTTP
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto); // Para retornar a resposta com sucesso do HTTP
    }

    @DeleteMapping(value = "/{id}") // Para indicar que o método responde a requisição do tipo DELETE do HTTP
    public ResponseEntity<CategoryDTO> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // Para retornar a resposta com sucesso do HTTP
    }


}
