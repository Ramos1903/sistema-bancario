package api.sistema.bancario.entity;

import api.sistema.bancario.domain.request.CustomerOperationRequest;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "customer")
@Entity
@PrimaryKeyJoinColumn(name = "seq_customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "seq_customer", nullable = false)
    private Long id;

    @Column(name = "name")
    String name;

    @Column(name = "exclusive")
    Boolean isExclusive;

    @Column(name = "balance")
    Double balance;

    @Column(name = "account")
    Integer account;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    Date birthDate;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerTransaction> transactions;
}
