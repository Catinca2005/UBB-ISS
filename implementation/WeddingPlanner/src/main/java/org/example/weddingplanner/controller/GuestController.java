package org.example.weddingplanner.controller;

import jakarta.servlet.http.HttpSession;
import org.example.weddingplanner.model.Event;
import org.example.weddingplanner.model.Guest;
import org.example.weddingplanner.repository.EventRepository;
import org.example.weddingplanner.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class GuestController {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private EventRepository eventRepository;

    private final String UPLOAD_DIR = "uploads/guests/";

    @GetMapping("/events/{eventId}/guests")
    public String showGuestList(@PathVariable UUID eventId, HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return "redirect:/dashboard"; // Event not found
        }

        List<Guest> guests = guestRepository.findByEventId(eventId);
        
        // Fix 500 error: properly add attributes
        model.addAttribute("event", event);
        model.addAttribute("guests", guests);
        
        // Save selected event in session for global navbar navigation
        session.setAttribute("selectedEventId", eventId);
        
        return "guests";
    }

    // Endpoint for the global navbar "Invitati" button
    @GetMapping("/guests")
    public String showGuestsFromSession(HttpSession session) {
        UUID selectedEventId = (UUID) session.getAttribute("selectedEventId");
        if (selectedEventId != null) {
            return "redirect:/events/" + selectedEventId + "/guests";
        }
        return "redirect:/dashboard"; // No event selected yet
    }

    @PostMapping("/events/{eventId}/guests/new")
    public String addGuest(@PathVariable UUID eventId,
                           @ModelAttribute Guest newGuest,
                           @RequestParam(value = "photo", required = false) MultipartFile photo,
                           HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return "redirect:/dashboard";
        }

        newGuest.setEvent(event);

        if (photo != null && !photo.isEmpty()) {
            try {
                // Ensure directory exists
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate a unique filename to prevent collisions
                String originalFilename = StringUtils.cleanPath(photo.getOriginalFilename());
                String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

                // Save file locally
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Save only the filename in the DB
                newGuest.setPhotoFilename(uniqueFilename);
            } catch (IOException e) {
                e.printStackTrace();
                // We could add an error message to RedirectAttributes, but for now just skip photo
            }
        }

        // Apply default values if left empty
        if (newGuest.getDietaryPreferences() == null || newGuest.getDietaryPreferences().trim().isEmpty()) {
            newGuest.setDietaryPreferences("None");
        }
        if (newGuest.getRsvpStatus() == null || newGuest.getRsvpStatus().trim().isEmpty()) {
            newGuest.setRsvpStatus("Pending");
        }

        guestRepository.save(newGuest);

        return "redirect:/events/" + eventId + "/guests";
    }

    @GetMapping("/events/{eventId}/guests/{guestId}/edit")
    public String showEditGuestForm(@PathVariable UUID eventId, @PathVariable UUID guestId, HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        
        Event event = eventRepository.findById(eventId).orElse(null);
        Guest guest = guestRepository.findById(guestId).orElse(null);
        
        if (event == null || guest == null || !guest.getEvent().getId().equals(eventId)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("event", event);
        model.addAttribute("guest", guest);
        return "edit-guest";
    }

    @PostMapping("/events/{eventId}/guests/{guestId}/edit")
    public String updateGuest(@PathVariable UUID eventId,
                              @PathVariable UUID guestId,
                              @ModelAttribute Guest updatedGuest,
                              @RequestParam(value = "photo", required = false) MultipartFile photo,
                              HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Guest existingGuest = guestRepository.findById(guestId).orElse(null);
        if (existingGuest == null || !existingGuest.getEvent().getId().equals(eventId)) {
            return "redirect:/dashboard";
        }

        existingGuest.setName(updatedGuest.getName());
        existingGuest.setRsvpStatus(updatedGuest.getRsvpStatus());
        existingGuest.setDietaryPreferences(updatedGuest.getDietaryPreferences());

        if (photo != null && !photo.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFilename = StringUtils.cleanPath(photo.getOriginalFilename());
                String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                existingGuest.setPhotoFilename(uniqueFilename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (existingGuest.getDietaryPreferences() == null || existingGuest.getDietaryPreferences().trim().isEmpty()) {
            existingGuest.setDietaryPreferences("None");
        }
        if (existingGuest.getRsvpStatus() == null || existingGuest.getRsvpStatus().trim().isEmpty()) {
            existingGuest.setRsvpStatus("Pending");
        }

        guestRepository.save(existingGuest);
        return "redirect:/events/" + eventId + "/guests";
    }

    @PostMapping("/events/{eventId}/guests/{guestId}/delete")
    public String deleteGuest(@PathVariable UUID eventId, @PathVariable UUID guestId, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Guest guest = guestRepository.findById(guestId).orElse(null);
        if (guest != null && guest.getEvent().getId().equals(eventId)) {
            guestRepository.delete(guest);
        }

        return "redirect:/events/" + eventId + "/guests";
    }
}
