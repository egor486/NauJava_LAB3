package ru.Dovgan_Egor.NauJava.EXCEPTION_PCK;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
