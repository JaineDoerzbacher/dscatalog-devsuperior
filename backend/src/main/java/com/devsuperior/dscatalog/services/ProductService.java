package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;
    @Autowired
    private CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable) {
        Page<Product> list = repository.findAll(pageable);

        return list.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourseNotFoundException("Entity not found"));

        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product(); // Para instanciar um objeto
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity); // Para salvar os dados

        return new ProductDTO(entity); // Para retornar os dados
    }



    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getOne(id); // Para não ir no banco de dados desnecessariamente, pois o getOne() não vai no banco de dados e sim deixa o objeto monitorado pelo JPA para que depois possa efetuar uma operação com o banco de dados
            copyDtoToEntity(dto, entity); // Para atualizar os dados
            entity = repository.save(entity); // Para salvar os dados atualizados
            return new ProductDTO(entity); // Para retornar os dados atualizados
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
    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());
        entity.setPrice(dto.getPrice());

        entity.getCategories().clear(); // Para limpar a lista de categorias do produto
        for (CategoryDTO catDto : dto.getCategories()) { // Para cada elemento catDto da coleção categories do ProductDTO, será adicionado um novo Category na coleção categories do Product
            Category category = categoryRepository.getOne(catDto.getId()); // Para instanciar um objeto
            entity.getCategories().add(category); // Para adicionar o objeto na coleção categories do Product
        }
    }
}
