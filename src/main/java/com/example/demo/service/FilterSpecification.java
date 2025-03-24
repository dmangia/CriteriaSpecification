package com.example.demo.service;

import com.example.demo.dto.SearchRequestDto;
import org.hibernate.query.criteria.internal.predicate.InPredicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FilterSpecification<T> {


    public Specification<T> getSearchSpecification(SearchRequestDto searchRequestDto) {
        return new Specification<T>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(searchRequestDto.getColumn()), searchRequestDto.getValue());
            }
        };
    }

    public Specification<T> getSearchSpecificationList(List<SearchRequestDto> searchRequestDtos) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesAND = new ArrayList<>();
            List<Predicate> predicatesOR = new ArrayList<>();
            Predicate pred=null;
            for (SearchRequestDto requestDto : searchRequestDtos) {
                System.out.println(requestDto.getOperation());

                switch (requestDto.getOperation()) {
                    case EQUAL:
                        if (requestDto.getTypeCase()==SearchRequestDto.TypeCase.DATE)
                            pred = criteriaBuilder.equal(root.get(requestDto.getColumn()), LocalDate.parse(requestDto.getValue()));
                        else  pred = criteriaBuilder.equal(root.get(requestDto.getColumn()), requestDto.getValue());
                        if (requestDto.getOrCase()) predicatesOR.add(pred);
                        else predicatesAND.add(pred);
                        break;
                    case LIKE:
                        if (root.get(requestDto.getColumn()).getJavaType() == String.class) {
                            Predicate like = criteriaBuilder.like(root.get(requestDto.getColumn()),
                                    "%" + requestDto.getValue() + "%");
                            if (requestDto.getOrCase()) predicatesOR.add(like);
                            else predicatesAND.add(like);

                        } else {
                            throw new IllegalArgumentException("LIKE operator is only applicable to String values");
                        }
                        break;
                    case IN:
                        // assume input is string and separated by comma:- "name1,name2,name3"
                        String[] values = requestDto.getValue().split(",");
                        Predicate in = root.get(requestDto.getColumn()).in(Arrays.asList(values));
                        if (requestDto.getOrCase()) predicatesOR.add(in);
                        else predicatesAND.add(in);

                        break;
                    case GREATER_THAN:
                        if (requestDto.getTypeCase()==SearchRequestDto.TypeCase.DATE)
                            pred = criteriaBuilder.greaterThan(root.get(requestDto.getColumn()), LocalDate.parse(requestDto.getValue()));
                        else  pred = criteriaBuilder.greaterThan(root.get(requestDto.getColumn()), requestDto.getValue());
                        if (requestDto.getOrCase()) predicatesOR.add(pred);
                        else predicatesAND.add(pred);

                        break;
                    case GREATER_EQ_THAN:
                        if (requestDto.getTypeCase()==SearchRequestDto.TypeCase.DATE)
                            pred = criteriaBuilder.greaterThanOrEqualTo(root.get(requestDto.getColumn()), LocalDate.parse(requestDto.getValue()));
                        else  pred = criteriaBuilder.greaterThanOrEqualTo(root.get(requestDto.getColumn()), requestDto.getValue());
                        if (requestDto.getOrCase()) predicatesOR.add(pred);
                        else predicatesAND.add(pred);

                        break;
                    case LESS_EQ_THAN:
                        if (requestDto.getTypeCase()==SearchRequestDto.TypeCase.DATE)
                            pred = criteriaBuilder.lessThan(root.get(requestDto.getColumn()), LocalDate.parse(requestDto.getValue()));
                        else  pred = criteriaBuilder.lessThan(root.get(requestDto.getColumn()), requestDto.getValue());
                        if (requestDto.getOrCase()) predicatesOR.add(pred);
                        else predicatesAND.add(pred);

                        break;
                    case IS_NOT_NULL:
                        Predicate isNotNull = criteriaBuilder.isNotNull(root.get(requestDto.getColumn()));
                        if (requestDto.getOrCase()) predicatesOR.add(isNotNull);
                        else predicatesAND.add(isNotNull);

                        break;
                    case IS_NULL:
                        Predicate isNull = criteriaBuilder.isNull(root.get(requestDto.getColumn()));
                        if (requestDto.getOrCase()) predicatesOR.add(isNull);
                        else predicatesAND.add(isNull);

                        break;
                    case BETWEEN:   // si usa tra due valori numerici, tipo prezzo tra 10 e 30
                        String[] boundaryValues = requestDto.getValue().split(",");
                        Predicate between = null;
                        if (requestDto.getTypeCase()==SearchRequestDto.TypeCase.DATE) between =
                                criteriaBuilder.between(root.get(requestDto.getColumn()), LocalDate.parse(boundaryValues[0]),
                                        LocalDate.parse(boundaryValues[1]));
                        else between =
                                criteriaBuilder.between(root.get(requestDto.getColumn()),
                                        Integer.parseInt(boundaryValues[0]), Integer.parseInt(boundaryValues[1]));
                        if (requestDto.getOrCase()) predicatesOR.add(between);
                        else predicatesAND.add(between);

                        break;
                    case JOIN:
                        Predicate join = criteriaBuilder.equal(root.join(requestDto.getJoinTable()).get(requestDto.getColumn()), requestDto.getValue());
                        predicatesAND.add(join);

                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected Value for Operation: " + requestDto.getOperation());
                }
            }

            if (predicatesOR.size() > 0) predicatesAND.add(criteriaBuilder.or(predicatesOR.toArray(new Predicate[0])));

            Predicate finalAndPredicate = criteriaBuilder.and(predicatesAND.toArray(new Predicate[0]));

            return finalAndPredicate;
        };

    }


}
