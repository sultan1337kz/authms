package com.authms.examples;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// URL Shortener Service
class URLShortener {
    private static final String BASE_HOST = "http://short.ly/";
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final ConcurrentHashMap<String, String> shortToLong = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> longToShort = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    // Generate short URL
    public String shortenURL(String longURL) {
        if (longToShort.containsKey(longURL)) {
            return BASE_HOST + longToShort.get(longURL);
        }
        
        long id = counter.getAndIncrement();
        String shortKey = encodeBase62(id);
        shortToLong.put(shortKey, longURL);
        longToShort.put(longURL, shortKey);
        
        return BASE_HOST + shortKey;
    }

    // Retrieve original URL
    public String getOriginalURL(String shortURL) {
        String shortKey = shortURL.replace(BASE_HOST, "");
        return shortToLong.getOrDefault(shortKey, "URL not found");
    }

    // Base62 Encoding
    private String encodeBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(BASE62.charAt((int) (num % 62)));
            num /= 62;
        }
        return sb.reverse().toString();
    }
}

// Example Usage
public class URLShortenerMain {
    public static void main(String[] args) {
        URLShortener shortener = new URLShortener();
        
        String longURL = "https://www.example.com/some/very/long/url";
        String shortURL = shortener.shortenURL(longURL);
        
        System.out.println("Short URL: " + shortURL);
        System.out.println("Original URL: " + shortener.getOriginalURL(shortURL));
    }
}
