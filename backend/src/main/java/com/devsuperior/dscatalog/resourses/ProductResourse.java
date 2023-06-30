package com.devsuperior.dscatalog.resourses;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;


@RestController // Para indicar que a classe é um recurso web implementado por um controlador REST
@RequestMapping(value = "/products") // Para indicar o caminho do recurso
public class ProductResourse {

    @Autowired
    private ProductService service;

    @GetMapping // Para indicar que o método responde a requisição do tipo GET do HTTP
    public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable) { // Para indicar o parâmetro da requisição)

        Page<ProductDTO> list = service.findAllPaged(pageable); // Para acessar o serviço

        return ResponseEntity.ok().body(list); // Para retornar a resposta com sucesso do HTTP
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findbyId(@PathVariable Long id) {

        ProductDTO dto = service.findById(id); // Para acessar o serviço
        return ResponseEntity.ok().body(dto); // Para retornar a resposta com sucesso do HTTP
    }

    @PostMapping // Para indicar que o método responde a requisição do tipo POST do HTTP
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}") // Para retornar o código 201
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}") // Para indicar que o método responde a requisição do tipo PUT do HTTP
    public ResponseEntity<ProductDTO> update(@Valid @PathVariable Long id, @RequestBody ProductDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto); // Para retornar a resposta com sucesso do HTTP
    }

    @DeleteMapping(value = "/{id}") // Para indicar que o método responde a requisição do tipo DELETE do HTTP
    public ResponseEntity<ProductDTO> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // Para retornar a resposta com sucesso do HTTP
    }


}
