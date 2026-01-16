package com.example.todolist.controller;

import com.example.todolist.dto.TareaDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todo")
public class TodoController {

    @SuppressWarnings("unchecked")
    @PostMapping("/agregar")
    public ResponseEntity<String> agregarTarea(
            HttpSession session,
            @RequestParam String titulo,
            @RequestParam String descripcion) {


        // El SupressWarnings es para evitar la advertencia de conversión insegura al recuperar la lista de tareas de la sesión
        List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");
        if (tareas == null) {
            tareas = new ArrayList<>();
        }

        tareas.add(TareaDTO.of(titulo, descripcion));
        session.setAttribute("tareas", tareas);

        return ResponseEntity.ok("✅ Tarea agregada. Total tareas: " + tareas.size());
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/listar")
    public ResponseEntity<?> verTareas(HttpSession session) {
        List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");

        if (tareas == null || tareas.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "No hay tareas pendientes",
                    "tareas", List.of()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "tareas", tareas,
                "total", tareas.size()
        ));
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/completar/{id}")
    public ResponseEntity<String> completarTarea(HttpSession session, @PathVariable long id) {
        List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");

        if (tareas == null) {
            return ResponseEntity.badRequest().body("❌ No hay tareas");
        }

        for (int i = 0; i < tareas.size(); i++) {
            TareaDTO tarea = tareas.get(i);
            if (tarea.id() == id) {
                TareaDTO completada = new TareaDTO(
                        tarea.titulo(),
                        tarea.descripcion(),
                        true,
                        tarea.id()
                );
                tareas.set(i, completada);
                session.setAttribute("tareas", tareas);
                return ResponseEntity.ok("✅ Tarea completada");
            }
        }

        return ResponseEntity.notFound().build();
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarTarea(HttpSession session, @PathVariable long id) {
        List<TareaDTO> tareas = (List<TareaDTO>) session.getAttribute("tareas");

        if (tareas == null) {
            return ResponseEntity.badRequest().body("❌ No hay tareas");
        }

        tareas.removeIf(t -> t.id() == id);
        session.setAttribute("tareas", tareas);

        return ResponseEntity.ok("🗑️ Tarea eliminada");
    }

    @DeleteMapping("/limpiar")
    public ResponseEntity<String> limpiarTareas(HttpSession session) {
        session.removeAttribute("tareas");
        return ResponseEntity.ok("🗑️ Lista de tareas limpiada");
    }
}