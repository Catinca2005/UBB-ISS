package org.example.weddingplanner.controller;

import jakarta.servlet.http.HttpSession;
import org.example.weddingplanner.model.Event;
import org.example.weddingplanner.model.User;
import org.example.weddingplanner.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsible for managing event-related routing and logic.
 */
@Controller
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private org.example.weddingplanner.repository.TaskRepository taskRepository;

    @Autowired
    private org.example.weddingplanner.repository.ShoppingItemRepository shoppingItemRepository;

    /**
     * Renders the main dashboard.
     * Fetches only the events belonging to the currently logged-in user.
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        // 1. Check if user is actually logged in
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Kick unauthorized users back to login
        }

        // 2. Fetch user's events from the database
        List<Event> userEvents = eventRepository.findAllByOrganizerId(loggedInUser.getId());

        // 3. Calculate spent budget for each event (Tasks + Shopping)
        java.util.Map<UUID, Double> spentMap = new java.util.HashMap<>();
        for (Event event : userEvents) {
            Double tasksSpent = taskRepository.sumEstimatedCostByEventId(event.getId());
            Double shoppingSpent = shoppingItemRepository.sumCostByEventId(event.getId());
            spentMap.put(event.getId(), tasksSpent + shoppingSpent);
        }

        // 4. Send data to the HTML template
        model.addAttribute("events", userEvents);
        model.addAttribute("spentMap", spentMap);
        model.addAttribute("user", loggedInUser);

        return "dashboard";
    }

    @GetMapping("/events/{id}/select")
    @ResponseBody
    public String selectEventForSession(@PathVariable UUID id, HttpSession session) {
        session.setAttribute("selectedEventId", id);
        return "success";
    }

    /**
     * Displays the form for creating a new event.
     */
    @GetMapping("/events/new")
    public String showNewEventForm(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "new-event";
    }

    /**
     * Processes the creation of a new event and ties it to the logged-in user.
     */
    @PostMapping("/events/new")
    public String processNewEvent(@ModelAttribute Event newEvent, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // Link the event to the current organizer
        newEvent.setOrganizer(loggedInUser);

        // Save to database
        eventRepository.save(newEvent);

        // Redirect back to dashboard to see the new event
        return "redirect:/dashboard";
    }
}