package eu.crg.ega.microservice.repository;

import com.mysema.query.types.Predicate;

import eu.crg.ega.microservice.helper.CommonQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface CustomQuerydslJpaRepository<T, ID extends Serializable>
    extends
    JpaRepository<T, ID>,
    QueryDslPredicateExecutor<T> {

  public Page<T> findAll(Predicate predicate, CommonQuery commonQuery);

  public Page<T> findAll(CommonQuery commonQuery);
}
