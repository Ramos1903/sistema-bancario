package api.sistema.bancario.repository;

import api.sistema.bancario.entity.Customer;
import api.sistema.bancario.entity.CustomerTransaction;
import api.sistema.bancario.vo.CustomerVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

    @Query(" SELECT c FROM Customer c WHERE c.account = :account ")
    Customer findByAccount(@Param("account") Integer account);

}
