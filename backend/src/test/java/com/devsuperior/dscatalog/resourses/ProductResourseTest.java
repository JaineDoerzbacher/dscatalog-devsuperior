package com.devsuperior.dscatalog.resourses;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourseNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tests.Factory;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductResourse.class) // Para indicar que a classe é um teste de unidade web MVC e que o Spring Boot deve configurar o ambiente para testar o controlador
class ProductResourseTest {

    @Autowired
    private MockMvc mockMvc; // Para simular uma requisição web

    @MockBean // Para indicar que o objeto será um mock
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos para JSON e vice-versa

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private PageImpl<ProductDTO> page; // pageImpl é uma implementação de page para testes
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;

        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO)); //instancia uma page com um objeto product dentro
        when(service.findAllPaged(any())).thenReturn(page); //Quando chamar o findAllPaged com qualquer argumento, retorne a page que foi instanciada no setUp com o mock do service (page)

        when(service.update(eq(existingId), any())).thenReturn(productDTO); //Quando chamar o update com o existingId e o productDTO, retorne o productDTO que foi instanciado no setUp com o mock do service (productDTO)
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourseNotFoundException.class); // o eq é para comparar o argumento com o existingId e o nonExistingId e o any() é para comparar o argumento com o productDTO e o productDTO e o thenThrow é para lançar uma exceção

        when(service.findById(existingId)).thenReturn(productDTO);
        when(service.findById(nonExistingId)).thenThrow(ResourseNotFoundException.class);

        doNothing().when(service).delete(existingId);
        doThrow(ResourseNotFoundException.class).when(service).delete(nonExistingId);
        doThrow(DatabaseException.class).when(service).delete(dependentId);

        when(service.insert(any())).thenReturn(productDTO); //Quando chamar o insert com qualquer argumento, retorne o productDTO que foi instanciado no setUp com o mock do service (productDTO)

    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products")).andExpect(status().isOk()); // o get("/products") é a url que será testada e o andExpect(status().isOk()) é o que se espera que aconteça, nesse caso, que o status seja 200
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));


        result.andExpect(status().isOk()); // o get("/products/{id}", existingId) é a url que será testada e o andExpect(status().isOk()) é o que se espera que aconteça, nesse caso, que o status seja 200
        result.andExpect(jsonPath("$.id").exists()); // o get("/products/{id}", existingId) é a url que será testada e o andExpect(jsonPath("$.id").exists()) é o que se espera que aconteça, nesse caso, que o id exista
        result.andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

        ResultActions result = // ResultActions é o resultado da requisição que será testada (nesse caso, o get)
                mockMvc.perform(get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));       // o get("/products/{id}", nonExistingId) é a url que será testada e o andExpect(status().isNotFound()) é o que se espera que aconteça, nesse caso, que o status seja 404

        result.andExpect(status().isNotFound()); // o get("/products/{id}", nonExistingId) é a url que será testada e o andExpect(status().isNotFound()) é o que se espera que aconteça, nesse caso, que o status seja 404
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO); // Converte o objeto productDTO para JSON

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingId) // o put("/products/{id}", existingId) é a url que será testada o put tem corpo na requisição
                        .content(jsonBody) // O conteúdo da requisição é o jsonBody
                        .contentType(MediaType.APPLICATION_JSON) // O tipo de conteúdo da requisição é JSON
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO); // Converte o objeto productDTO para JSON

        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingId) // o put("/products/{id}", existingId) é a url que será testada o put tem corpo na requisição
                        .content(jsonBody) // O conteúdo da requisição é o jsonBody
                        .contentType(MediaType.APPLICATION_JSON) // O tipo de conteúdo da requisição é JSON
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnBadRequestWhenIdIsDependent() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", dependentId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
    }



    @Test
    public void insertShouldReturnProductDTOCreated() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO); // Converte o objeto productDTO para JSON

        ResultActions result =
                mockMvc.perform(post("/products") // o post("/products") é a url que será testada o post tem corpo na requisição
                        .content(jsonBody) // O conteúdo da requisição é o jsonBody
                        .contentType(MediaType.APPLICATION_JSON) // O tipo de conteúdo da requisição é JSON
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }
}