package org.example.weddingplanner.controller;

import jakarta.servlet.http.HttpSession;
import org.example.weddingplanner.model.Event;
import org.example.weddingplanner.model.Guest;
import org.example.weddingplanner.model.SeatingTable;
import org.example.weddingplanner.repository.EventRepository;
import org.example.weddingplanner.repository.GuestRepository;
import org.example.weddingplanner.repository.SeatingTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class SeatingController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private SeatingTableRepository seatingTableRepository;

    @GetMapping("/seating")
    public String showSeatingFromSession(HttpSession session) {
        UUID selectedEventId = (UUID) session.getAttribute("selectedEventId");
        if (selectedEventId != null) {
            return "redirect:/events/" + selectedEventId + "/seating";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/events/{eventId}/seating")
    public String showSeating(@PathVariable UUID eventId, HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return "redirect:/dashboard";

        List<Guest> allGuests = guestRepository.findByEventId(eventId);
        List<Guest> unseatedGuests = guestRepository.findByEventIdAndSeatingTableIsNull(eventId);
        List<SeatingTable> tables = seatingTableRepository.findByEventId(eventId);

        // Build a map: tableId -> list of guests seated at that table
        Map<UUID, List<Guest>> tableGuestsMap = new LinkedHashMap<>();
        Map<UUID, Long> tableOccupancyMap = new LinkedHashMap<>();
        for (SeatingTable table : tables) {
            List<Guest> seatedGuests = guestRepository.findBySeatingTableId(table.getId());
            tableGuestsMap.put(table.getId(), seatedGuests);
            tableOccupancyMap.put(table.getId(), (long) seatedGuests.size());
        }

        model.addAttribute("event", event);
        model.addAttribute("allGuests", allGuests);
        model.addAttribute("unseatedGuests", unseatedGuests);
        model.addAttribute("tables", tables);
        model.addAttribute("tableGuestsMap", tableGuestsMap);
        model.addAttribute("tableOccupancyMap", tableOccupancyMap);

        session.setAttribute("selectedEventId", eventId);

        return "seating";
    }

    @PostMapping("/events/{eventId}/seating/tables/new")
    public String addTable(@PathVariable UUID eventId, @RequestParam("maxSeats") Integer maxSeats, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return "redirect:/dashboard";

        long tableCount = seatingTableRepository.countByEventId(eventId);

        SeatingTable table = new SeatingTable();
        table.setTableName("Masa " + (tableCount + 1));
        table.setMaxSeats(maxSeats != null ? maxSeats : 10);
        table.setEvent(event);
        seatingTableRepository.save(table);

        return "redirect:/events/" + eventId + "/seating";
    }

    @PostMapping("/events/{eventId}/seating/assign")
    public String assignGuest(@PathVariable UUID eventId,
                              @RequestParam("guestId") UUID guestId,
                              @RequestParam("tableId") UUID tableId,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        SeatingTable table = seatingTableRepository.findById(tableId).orElse(null);
        Guest guest = guestRepository.findById(guestId).orElse(null);

        if (table == null || guest == null) {
            return "redirect:/events/" + eventId + "/seating";
        }

        // UC-6 Exception 6.0.E1: Check capacity
        long currentOccupancy = guestRepository.countBySeatingTableId(tableId);
        if (currentOccupancy >= table.getMaxSeats()) {
            redirectAttributes.addFlashAttribute("error", "Masa \"" + table.getTableName() + "\" este plină! Selectează altă masă.");
            return "redirect:/events/" + eventId + "/seating";
        }

        guest.setSeatingTable(table);
        guestRepository.save(guest);

        return "redirect:/events/" + eventId + "/seating";
    }

    @PostMapping("/events/{eventId}/seating/unassign")
    public String unassignGuest(@PathVariable UUID eventId,
                                @RequestParam("guestId") UUID guestId,
                                HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        Guest guest = guestRepository.findById(guestId).orElse(null);
        if (guest != null) {
            guest.setSeatingTable(null);
            guestRepository.save(guest);
        }

        return "redirect:/events/" + eventId + "/seating";
    }

    @PostMapping("/events/{eventId}/seating/tables/{tableId}/delete")
    public String deleteTable(@PathVariable UUID eventId, @PathVariable UUID tableId, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        // Unassign all guests from this table first
        List<Guest> seatedGuests = guestRepository.findBySeatingTableId(tableId);
        for (Guest g : seatedGuests) {
            g.setSeatingTable(null);
            guestRepository.save(g);
        }

        seatingTableRepository.deleteById(tableId);
        return "redirect:/events/" + eventId + "/seating";
    }
}
