package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;


    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(Pageable pageable) {
        Page<Category> list = repository.findAll(pageable);

        return list.map(CategoryDTO::new);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourseNotFoundException("Entity not found"));

        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category(); // Para instanciar um objeto
        entity.setName(dto.getName()); // Para inserir os dados
        entity = repository.save(entity); // Para salvar os dados

        return new CategoryDTO(entity); // Para retornar os dados
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = repository.getOne(id); // Para não ir no banco de dados desnecessariamente, pois o getOne() não vai no banco de dados e sim deixa o objeto monitorado pelo JPA para que depois possa efetuar uma operação com o banco de dados
            entity.setName(dto.getName()); // Para atualizar os dados
            entity = repository.save(entity); // Para salvar os dados atualizados
            return new CategoryDTO(entity); // Para retornar os dados atualizados
        } catch (EntityNotFoundException e) { // Para capturar o erro caso o id não exista
            throw new ResourseNotFoundException("Id not found " + id); // Para retornar o erro 404
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) { // Para capturar o erro caso o id não exista
            throw new ResourseNotFoundException("Id not found " + id); // Para retornar o erro 404
        } catch (DataIntegrityViolationException e) { // Para capturar o erro caso o id esteja sendo usado por outra tabela
            throw new DatabaseException("Integrity violation");
        }
    }
}
