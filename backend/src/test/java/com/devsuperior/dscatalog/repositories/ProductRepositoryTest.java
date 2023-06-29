package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import tests.Factory;

import java.util.Optional;

@DataJpaTest
class ProductRepositoryTest {


    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;

    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception { // @BeforeEach é usado para sinalizar que o método a seguir deve ser executado antes de cada @Test, @RepeatedTest, @ParameterizedTest e @TestFactory no arquivo de teste atual.
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId); // Para salvar os dados
        repository.findById(existingId); // Para retornar os dados
        Optional<Product> result = repository.findById(existingId); // Para retornar os dados
        Assertions.assertFalse(result.isPresent()); // Para verificar se o objeto existe

    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> { //EmptyResultDataAccessException é uma exceção que é lançada quando uma operação de acesso a dados não encontra um resultado, quando o resultado esperado era um único resultado não nulo.
            repository.deleteById(nonExistingId); // Para salvar os dados
        });

    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {

        Product product = Factory.createProduct(); // Para instanciar um objeto
        product.setId(null); // Para instanciar um objeto e deixar o id nulo
        product = repository.save(product); // Para salvar os dados e deixar o id nulo (autoincrement)
        Assertions.assertNotNull(product.getId()); // Para verificar se o id está nulo
        Assertions.assertEquals(countTotalProducts + 1, product.getId()); // Para verificar countTotalProducts + 1 = 26 e product.getId() = 26 (autoincrement)
    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {

        Optional<Product> result = repository.findById(existingId); //passa como parâmetro o id existente
        Assertions.assertTrue(result.isPresent()); //isPresent() é usado para verificar se o objeto existe
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExists() {

        Optional<Product> result = repository.findById(nonExistingId); //passa como parâmetro o id não existente
        Assertions.assertTrue(result.isEmpty()); //isEmpty() é usado para verificar se o objeto está vazio
    }
}