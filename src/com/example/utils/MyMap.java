package com.example.utils;

import java.util.*;

public class MyMap<K, V> {
    // 创建一个线程安全的HashMap
    public Map<K, V> map = Collections.synchronizedMap(new HashMap<K, V>());
    // 根据指定的value来删除指定的项
    public synchronized void removeByValue(Object value) {
        for (Object key : map.keySet()) {
            if (map.get(key) == value) {
                map.remove(key);
                break;
            }
        }
    }
    // 获取所有value组成的Set
    public synchronized Set<V> valueSet() {
        Set<V> result = new HashSet<>();
        // 将Map中所有的value添加到result中
        map.forEach((key, value) -> result.add(value));
        return result;
    }

    // 获取所有key组成的Set
    public synchronized Set<K> keySet() {
        Set<K> result = new HashSet<>();
        // 将Map中所有的value添加到result中
        map.forEach((key, value) -> result.add(key));
        return result;
    }
    // 根据value查找value添加到result中
    public synchronized K getKeyByValue(V val ) {
        // 遍历所有key组成的集合
        for (K key : map.keySet()) {
            // 如果指定key对应的value与被搜索的value相同，则返回对应的key
            if (map.get(key) == val || map.get(key).equals(val)) {
                return key;
            }
        }
        return null;
    }
    // 实现put()方法不允许value重复
    public synchronized V put(K key, V value) {
        // 遍历所有的value组成的集合
        for (V val : valueSet()) {
            // 如果某个value与某个试图放入的value相同
            // 则抛出一个RuntimeException异常
            if (val.equals(value) && val.hashCode() == value.hashCode()) {
                throw new RuntimeException("MyMap实例中不能有重复的value");
            }
        }
        return map.put(key, value);
    }

}
