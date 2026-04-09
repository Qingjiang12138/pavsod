package com.pavsod.pavsodbackend.utils;

public class CurrentHolder {
    private static final ThreadLocal<Long> CURRENT_LOCAL = new ThreadLocal<>();

    public static void setCurrentLocal(Long userId){
        CURRENT_LOCAL.set(userId);
    }

    public static Long getCurrentId(){
        return CURRENT_LOCAL.get();
    }

    public static void remove(){
        CURRENT_LOCAL.remove();
    }
}
