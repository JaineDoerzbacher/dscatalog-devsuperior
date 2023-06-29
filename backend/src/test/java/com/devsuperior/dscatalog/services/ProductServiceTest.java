package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourseNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tests.Factory;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService service; // não usa o autowired, usa o injectmocks para injetar a dependência  (service) no teste
    @Mock
    private ProductRepository repository; //usa o mock para simular o repository e não usar o banco de dados

    @Mock
    CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private PageImpl<Product> page; // pageImpl é uma implementação de page para testes

    private Product product;
    private Category category;
    ProductDTO productDTO;
    private long dependentId;

    @BeforeEach
    void setUp() throws Exception { // @BeforeEach é usado para sinalizar que o método a seguir deve ser executado antes de cada @Test, @RepeatedTest, @ParameterizedTest e @TestFactory no arquivo de teste atual.
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        product = Factory.createProduct();
        productDTO = Factory.createProductDTO();
        category = Factory.createCategory();
        page = new PageImpl<>(List.of(new Product())); //instancia uma page com um objeto product dentro
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page); //quando o método findAll for chamado com qualquer argumento, retorne a page que foi instanciada no setUp com o mock do repository (page)
        Mockito.doNothing().when(repository).deleteById(existingId); //quando o método deleteById for chamado com o existingId como parametro, não faça nada
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product); //quando o método save for chamado com qualquer argumento, retorne o product que foi instanciado no setUp com o mock do repository (product)

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product)); //quando o método findAllById for chamado com o existingId como parametro, retorne o product que foi instanciado no setUp com o mock do repository (product)
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty()); //quando o método findAllById for chamado com o nonExistingId como parametro, retorne um optional vazio

        Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category); //quando o método getOne for chamado com o existingId como parametro, retorne o product que foi instanciado no setUp com o mock do repository (product)
        Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(ResourseNotFoundException.class);//quando o método getOne for chamado com o existingId como parametro, retorne  Mockito.when(repository.getOne(existingId)).thenReturn(product); //quando o método getOne for chamado com o existingId como parametro, retorne o product que foi instanciado no setUp com o mock do repository (product)

        Mockito.when(repository.getOne(existingId)).thenReturn(product);//quando o método getOne for chamado com o existingId como parametro, retorne o product que foi instanciado no setUp com o mock do repository (product)
        Mockito.when(repository.getOne(nonExistingId)).thenThrow(ResourseNotFoundException.class);//quando o método getOne for chamado com o existingId como parametro, retorne  Mockito.when(repository.getOne(existingId)).thenReturn(product); //quando o método getOne for chamado com o existingId como parametro, retorne o product que foi instanciado no setUp com o mock do repository (product)

        Mockito.doThrow(ResourseNotFoundException.class).when(repository).deleteById(nonExistingId); //quando o método deleteById for chamado com o nonExistingId como parametro, lance a exceção EmptyResultDataAccessException.class
        Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentId); //quando o método deleteById for chamado com o dependentId como parametro, lance a exceção DataIntegrityViolationException.class
    }


    @Test
    public void deleteShoulDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> { //assertDoesNotThrow é usado para verificar se o método deleteById não lança uma exceção EmptyResultDataAccessException.class, se não lançar, o teste passa
            service.delete(existingId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId); //verifica se o método deleteById foi chamado uma vez com o existingId como parametro
        //mockito.times(1) é usado para verificar se o método deleteById foi chamado uma vez
    }

    @Test
    public void deleteShouldDatabaseExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(DatabaseException.class, () -> { //assertThrows é usado para verificar se o método deleteById lança uma exceção EmptyResultDataAccessException.class, se lançar, o teste passa
            service.delete(dependentId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId); //verifica se o método deleteById foi chamado uma vez com o nonExistingId como parametro
    }

    @Test
    public void deleteShouldThrowResourseNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourseNotFoundException.class, () -> { //assertThrows é usado para verificar se o método deleteById lança uma exceção EmptyResultDataAccessException.class, se lançar, o teste passa
            service.delete(nonExistingId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId); //verifica se o método deleteById foi chamado uma vez com o nonExistingId como parametro
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10); //instancia um pageable com os parametros 0 e 10
        Page<ProductDTO> result = service.findAllPaged(pageable); //chama o método findAllPaged com o pageable como parametro

        Assertions.assertNotNull(result); //verifica se o result não é nulo
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable); //verifica se o método findAll foi chamado uma vez com o pageable como parametro

    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.findById(existingId); //chama o método findById com o existingId como parametro

        Assertions.assertNotNull(result); //verifica se o result não é nulo
        Mockito.verify(repository, Mockito.times(1)).findById(existingId); //verifica se o método findById foi chamado uma vez com o existingId como parametro
    }

    @Test
    public void findByIdShouldThrowResourseNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourseNotFoundException.class, () -> { //assertThrows é usado para verificar se o método findById lança uma exceção ResourseNotFoundException.class, se lançar, o teste passa
            service.findById(nonExistingId);
        });
        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId); //verifica se o método findById foi chamado uma vez com o nonExistingId como parametro
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {

        ProductDTO result = service.update(existingId, productDTO); //chama o método update com o existingId e o productDTO como parametro
        Assertions.assertNotNull(result); //verifica se o result não é nulo
        Mockito.verify(repository, Mockito.times(1)).save(product); //verifica se o método save foi chamado uma vez com o product como parametro
    }

    @Test
    public void updateShouldThrowResourseNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourseNotFoundException.class, () -> { //assertThrows é usado para verificar se o método update lança uma exceção ResourseNotFoundException.class, se lançar, o teste passa
            service.update(nonExistingId, productDTO);
        });
        Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId); //verifica se o método getOne foi chamado uma vez com o nonExistingId como parametro

    }
}