package com.example.pcroom.controllertest;

import com.example.pcroom.application.ProductService;
import com.example.pcroom.domain.Product;
import com.example.pcroom.presentation.ProductResponseDto;
import com.example.pcroom.presentation.ProductSaveRequestDto;
import com.example.pcroom.presentation.controller.ProductController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("상품 save API 테스트")
    public void createProduct() throws Exception {
        ProductSaveRequestDto productSaveRequestDto = new ProductSaveRequestDto();
        productSaveRequestDto.setName("milk");
        productSaveRequestDto.setPrice(3000);
        productSaveRequestDto.setQuantity(20);

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(1L);
        productResponseDto.setName("milk");
        productResponseDto.setPrice(3000);
        productResponseDto.setQuantity(20);

        Mockito.when(productService.save(Mockito.any(ProductSaveRequestDto.class)))
                .thenReturn(productResponseDto);

        mockMvc.perform(post("/product")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productSaveRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("milk"))
                .andExpect(jsonPath("$.price").value(3000))
                .andExpect(jsonPath("$.quantity").value(20));


    }

    @Test
    @DisplayName("전체 상품 검색")
    public void findAllProducts() throws Exception {

        // Given

        ProductResponseDto productResponseDto1 = new ProductResponseDto();
        productResponseDto1.setId(1L);
        productResponseDto1.setName("milk");
        productResponseDto1.setPrice(3000);
        productResponseDto1.setQuantity(20);

        ProductResponseDto productResponseDto2 = new ProductResponseDto();
        productResponseDto2.setId(2L);
        productResponseDto2.setName("choco");
        productResponseDto2.setPrice(1000);
        productResponseDto2.setQuantity(100);

        List<ProductResponseDto> productResponseDtos = List.of(productResponseDto1, productResponseDto2);

        // When

        Mockito.when(productService.findAll()).thenReturn(productResponseDtos);

        // Then

        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("milk"))
                .andExpect(jsonPath("$[0].price").value(3000))
                .andExpect(jsonPath("$[0].quantity").value(20))
                .andExpect(jsonPath("$[1].name").value("choco"))
                .andExpect(jsonPath("$[1].price").value(1000))
                .andExpect(jsonPath("$[1].quantity").value(100));

    }
}
