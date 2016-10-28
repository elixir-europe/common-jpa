package eu.crg.ega.microservice.repository;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;

import eu.crg.ega.microservice.helper.CommonQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

public class CustomQuerydslJpaRepositoryImpl<T, ID extends Serializable>
    extends QueryDslJpaRepository<T, ID> implements CustomQuerydslJpaRepository<T, ID> {

  private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER;
  private final EntityPath<T> path;
  private final PathBuilder<T> builder;
  private final Querydsl querydsl;

  public CustomQuerydslJpaRepositoryImpl(JpaEntityInformation<T, ID> entityInformation,
                                         EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.path = DEFAULT_ENTITY_PATH_RESOLVER.createPath(entityInformation.getJavaType());
    this.builder = new PathBuilder(this.path.getType(), this.path.getMetadata());
    this.querydsl = new Querydsl(entityManager, this.builder);
  }

  public Page<T> findAll(Predicate predicate, CommonQuery commonQuery) {
    JPQLQuery countQuery = createQuery(predicate);
    JPQLQuery query = null;
    QPageRequest pagination = null;
    if(commonQuery.getLimit() == 0 && commonQuery.getSkip() == 0) {
      query = createQuery(predicate);
    } else {
      pagination = new QPageRequest(commonQuery.getSkip(), commonQuery.getLimit());
      query = querydsl.applyPagination(pagination, createQuery(predicate));  
    }

    if (commonQuery.getSort() != null) {
      query = querydsl.applySorting(commonQuery.getSort(), query);
    }

    Long total = countQuery.count();
    List<T> content =
        pagination == null || total > pagination.getOffset() ? query.list(path) : Collections
            .<T>emptyList();

    return new PageImpl<T>(content, pagination, total);
  }

  public Page<T> findAll(CommonQuery commonQuery) {
    Page<T> page = null;
    if (commonQuery.getLimit() == 0 && commonQuery.getSkip() == 0) {
      // Return ALL results
      List<T> list = null;
      if(commonQuery.getSort() != null) {
        list = findAll(commonQuery.getSort());
      } else {
        list = findAll();
      }
      page = new PageImpl<T>(list);
    } else {
      page = findAll(null, commonQuery);
    }
    return page;
  }

  static {
    DEFAULT_ENTITY_PATH_RESOLVER = SimpleEntityPathResolver.INSTANCE;
  }

}