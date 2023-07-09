package api.sistema.bancario.service;

import api.sistema.bancario.domain.request.CustomerCreateAccountRequest;
import api.sistema.bancario.domain.request.CustomerOperationRequest;
import api.sistema.bancario.domain.request.CustomerSearchRequest;
import api.sistema.bancario.domain.request.CustomerTransactionSearchRequest;
import api.sistema.bancario.domain.response.CustomerResponse;
import api.sistema.bancario.entity.Customer;
import api.sistema.bancario.entity.CustomerTransaction;
import api.sistema.bancario.repository.CustomerRepository;
import api.sistema.bancario.repository.CustomerTransactionRepository;
import api.sistema.bancario.vo.CustomerTransactionVO;
import api.sistema.bancario.vo.CustomerVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class CustomerService {
    private static String CREATED_CUSTOMER_FEEDBACK ="Cliente Cadastrado com Sucesso!!";
    private static String SUCCESSFUL_CUSTOMER_DEPOSIT = "Depósito realizado com sucesso!!";
    private static String SUCCESSFUL_CUSTOMER_WITHDRAW = "Saque realizado com sucesso!!";
    private static String INTERNAL_ERROR = "Não foi possível realizar esta operação no momento, tente novamente mais tarde.";

    private static Integer PAGINATION_SIZE = 10;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerTransactionRepository customerTransactionRepository;

    public CustomerResponse createCustomer(CustomerCreateAccountRequest request) throws Exception {
        validateAccountCreationRequest(request);
        Customer customer = new Customer();
        try{
            passCustomerAttributes(request, customer);
            customerRepository.save(customer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(INTERNAL_ERROR);
        }
        return new CustomerResponse(CREATED_CUSTOMER_FEEDBACK, new CustomerVO(customer));
    }

    private void passCustomerAttributes(CustomerCreateAccountRequest request, Customer customer) {
        customer.setName(request.getName());
        customer.setBirthDate(request.getBirthDate());
        customer.setIsExclusive(request.getIsExclusive());
        customer.setBalance(0.0);
        customer.setTransactions(new ArrayList<>());
        customer.setAccount(validateNewAccount(generateCustomerAccount()));
    }

    private Integer generateCustomerAccount() {
        return new Random().nextInt(999999999);
    }

    private Integer validateNewAccount(Integer account) {
        Customer existingAccount = customerRepository.findByAccount(account);
        while (true) {
            generateCustomerAccount();
            if( existingAccount == null ) {
                break;
            }
        }
        return account;
    }

    private void validateAccountCreationRequest(CustomerCreateAccountRequest request) throws Exception {
        if(StringUtils.isBlank(request.getName())) {
            throw new Exception("Nome inválido.");
        }
        if(request.getBirthDate() == null) {
            throw new Exception("Data de aniversário inválida.");
        }
        if(request.getIsExclusive() == null) {
            throw new Exception("Tipo de conta inválida.");
        }
    }

    public CustomerResponse depositCustomerBalance(CustomerOperationRequest request) throws Exception {
        validateOperationRequest(request);
        Customer customer = new Customer();
        try {
            customer = customerRepository.findByAccount(request.getAccount());
            customer.setBalance(customer.getBalance() + request.getAmount());
            addNewTransaction(customer, request, true);
            customerRepository.save(customer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(INTERNAL_ERROR);
        }
        return new CustomerResponse(SUCCESSFUL_CUSTOMER_DEPOSIT, new CustomerVO(customer));
    }

    private void validateOperationRequest(CustomerOperationRequest request) throws Exception {
        if(request.getAccount() == null) {
            throw new Exception("Conta inválida");
        }
        if(request.getAmount() == null) {
            throw new Exception("Valor de operação inválido");
        }
    }

    public CustomerResponse withdrawCustomerBalance(CustomerOperationRequest request) throws Exception {
        validateOperationRequest(request);
        Customer customer = new Customer();
        try {
            customer = customerRepository.findByAccount(request.getAccount());
            customer.setBalance(applyWithdrawTax(customer.getBalance(), request.getAmount(), customer.getIsExclusive()));
            addNewTransaction(customer, request, false);
            customerRepository.save(customer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(INTERNAL_ERROR);
        }
        return new CustomerResponse(SUCCESSFUL_CUSTOMER_WITHDRAW, new CustomerVO(customer));
    }

    private void addNewTransaction(Customer customer, CustomerOperationRequest request, Boolean isDeposit) {
        if(customer.getTransactions() == null) {
            customer.setTransactions(new ArrayList<>());
        }
        CustomerTransaction transaction = passTransactionData(customer, request, isDeposit);
        customer.getTransactions().add(transaction);
    }

    private CustomerTransaction passTransactionData(Customer customer, CustomerOperationRequest request, Boolean isDeposit) {
        CustomerTransaction customerTransaction = new CustomerTransaction();
        customerTransaction.setCustomer(customer);
        customerTransaction.setDate(new Date());
        customerTransaction.setAmount(request.getAmount());
        customerTransaction.setIsDeposit(isDeposit);
        return  customerTransaction;
    }

    private Double applyWithdrawTax(Double balance, Double withdraw, Boolean isExclusive) throws Exception {
        if(isExclusive || withdraw <= 100.00) {
            return (balance - withdraw);
        } else if (withdraw > 100.00 && withdraw <= 300.00) {
            return balance - (withdraw + (withdraw * 0.004));
        } else if( withdraw > 300.00) {
            return balance - (withdraw + (withdraw*0.01));
        }
        throw new Exception(INTERNAL_ERROR);
    }

    public List<CustomerVO> customers(CustomerSearchRequest request) throws Exception {
        validateCustomerSearchRequest(request);
        try {
            Pageable pagination = PageRequest.of(request.getPage(), PAGINATION_SIZE, Sort.by("name"));
            Page<Customer> customers = customerRepository.findAll(pagination);
            return transformCustomerToVO(customers);
        }catch (Exception e) {
            e.printStackTrace();
            throw new Exception(INTERNAL_ERROR);
        }
    }
    private void validateCustomerSearchRequest(CustomerSearchRequest request) throws Exception {
        if(request.getPage() == null) {
            throw new Exception("Página inexistente");
        }
    }

    private List<CustomerVO> transformCustomerToVO(Page<Customer> customers) {
        List<CustomerVO> response = new ArrayList<>();
        if(!customers.isEmpty()) {
            for(Customer c : customers) {
                response.add(new CustomerVO(c));
            }
        }
        return response;
    }

    public List<CustomerTransactionVO> customerTransactionsByDate(CustomerTransactionSearchRequest request) throws Exception {
        validateCustomerTransactionRequest(request);
        try {
            Customer customer = customerRepository.findByAccount(request.getAccount());
            Pageable filteredPagination = PageRequest.of(request.getPage(), PAGINATION_SIZE, Sort.by("date"));
            Page<CustomerTransaction> transactions = customerTransactionRepository.getAllBetweenDates(convertUnixToDate(request.getInitialDate()), convertUnixToDate(request.getFinalDate()),
                    customer, filteredPagination);
            return transformTransactionToVO(transactions);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(INTERNAL_ERROR);
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

    private void validateCustomerTransactionRequest(CustomerTransactionSearchRequest request) throws Exception {
        if(request.getPage() == null) {
            throw new Exception("Página inexistente.");
        }
        if(request.getAccount() == null) {
            throw new Exception("Conta inválida.");
        }
        if(Objects.isNull(request.getInitialDate())) {
            throw new Exception("Data de início inválida.");
        }
        if(Objects.isNull(request.getFinalDate())) {
            throw new Exception("Data final inválida.");
        }
    }

    private List<CustomerTransactionVO> transformTransactionToVO(Page<CustomerTransaction> transactions) {
        List<CustomerTransactionVO> response = new ArrayList<>();
        if(!transactions.isEmpty()) {
            for(CustomerTransaction t : transactions) {
                response.add(new CustomerTransactionVO(t));
            }
        }
        return response;
    }
}
