package api.sistema.bancario.vo;

import api.sistema.bancario.entity.Customer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerVO {
    private Long id;
    private String name;
    private Integer account;
    private Double balance;

    public CustomerVO(Customer c) {
        if (c == null) return;
        this.id = c.getId();
        this.name = c.getName();
        this.account = c.getAccount();
        this.balance = c.getBalance();
    }
}
