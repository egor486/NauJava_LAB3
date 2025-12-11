package ru.Dovgan_Egor.NauJava.EXCEPTION;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
