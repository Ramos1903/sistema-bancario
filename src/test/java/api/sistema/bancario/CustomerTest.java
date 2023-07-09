package api.sistema.bancario;

import api.sistema.bancario.controller.CustomerController;
import api.sistema.bancario.domain.response.CustomerResponse;
import api.sistema.bancario.repository.CustomerRepository;
import api.sistema.bancario.service.CustomerService;
import api.sistema.bancario.vo.CustomerTransactionVO;
import api.sistema.bancario.vo.CustomerVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes=Application.class)
public class CustomerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerController customerController;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void create() throws Exception {
        //Given
        String url = "/customer/create";
        String requestBody = "{\n" +
                "    \"name\":\"Souza\",\n" +
                "    \"birthDate\": 1665944615,\n" +
                "    \"isExclusive\": false\n" +
                "}";
        //When
        MvcResult result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestBody.getBytes())).andReturn();
        //Then
        assertNotNull(result.getResponse());
        assertEquals(201, result.getResponse().getStatus());
        try {
            CustomerResponse customerResponse = new ObjectMapper().readValue(result.getResponse().getContentAsString(), CustomerResponse.class);
            assertEquals("Cliente Cadastrado com Sucesso!!", customerResponse.getFeedback());
            assertEquals("Souza", customerResponse.getCustomer().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deposit() throws Exception {
        //Given
        String url = "/customer/deposit";
        String requestBody = "{\n" +
                "    \"account\":542373351,\n" +
                "    \"amount\": 100.00\n" +
                "}";
        //When
        MvcResult result = mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(requestBody.getBytes())).andReturn();
        //Then
        assertNotNull(result.getResponse());
        assertEquals(200, result.getResponse().getStatus());
        try {
            CustomerResponse customerResponse = new ObjectMapper().readValue(result.getResponse().getContentAsString(), CustomerResponse.class);
            assertEquals("Depósito realizado com sucesso!!", customerResponse.getFeedback());
            assertEquals(542373351, customerResponse.getCustomer().getAccount());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void withdraw() throws Exception {
        //Given
        String url = "/customer/withdraw";
        String requestBody = "{\n" +
                "    \"account\":542373351,\n" +
                "    \"amount\": 150.00\n" +
                "}";
        //When
        MvcResult result = mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(requestBody.getBytes())).andReturn();
        //Then
        assertNotNull(result.getResponse());
        assertEquals(200, result.getResponse().getStatus());
        try {
            CustomerResponse customerResponse = new ObjectMapper().readValue(result.getResponse().getContentAsString(), CustomerResponse.class);
            assertEquals("Saque realizado com sucesso!!", customerResponse.getFeedback());
            assertEquals(542373351, customerResponse.getCustomer().getAccount());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void search() throws Exception {
        //Given
        String url = "/customer/search?page=0";
        //When
        MvcResult result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andReturn();
        //Then
        assertNotNull(result.getResponse());
        assertEquals(200, result.getResponse().getStatus());
        try {
            List<CustomerVO> customers = new ObjectMapper().readValue(result.getResponse().getContentAsString(), new TypeReference<List<CustomerVO>>(){});
            assertEquals(new CustomerVO(66L, "Bianca", 466806862, 0.0), customers.stream()
                    .filter(customerVO -> customerVO.getAccount().equals(466806862)).findFirst().orElse(null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void transactions() throws Exception {
        //Given
        String url = "/customer/transactions?page=0&initialDate=1666021709&finalDate=1666021709&account=199270232";
        //When
        MvcResult result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andReturn();
        //Then
        assertNotNull(result.getResponse());
        assertEquals(200, result.getResponse().getStatus());
        try {
            List<CustomerTransactionVO> transactions = new ObjectMapper().readValue(result.getResponse().getContentAsString(), new TypeReference<List<CustomerTransactionVO>>(){});
            assertEquals(new CustomerTransactionVO(147L, convertUnixToDate(1665975600L), 100.0, "Depósito",
                    new CustomerVO(11L, "Amanda", 199270232, -50.599999999999994)), transactions.stream()
                    .filter(customerTransactionVO -> customerTransactionVO.getId().equals(147L)).findFirst().orElse(null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Date convertUnixToDate(long longUnix) {
        try {
            Instant instant = Instant.ofEpochSecond( longUnix );
            return Date.from( instant );
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
