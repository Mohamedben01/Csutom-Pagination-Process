package com.ben.pagination.utils;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PagingRepositoryImpl <T, U extends SearchCriteria> implements PagingRepositoryCustom<T,U>{

    @Autowired
    protected PagingRepositoryHelper helper;

    @PersistenceContext
    protected EntityManager em;
    protected Class<T> entityClass;

    @PostConstruct
    protected abstract void setEntityClass();
    protected abstract Predicate getPredicate(U criteria, CriteriaBuilder cb, Root<T> entityRoot);

    @Override
    public Long count(U crit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);

        // FROM
        Root<T> entityRoot = q.from(entityClass);
        q.select(cb.count(entityRoot));
        // WHERE
        Predicate pred = getPredicate(crit, cb, entityRoot);
        q.where(pred);

        return em.createQuery(q).getSingleResult();
    }

    @Override
    public Page<T> search(U crit, Integer currentPage, Integer pageSize, String sortDirection, String sortedField) {

        // Total elements
        Long maxElement = count(crit);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(entityClass);

        // FROM
        Root<T> entityRoot = q.from(entityClass);
        q.select(entityRoot);
        // WHERE
        Predicate pred = getPredicate(crit, cb, entityRoot);
        q.where(pred);

        // ORDER
        if (sortDirection != null && sortedField != null) {
            Order order;
            if (sortedField.indexOf('.') > -1) {
                Join<Object, Object> join = entityRoot.join(sortedField.substring(0, sortedField.indexOf('.')));
                sortedField = sortedField.substring(sortedField.indexOf('.') + 1);
                order = "desc".equalsIgnoreCase(sortDirection) ? cb.desc(join.get(sortedField)) : cb.asc(join.get(sortedField));
            } else {
                order = "desc".equalsIgnoreCase(sortDirection) ? cb.desc(entityRoot.get(sortedField)) : cb.asc(entityRoot.get(sortedField));
            }
            q.orderBy(order);
        }

        TypedQuery<T> typedQuery = em.createQuery(q);
        // LIMIT
        if (currentPage != null && pageSize != null) {
            if (currentPage > maxElement / pageSize) currentPage = (int) Math.ceil(maxElement / pageSize);
            typedQuery.setFirstResult((currentPage) * pageSize);
            typedQuery.setMaxResults(pageSize);
        }
        List<T> resList = typedQuery.getResultStream().distinct().collect(Collectors.toList());

        // Construct the returning page
        Page<T> result;
        if (sortDirection != null && sortedField != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
            result = new PageImpl<>(resList, PageRequest.of(currentPage == null ? 0 : currentPage, pageSize == null ? 25 : pageSize, direction, sortedField), maxElement);
        } else {
            result = new PageImpl<>(resList, PageRequest.of(currentPage == null ? 0 : currentPage, pageSize == null ? 25 : pageSize), maxElement);
        }
        return result;
    }

    @Override
    public List<T> search(U crit, String sortDirection, String sortedField) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(entityClass);

        // FROM
        Root<T> root = q.from(entityClass);
        q.select(root);
        // WHERE
        Predicate pred = getPredicate(crit, cb, root);
        q.where(pred);

        // ORDER
        if (sortDirection != null && sortedField != null) {
            Order order;
            if (sortedField.indexOf('.') > -1) {
                Join<Object, Object> join = root.join(sortedField.substring(0, sortedField.indexOf('.')));
                sortedField = sortedField.substring(sortedField.indexOf('.') + 1);
                order = "desc".equalsIgnoreCase(sortDirection) ? cb.desc(join.get(sortedField)) : cb.asc(join.get(sortedField));
            } else {
                order = "desc".equalsIgnoreCase(sortDirection) ? cb.desc(root.get(sortedField)) : cb.asc(root.get(sortedField));
            }
            q.orderBy(order);
        }

        TypedQuery<T> typedQuery = em.createQuery(q);

        return typedQuery.getResultStream().distinct().collect(Collectors.toList());
    }
}
