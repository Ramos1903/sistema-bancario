package api.sistema.bancario.vo;

import api.sistema.bancario.entity.Customer;
import api.sistema.bancario.entity.CustomerTransaction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerTransactionVO {

    private static String DEPOSIT = "Depósito";

    private static String WITHDRAW = "Saque";
    private Long id;
    private Date date;
    private Double amount;
    private String operation;
    private CustomerVO customer;

    public CustomerTransactionVO(CustomerTransaction t) {
        if (t == null) return;
        this.id = t.getId();
        this.date = t.getDate();
        this.operation = t.getIsDeposit() ? DEPOSIT  : WITHDRAW;
        this.amount = t.getAmount();
        this.customer = new CustomerVO(t.getCustomer());
    }
}
