package ru.jd6team7.cooperatproject1.exceptions;

public class GuardsListIsEmptyException extends RuntimeException{
    public GuardsListIsEmptyException(String message) {
        super(message);
    }
}
