package com.rivigo.riconet.core.dto;


import java.util.ArrayList;
import java.util.List;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractListConverter<T, V> implements ListConverter<T, V> {

    @Autowired protected Mapper mapper;

    public Iterable<V> convertListTo(Iterable<T> list) {

        if (null == list) {
            return null;
        }

        List<V> res = new ArrayList<>();
        for (T each : list) {
            V convertTo = convertTo(each);
            if (convertTo != null) {
                res.add(convertTo);
            }
        }
        return res;
    }

    public Iterable<T> convertListFrom(Iterable<V> list) {

        if (null == list) {
            return null;
        }

        List<T> res = new ArrayList<>();
        for (V each : list) {
            T convertFrom = convertFrom(each);
            if (convertFrom != null) {
                res.add(convertFrom);
            }
        }
        return res;
    }
}
