package com.example.todolist.dto;



public record TareaDTO(String titulo, String descripcion, boolean completada, long id) {

    public static TareaDTO of(String titulo, String descripcion) {
        return new TareaDTO(titulo, descripcion, false, System.currentTimeMillis());
    }
}