package org.example.weddingplanner.controller;

import jakarta.servlet.http.HttpSession;
import org.example.weddingplanner.model.Event;
import org.example.weddingplanner.model.User;
import org.example.weddingplanner.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Controller responsible for managing event-related routing and logic.
 */
@Controller
public class EventController {

    @Autowired
    private EventRepository eventRepository;

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

        // 3. Send data to the HTML template
        model.addAttribute("events", userEvents);
        model.addAttribute("user", loggedInUser);

        return "dashboard";
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