package com.system.ladiesHealth.dao.specification;

import com.system.ladiesHealth.domain.po.UserPO;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public class UserSpecification implements Specification<UserPO> {

    private String username;
    private String email;
    private String phone;

    @Override
    public Predicate toPredicate(
            @Nullable Root<UserPO> root,
            @Nullable CriteriaQuery<?> query,
            @Nullable CriteriaBuilder criteriaBuilder) {
        if (root == null || query == null || criteriaBuilder == null) {
            return null;
        }
        List<Predicate> conditions = new ArrayList<>();
        if (username != null) {
            conditions.add(criteriaBuilder.equal(root.get("username"), username));
        }
        if (email != null) {
            conditions.add(criteriaBuilder.equal(root.get("email"), email));
        }
        if (phone != null) {
            conditions.add(criteriaBuilder.equal(root.get("phone"), phone));
        }
        conditions.add(criteriaBuilder.isNull(root.get("delTime")));

        return criteriaBuilder.and(conditions.toArray(new Predicate[0]));
    }
}
