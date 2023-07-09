package api.sistema.bancario;

import api.sistema.bancario.entity.Customer;
import api.sistema.bancario.entity.CustomerTransaction;
import api.sistema.bancario.repository.CustomerRepository;
import api.sistema.bancario.repository.CustomerTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes=Application.class)
public class CustomerTransactionRepositoryTest {

    @Autowired
    private CustomerTransactionRepository customerTransactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void findCustomerTransactions() {
        Customer customer = customerRepository.findByAccount(199270232);
        Pageable filteredPagination = PageRequest.of(0, 10, Sort.by("date"));
        Page<CustomerTransaction> customerTransactions = customerTransactionRepository
                .getAllBetweenDates(new Date(), new Date(), customer, filteredPagination);
        assertEquals(2, customerTransactions.getTotalElements());
    }
}
