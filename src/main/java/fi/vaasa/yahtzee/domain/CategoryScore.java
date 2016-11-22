package fi.vaasa.yahtzee.domain;

import java.util.Map;

public class CategoryScore implements Map.Entry<Category, Integer> {
	
    private Category key;
    private Integer value;

    public CategoryScore(Category key, Integer value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Category getKey() {
        return key;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public Integer setValue(Integer value) {
    	Integer old = this.value;
        this.value = value;
        return old;
    }
}