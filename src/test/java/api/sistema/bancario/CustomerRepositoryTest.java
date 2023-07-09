package api.sistema.bancario;

import api.sistema.bancario.entity.Customer;
import api.sistema.bancario.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes=Application.class)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void findById() {
        Customer customer = customerRepository.findById(13L).get();
        assertEquals(13L, customer.getId());
    }

    @Test
    public void findByAccount() {
        Customer customer = customerRepository.findByAccount(542373351);
        assertEquals(542373351, customer.getAccount());
    }
}
