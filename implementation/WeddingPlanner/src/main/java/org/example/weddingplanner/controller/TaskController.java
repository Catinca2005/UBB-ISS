package org.example.weddingplanner.controller;

import jakarta.servlet.http.HttpSession;
import org.example.weddingplanner.model.Event;
import org.example.weddingplanner.model.Task;
import org.example.weddingplanner.repository.EventRepository;
import org.example.weddingplanner.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/tasks")
    public String showTasksFromSession(HttpSession session) {
        UUID selectedEventId = (UUID) session.getAttribute("selectedEventId");
        if (selectedEventId != null) {
            return "redirect:/events/" + selectedEventId + "/tasks";
        }
        return "redirect:/dashboard"; // No event selected yet
    }

    @GetMapping("/events/{eventId}/tasks")
    public String showTaskBoard(@PathVariable UUID eventId, HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return "redirect:/dashboard";
        }

        List<Task> tasks = taskRepository.findByEventId(eventId);
        Double totalSpent = taskRepository.sumEstimatedCostByEventId(eventId);

        model.addAttribute("event", event);
        model.addAttribute("tasks", tasks);
        model.addAttribute("totalSpent", totalSpent);
        
        session.setAttribute("selectedEventId", eventId); // Keep context

        return "tasks";
    }

    @PostMapping("/events/{eventId}/tasks/new")
    public String addTask(@PathVariable UUID eventId, @ModelAttribute Task newTask, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event != null) {
            if (newTask.getEstimatedCost() == null || newTask.getEstimatedCost() < 0) {
                newTask.setEstimatedCost(0.0);
            }
            newTask.setEvent(event);
            newTask.setStatus("Pending");
            taskRepository.save(newTask);
        }

        return "redirect:/events/" + eventId + "/tasks";
    }

    @PostMapping("/events/{eventId}/tasks/{taskId}/complete")
    public String completeTask(@PathVariable UUID eventId, @PathVariable UUID taskId, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null && task.getEvent().getId().equals(eventId)) {
            task.setStatus("Completed");
            taskRepository.save(task);
        }

        return "redirect:/events/" + eventId + "/tasks";
    }

    @PostMapping("/events/{eventId}/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable UUID eventId, @PathVariable UUID taskId, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null && task.getEvent().getId().equals(eventId)) {
            taskRepository.delete(task);
        }

        return "redirect:/events/" + eventId + "/tasks";
    }
}
