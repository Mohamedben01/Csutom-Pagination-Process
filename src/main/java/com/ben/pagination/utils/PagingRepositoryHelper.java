package com.ben.pagination.utils;

import com.ben.pagination.criteria.StudentCriteria;
import com.ben.pagination.entity.Student;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@Service
public class PagingRepositoryHelper {


    public <T extends SearchCriteria> Predicate addSimpleLike(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, String> getter) {
        if (getter.apply(criteria) != null) {
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                return cb.and(pred, cb.like(cb.lower(enumPath.get(attrs[1]).as(String.class)),
                        surroundPercent(getter.apply(criteria).toLowerCase())));
            } else {
                return cb.and(pred, cb.like(cb.lower(rootEntity.get(attributeName).as(String.class)),
                        surroundPercent(getter.apply(criteria).toLowerCase())));
            }
        }
        return pred;
    }


    public <T extends SearchCriteria> Predicate addSimpleLikeOnTwoColumns(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String firstAtributeName, String secondAttributeName, Function<T, String> getter) {
        if (getter.apply(criteria) != null) {
            Predicate or = cb.conjunction();
            return cb.and(pred, cb.or(addSimpleLike(or, cb, rootEntity, criteria, firstAtributeName, getter),
                    addSimpleLike(or, cb, rootEntity, criteria, secondAttributeName, getter)));
        }
        return pred;
    }

    public Predicate addSimpleEqualsNull(Predicate pred, CriteriaBuilder cb, Root rootEntity, String attributeName) {
        String[] attrs = attributeName.split("/");
        if (attrs.length == 2) {
            Path enumPath = rootEntity.get(attrs[0]);
            return cb.and(pred, cb.isNull(enumPath.get(attrs[1]).as(String.class)));
        } else {
            return cb.and(pred, cb.isNull(rootEntity.get(attributeName).as(String.class)));
        }
    }


    public Predicate addSimpleEqual(Predicate pred, CriteriaBuilder cb, Root rootEntity, String attributeName, String value) {
        if (value != null) {
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                return cb.and(pred, cb.equal(enumPath.get(attrs[1]).as(String.class),
                        cb.literal(value)));
            } else {
                return cb.and(pred, cb.equal(rootEntity.get(attributeName).as(String.class),
                        cb.literal(value)));
            }
        }
        return pred;
    }

    public <T extends SearchCriteria> Predicate addSimpleIntegerEqual(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, Integer> getter) {
        if (getter.apply(criteria) != null) {
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                return cb.and(pred, cb.equal(enumPath.get(attrs[1]).as(Integer.class),
                        getter.apply(criteria)));
            } else {
                return cb.and(pred, cb.equal(rootEntity.get(attributeName).as(Integer.class),
                        getter.apply(criteria)));
            }
        }
        return pred;
    }

    /**
     * <p>Build a predicate from a string representing a boolean and apply a equals on a Boolean field.</p>
     * <em>The criteria value can be : 'true', 'on', 'y', 't' or 'yes' which will be considered as true otherwise false</em>.
     * <p>The entity field must be a <code>java.lang.Boolean</code></p>
     *
     * @see java.lang.Boolean
     * @see org.apache.commons.lang3.BooleanUtils
     */
    public <T extends SearchCriteria> Predicate addBooleanEqual(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, String> getter) {
        String criteriaValStr = getter.apply(criteria);
        if (criteriaValStr != null) {
            Boolean criteriaValBool = toBooleanObject(criteriaValStr);
            if(criteriaValBool==null){
                return pred;
            }
            String[] attrs = attributeName.split("/");
            Expression fieldExp;
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                fieldExp = enumPath.get(attrs[1]).as(Boolean.class);
            } else {
                fieldExp = rootEntity.get(attributeName).as(Boolean.class);
            }

            return cb.and(pred, cb.equal(fieldExp, cb.literal(criteriaValBool)));
        }
        return pred;
    }

    public Predicate addSimpleNotEqual(Predicate pred, CriteriaBuilder cb, Root rootEntity, String attributeName, String value) {
        if (value != null) {
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                return cb.and(pred, cb.notEqual(enumPath.get(attrs[1]).as(String.class),
                        cb.literal(value)));
            } else {
                return cb.and(pred, cb.notEqual(rootEntity.get(attributeName).as(String.class),
                        cb.literal(value)));
            }
        }
        return pred;
    }

    public <T extends SearchCriteria> Predicate addSimpleDateLike(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, String> getter) {
        if (getter.apply(criteria) != null) {
            Expression<String> vehicleRegistrationExpDate;
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                vehicleRegistrationExpDate = cb.function(
                        "DATE_FORMAT",
                        String.class,
                        enumPath.get(attrs[1]),
                        cb.literal("%d/%m/%Y")
                );
            } else {
                vehicleRegistrationExpDate = cb.function(
                        "DATE_FORMAT",
                        String.class,
                        rootEntity.get(attributeName),
                        cb.literal("%d/%m/%Y")
                );
            }
            return cb.and(pred, cb.like(vehicleRegistrationExpDate, surroundPercent(getter.apply(criteria))));
        }
        return pred;
    }

    public <T extends SearchCriteria> Predicate addSimpleLocalDateTimeLike(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, String> getter) {
        if (getter.apply(criteria) != null) {
            Expression<String> vehicleRegistrationExpDate;
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                vehicleRegistrationExpDate = cb.function(
                        "DATE_FORMAT",
                        String.class,
                        enumPath.get(attrs[1]),
                        cb.literal("%d/%m/%Y %r")
                );
            } else {
                vehicleRegistrationExpDate = cb.function(
                        "DATE_FORMAT",
                        String.class,
                        rootEntity.get(attributeName),
                        cb.literal("%d/%m/%Y %r")
                );
            }
            return cb.and(pred, cb.like(vehicleRegistrationExpDate, surroundPercent(getter.apply(criteria))));
        }
        return pred;
    }

    public <T extends SearchCriteria> Predicate addSimpleLocalDateBeforeOrEqual(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, LocalDate> getter) {
        if (getter.apply(criteria) != null) {
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                return cb.and(pred,  cb.lessThanOrEqualTo(enumPath.get(attrs[1]), getter.apply(criteria)));
            } else {
                return cb.and(pred, cb.lessThanOrEqualTo(rootEntity.get(attributeName), getter.apply(criteria)));
            }
        }
        return pred;
    }

    public <T extends SearchCriteria> Predicate addSimpleLocalDateAfterOrEqual(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, LocalDate> getter) {
        if (getter.apply(criteria) != null) {
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                return cb.and(pred,  cb.greaterThanOrEqualTo(enumPath.get(attrs[1]), getter.apply(criteria)));
            } else {
                return cb.and(pred, cb.greaterThanOrEqualTo(rootEntity.get(attributeName), getter.apply(criteria)));
            }
        }
        return pred;
    }

    public <T extends SearchCriteria> Predicate addSimpleLocalDateBetween(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, LocalDate> getterBefore, Function<T, LocalDate> getterAfter) {
        if (getterBefore.apply(criteria) != null || getterAfter.apply(criteria) != null) {
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                if(getterBefore.apply(criteria) != null && getterAfter.apply(criteria) != null) {
                    return cb.and(pred, cb.between(enumPath.get(attrs[1]), getterBefore.apply(criteria), getterAfter.apply(criteria)));
                } else if (getterBefore.apply(criteria) != null) {
                    return cb.and(pred, cb.greaterThanOrEqualTo(enumPath.get(attrs[1]), getterBefore.apply(criteria)));
                } else {
                    return cb.and(pred, cb.lessThanOrEqualTo(enumPath.get(attrs[1]), getterAfter.apply(criteria)));
                }
            } else {
                if(getterBefore.apply(criteria) != null && getterAfter.apply(criteria) != null) {
                    return cb.and(pred, cb.between(rootEntity.get(attributeName), getterBefore.apply(criteria), getterAfter.apply(criteria)));
                } else if (getterBefore.apply(criteria) != null) {
                    return cb.and(pred, cb.greaterThanOrEqualTo(rootEntity.get(attributeName), getterBefore.apply(criteria)));
                } else {
                    return cb.and(pred, cb.lessThanOrEqualTo(rootEntity.get(attributeName), getterAfter.apply(criteria)));
                }
            }
        }
        return pred;
    }

    public <T extends SearchCriteria> Predicate addSimpleLocalDateTimeBetween(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, LocalDateTime> getterBefore, Function<T, LocalDateTime> getterAfter) {
        if (getterBefore.apply(criteria) != null || getterAfter.apply(criteria) != null) {
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                if(getterBefore.apply(criteria) != null && getterAfter.apply(criteria) != null) {
                    return cb.and(pred, cb.between(enumPath.get(attrs[1]), getterBefore.apply(criteria), getterAfter.apply(criteria)));
                } else if (getterBefore.apply(criteria) != null) {
                    return cb.and(pred, cb.greaterThanOrEqualTo(enumPath.get(attrs[1]), getterBefore.apply(criteria)));
                } else {
                    return cb.and(pred, cb.lessThanOrEqualTo(enumPath.get(attrs[1]), getterAfter.apply(criteria)));
                }
            } else {
                if(getterBefore.apply(criteria) != null && getterAfter.apply(criteria) != null) {
                    return cb.and(pred, cb.between(rootEntity.get(attributeName), getterBefore.apply(criteria), getterAfter.apply(criteria)));
                } else if (getterBefore.apply(criteria) != null) {
                    return cb.and(pred, cb.greaterThanOrEqualTo(rootEntity.get(attributeName), getterBefore.apply(criteria)));
                } else {
                    return cb.and(pred, cb.lessThanOrEqualTo(rootEntity.get(attributeName), getterAfter.apply(criteria)));
                }
            }
        }
        return pred;
    }

    public <T extends SearchCriteria> Predicate addSimpleListIn(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, Collection> getter) {
        if (getter.apply(criteria) != null && !getter.apply(criteria).isEmpty()) {
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.join(attrs[0]);
                return cb.and(pred, enumPath.get(attrs[1]).in(getter.apply(criteria)));
            } else {
                return cb.and(pred, rootEntity.get(attributeName).in(getter.apply(criteria)));
            }
        } else if (getter.apply(criteria) != null) {
            return cb.isTrue(cb.literal(false));
        }
        return pred;
    }

    public <T extends SearchCriteria> Predicate addSimpleListInOrNull(Predicate pred, CriteriaBuilder cb, Root rootEntity, T criteria, String attributeName, Function<T, Collection> getter) {
        if (getter.apply(criteria) != null) {
            return cb.and(pred, cb.or(addSimpleListIn(pred, cb, rootEntity, criteria, attributeName, getter),
                    cb.isNull(rootEntity.get(attributeName).as(String.class))));
        }
        return pred;
    }

    public String surroundPercent(String root) {
        return '%' + root + '%';
    }

    public Predicate applyExternalCheck(Predicate pred, CriteriaBuilder cb, Root rootEntity, String attributeName, List<Integer> ids) {
        if (ids.isEmpty()) {
            return cb.or(); // It's a false literal in criteria query... yes it is...
        } else {
            String[] attrs = attributeName.split("/");
            if (attrs.length == 2) {
                Path enumPath = rootEntity.get(attrs[0]);
                return cb.and(pred, enumPath.get(attrs[1]).in(ids));
            } else {
                return cb.and(pred, rootEntity.get(attributeName).in(ids));
            }
        }
    }

    // String to Boolean methods
    //-----------------------------------------------------------------------

    /**
     * <p>Converts a String to a Boolean. Rewrite of org.apache.commons.lang3.BooleanUtils.toBoolean() method</p>
     *
     * <p>{@code 'true'}, {@code 'on'}, {@code 'y'}, {@code 't'} or {@code 'yes'} or {@code '1'}
     * (case insensitive) will return {@code true}.
     * {@code 'false'}, {@code 'off'}, {@code 'n'}, {@code 'f'} or {@code 'no'} or {@code '0'}
     * (case insensitive) will return {@code false}.
     * Otherwise, {@code null} is returned.</p>
     *
     * <p>NOTE: This returns null and will throw a NullPointerException if autoboxed to a boolean. </p>
     *
     * <pre>
     *   // N.B. case is not significant
     *   BooleanUtils.toBooleanObject(null)    = null
     *   BooleanUtils.toBooleanObject("true")  = Boolean.TRUE
     *   BooleanUtils.toBooleanObject("T")     = Boolean.TRUE // i.e. T[RUE]
     *   BooleanUtils.toBooleanObject("false") = Boolean.FALSE
     *   BooleanUtils.toBooleanObject("f")     = Boolean.FALSE // i.e. f[alse]
     *   BooleanUtils.toBooleanObject("No")    = Boolean.FALSE
     *   BooleanUtils.toBooleanObject("n")     = Boolean.FALSE // i.e. n[o]
     *   BooleanUtils.toBooleanObject("on")    = Boolean.TRUE
     *   BooleanUtils.toBooleanObject("ON")    = Boolean.TRUE
     *   BooleanUtils.toBooleanObject("off")   = Boolean.FALSE
     *   BooleanUtils.toBooleanObject("oFf")   = Boolean.FALSE
     *   BooleanUtils.toBooleanObject("yes")   = Boolean.TRUE
     *   BooleanUtils.toBooleanObject("Y")     = Boolean.TRUE // i.e. Y[ES]
     *   BooleanUtils.toBooleanObject("1")     = Boolean.TRUE
     *   BooleanUtils.toBooleanObject("0")     = Boolean.FALSE
     *   BooleanUtils.toBooleanObject("blue")  = null
     *   BooleanUtils.toBooleanObject("true ") = null // trailing space (too long)
     *   BooleanUtils.toBooleanObject("ono")   = null // does not match on or no
     * </pre>
     *
     * @param str  the String to check; upper and lower case are treated as the same
     * @return the Boolean value of the string, {@code null} if no match or {@code null} input
     */
    public Boolean toBooleanObject(final String str) {
        // Previously used equalsIgnoreCase, which was fast for interned 'true'.
        // Non interned 'true' matched 15 times slower.
        //
        // Optimisation provides same performance as before for interned 'true'.
        // Similar performance for null, 'false', and other strings not length 2/3/4.
        // 'true'/'TRUE' match 4 times slower, 'tRUE'/'True' 7 times slower.
        if ("true".equals(str)) {
            return Boolean.TRUE;
        }
        if (str == null) {
            return null;
        }
        switch (str.length()) {
            case 1: {
                final char ch0 = str.charAt(0);
                if (ch0 == 'y' || ch0 == 'Y' ||
                        ch0 == 't' || ch0 == 'T'
                        || ch0 == '1') {
                    return Boolean.TRUE;
                }
                if (ch0 == 'n' || ch0 == 'N' ||
                        ch0 == 'f' || ch0 == 'F'
                        || ch0 == '0') {
                    return Boolean.FALSE;
                }
                break;
            }
            case 2: {
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                if ((ch0 == 'o' || ch0 == 'O') &&
                        (ch1 == 'n' || ch1 == 'N') ) {
                    return Boolean.TRUE;
                }
                if ((ch0 == 'n' || ch0 == 'N') &&
                        (ch1 == 'o' || ch1 == 'O') ) {
                    return Boolean.FALSE;
                }
                break;
            }
            case 3: {
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char ch2 = str.charAt(2);
                if ((ch0 == 'y' || ch0 == 'Y') &&
                        (ch1 == 'e' || ch1 == 'E') &&
                        (ch2 == 's' || ch2 == 'S') ) {
                    return Boolean.TRUE;
                }
                if ((ch0 == 'o' || ch0 == 'O') &&
                        (ch1 == 'f' || ch1 == 'F') &&
                        (ch2 == 'f' || ch2 == 'F') ) {
                    return Boolean.FALSE;
                }
                break;
            }
            case 4: {
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char ch2 = str.charAt(2);
                final char ch3 = str.charAt(3);
                if ((ch0 == 't' || ch0 == 'T') &&
                        (ch1 == 'r' || ch1 == 'R') &&
                        (ch2 == 'u' || ch2 == 'U') &&
                        (ch3 == 'e' || ch3 == 'E') ) {
                    return Boolean.TRUE;
                }
                break;
            }
            case 5: {
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char ch2 = str.charAt(2);
                final char ch3 = str.charAt(3);
                final char ch4 = str.charAt(4);
                if ((ch0 == 'f' || ch0 == 'F') &&
                        (ch1 == 'a' || ch1 == 'A') &&
                        (ch2 == 'l' || ch2 == 'L') &&
                        (ch3 == 's' || ch3 == 'S') &&
                        (ch4 == 'e' || ch4 == 'E') ) {
                    return Boolean.FALSE;
                }
                break;
            }
            default:
                break;
        }

        return null;
    }

}
