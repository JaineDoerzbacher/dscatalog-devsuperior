package com.devsuperior.dscatalog.resourses;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController // Para indicar que a classe é um recurso web implementado por um controlador REST
@RequestMapping(value = "/users") // Para indicar o caminho do recurso
public class UserResourse {

    @Autowired
    private UserService service;

    @GetMapping // Para indicar que o método responde a requisição do tipo GET do HTTP
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) { // Para indicar o parâmetro da requisição)

        Page<UserDTO> list = service.findAllPaged(pageable); // Para acessar o serviço

        return ResponseEntity.ok().body(list); // Para retornar a resposta com sucesso do HTTP
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findbyId(@PathVariable Long id) {

        UserDTO dto = service.findById(id); // Para acessar o serviço
        return ResponseEntity.ok().body(dto); // Para retornar a resposta com sucesso do HTTP
    }

    @PostMapping // Para indicar que o método responde a requisição do tipo POST do HTTP
    public ResponseEntity<UserDTO> insert(@RequestBody UserInsertDTO dto) {
        UserDTO newDto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}") // Para retornar o código 201
                .buildAndExpand(newDto.getId()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PutMapping(value = "/{id}") // Para indicar que o método responde a requisição do tipo PUT do HTTP
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto); // Para retornar a resposta com sucesso do HTTP
    }

    @DeleteMapping(value = "/{id}") // Para indicar que o método responde a requisição do tipo DELETE do HTTP
    public ResponseEntity<UserDTO> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // Para retornar a resposta com sucesso do HTTP
    }


}
