package api.sistema.bancario.entity;

import lombok.Data;

import javax.persistence.*;
import java.lang.reflect.Type;
import java.util.Date;

@Data
@Entity
@Table(name = "customer_transaction")
@PrimaryKeyJoinColumn(name = "seq_customer_transaction")
public class CustomerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "seq_customer_transaction", nullable = false)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "creation_dt")
    private Date date;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "deposit")
    private Boolean isDeposit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seq_customer")
    private Customer customer;

}
