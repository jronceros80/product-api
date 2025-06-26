package com.products.infrastructure.dto;

public record PageInfo(
        int size,
        int number,
        long totalElements,
        int totalPages) {
    
    public boolean first() {
        return number == 0;
    }

    public boolean last() {
        return number == totalPages - 1;
    }

    public boolean hasNext() {
        return number < totalPages - 1;
    }

    public boolean hasPrevious() {
        return number > 0;
    }
    
    public boolean isFirst() {
        return first();
    }

    public boolean isLast() {
        return last();
    }

    public boolean isHasNext() {
        return hasNext();
    }

    public boolean isHasPrevious() {
        return hasPrevious();
    }

    public int getSize() {
        return size();
    }

    public int getNumber() {
        return number();
    }

    public long getTotalElements() {
        return totalElements();
    }

    public int getTotalPages() {
        return totalPages();
    }
} 