package org.example.weddingplanner.controller;

import jakarta.servlet.http.HttpSession;
import org.example.weddingplanner.model.Event;
import org.example.weddingplanner.model.ShoppingItem;
import org.example.weddingplanner.repository.EventRepository;
import org.example.weddingplanner.repository.ShoppingItemRepository;
import org.example.weddingplanner.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class BudgetController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ShoppingItemRepository shoppingItemRepository;

    @GetMapping("/budget")
    public String showBudgetFromSession(HttpSession session) {
        UUID selectedEventId = (UUID) session.getAttribute("selectedEventId");
        if (selectedEventId != null) {
            return "redirect:/events/" + selectedEventId + "/budget";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/events/{eventId}/budget")
    public String showBudgetBoard(@PathVariable UUID eventId, HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return "redirect:/dashboard";
        }

        List<ShoppingItem> items = shoppingItemRepository.findByEventId(eventId);
        Double tasksSpent = taskRepository.sumEstimatedCostByEventId(eventId);
        Double shoppingSpent = shoppingItemRepository.sumCostByEventId(eventId);
        Double totalSpent = tasksSpent + shoppingSpent;
        
        Double totalBudget = event.getTotalBudget() != null ? event.getTotalBudget() : 0.0;
        Double remaining = Math.max(0, totalBudget - totalSpent);

        model.addAttribute("event", event);
        model.addAttribute("items", items);
        model.addAttribute("tasksSpent", tasksSpent);
        model.addAttribute("shoppingSpent", shoppingSpent);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("remaining", remaining);

        session.setAttribute("selectedEventId", eventId);

        return "budget";
    }

    @PostMapping("/events/{eventId}/budget/update")
    public String updateBudget(@PathVariable UUID eventId, @RequestParam("totalBudget") Double newBudget, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event != null) {
            event.setTotalBudget(newBudget != null ? newBudget : 0.0);
            eventRepository.save(event);
        }
        return "redirect:/events/" + eventId + "/budget";
    }

    @PostMapping("/events/{eventId}/budget/items/new")
    public String addShoppingItem(@PathVariable UUID eventId, @ModelAttribute ShoppingItem newItem, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event != null) {
            if (newItem.getCost() == null || newItem.getCost() < 0) {
                newItem.setCost(0.0);
            }
            newItem.setEvent(event);
            newItem.setIsPurchased(false);
            shoppingItemRepository.save(newItem);
        }
        return "redirect:/events/" + eventId + "/budget";
    }

    @PostMapping("/events/{eventId}/budget/items/{itemId}/toggle")
    public String toggleItemPurchased(@PathVariable UUID eventId, @PathVariable UUID itemId, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        ShoppingItem item = shoppingItemRepository.findById(itemId).orElse(null);
        if (item != null && item.getEvent().getId().equals(eventId)) {
            item.setIsPurchased(!item.getIsPurchased());
            shoppingItemRepository.save(item);
        }
        return "redirect:/events/" + eventId + "/budget";
    }

    @PostMapping("/events/{eventId}/budget/items/{itemId}/delete")
    public String deleteShoppingItem(@PathVariable UUID eventId, @PathVariable UUID itemId, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        ShoppingItem item = shoppingItemRepository.findById(itemId).orElse(null);
        if (item != null && item.getEvent().getId().equals(eventId)) {
            shoppingItemRepository.delete(item);
        }
        return "redirect:/events/" + eventId + "/budget";
    }
}
