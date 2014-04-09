package com.issuetracker.search.search;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * //TODO: document this
 *
 * @author Jiří Holuša
 */
public class SearchManager {

    private Client client;

    public SearchManager(Client client) {
        this.client = client;
    }

    public <T> List<T> search(SearchResponse searchResponse, Class<T> clazz) {
        List<T> result = new ArrayList<T>();

        for(SearchHit hit: searchResponse.getHits()) {
            T entity = mapMapToEntity(hit.getSource(), clazz);
            result.add(entity);
        }

        return result;
    }

    public <T> T get(GetResponse getResponse, Class<T> clazz) {
        T result = mapMapToEntity(getResponse.getSource(), clazz);

        return result;
    }

    public Client getClient() {
        return client;
    }

    private <T> T mapMapToEntity(Map<String, Object> source, Class<T> clazz) {
        T result = null;
        try {
            result = clazz.newInstance();
        } catch (InstantiationException e) {
            //TODO: handle exception properly
        } catch (IllegalAccessException e) {
            //TODO: handle exception properly
        }

        for(Field field: clazz.getDeclaredFields()) {
            for(Annotation annotation: field.getDeclaredAnnotations()) {
                if(annotation instanceof com.issuetracker.search.indexing.annotations.Field &&
                        source.containsKey(field.getName())) {
                    field.setAccessible(true);
                    try {
                        field.set(result, source.get(field.getName()));
                    } catch (IllegalAccessException e) {
                        //TODO: handle exception properly
                    }
                }
            }
        }

        return result;
    }
}