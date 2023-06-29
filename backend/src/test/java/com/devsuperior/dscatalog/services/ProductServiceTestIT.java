package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourseNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // o banco faça o rollback depois de cada teste
class ProductServiceTestIT {

    @Autowired
    private ProductService service; //como o teste é integrado usamos o auto wired para injetar a dependencia do service

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingId);
        assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourseNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIdDoesNotExist() {
        assertThrows(DatabaseException.class, () -> {
            service.delete(existingId);
        });
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPage0Size10() {

        PageRequest pageRequest = PageRequest.of(0, 10); // Instancia um objeto do tipo PageRequest com os parametros 0 e 10
        Page<ProductDTO> result = service.findAllPaged(pageRequest);
        Assertions.assertFalse(result.isEmpty()); // Verifica se a lista não está vazia
        Assertions.assertEquals(10, pageRequest.getPageSize()); // Verifica se a pagina é 0 (primeira pagina)  e se o tamanho é 10 (quantidade de elementos por pagina)
        Assertions.assertEquals(0, pageRequest.getPageNumber()); // getPageNumber() retorna o numero da pagina
        Assertions.assertEquals(countTotalProducts, result.getTotalElements()); // Verifica se o total de elementos é igual ao total de produtos
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {

        PageRequest pageRequest = PageRequest.of(50, 10); // Instancia um objeto do tipo PageRequest com os parametros 0 e 10
        Page<ProductDTO> result = service.findAllPaged(pageRequest);
        Assertions.assertTrue(result.isEmpty()); // Verifica se a lista não está vazia
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortByName() {

    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name")); // Instancia um objeto do tipo PageRequest com os parametros 0 e 10
        Page<ProductDTO> result = service.findAllPaged(pageRequest);
        Assertions.assertFalse(result.isEmpty()); // Verifica se a lista não está vazia
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName()); // Verifica se o primeiro elemento da lista é o Macbook Pro
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName()); // Verifica se o segundo elemento da lista é o PC Gamer
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName()); // Verifica se o terceiro elemento da lista é o PC Gamer Alfa
    }
}