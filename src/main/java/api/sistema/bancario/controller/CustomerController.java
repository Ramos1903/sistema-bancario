package api.sistema.bancario.controller;

import api.sistema.bancario.domain.request.CustomerCreateAccountRequest;
import api.sistema.bancario.domain.request.CustomerOperationRequest;
import api.sistema.bancario.domain.request.CustomerSearchRequest;
import api.sistema.bancario.domain.request.CustomerTransactionSearchRequest;
import api.sistema.bancario.domain.response.CustomerResponse;
import api.sistema.bancario.service.CustomerService;
import api.sistema.bancario.vo.CustomerTransactionVO;
import api.sistema.bancario.vo.CustomerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RequestMapping(path = "/customer")
@RestController
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerCreateAccountRequest request) throws Exception {
        return new ResponseEntity<>(customerService.createCustomer(request),HttpStatus.CREATED);
    }

    @PutMapping("/deposit")
    public ResponseEntity<CustomerResponse> depositCustomerBalance(@RequestBody CustomerOperationRequest request) throws Exception {
        return new ResponseEntity<>(customerService.depositCustomerBalance(request), HttpStatus.OK);
    }

    @PutMapping("/withdraw")
    public ResponseEntity<CustomerResponse> withdrawCustomerBalance(@RequestBody CustomerOperationRequest request) throws Exception {
        return new ResponseEntity<>(customerService.withdrawCustomerBalance(request), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerVO>> customers(CustomerSearchRequest request) throws Exception {
        return new ResponseEntity<>(customerService.customers(request), HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<CustomerTransactionVO>> customerTransactionsByDate(CustomerTransactionSearchRequest request) throws Exception {
        return new ResponseEntity<>(customerService.customerTransactionsByDate(request), HttpStatus.OK);
    }
}
