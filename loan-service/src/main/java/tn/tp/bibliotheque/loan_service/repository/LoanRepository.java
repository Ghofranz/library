package tn.tp.bibliotheque.loan_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.tp.bibliotheque.loan_service.entity.Loan;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByNomUtilisateur(String nomUtilisateur);

    List<Loan> findByStatus(Loan.LoanStatus status);

    List<Loan> findByBookId(Long bookId);

    Optional<Loan> findByBookIdAndStatus(Long bookId, Loan.LoanStatus status);
}