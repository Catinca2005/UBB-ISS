package org.example.weddingplanner.controller;

import org.example.weddingplanner.model.User;
import org.example.weddingplanner.repository.UserRepository;
import org.example.weddingplanner.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

/**
 * Controller responsible for handling user authentication routes.
 */
@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    /**
     * Processes the registration form submission.
     * Validates passwords, applies hashing, and persists the new user.
     *
     * @param user The user entity populated with form data.
     * @param confirmPassword The confirmation password to check against.
     * @return A redirect string indicating success or failure.
     */
    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user, @RequestParam String confirmPassword) {

        // 1. Validate that both passwords match
        if (!user.getPassword().equals(confirmPassword)) {
            return "redirect:/register?error=passwordsMismatch";
        }

        // 2. Security Check: Hash the password before database insertion
        String hashedPassword = SecurityUtils.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        // 3. Persist the user in the SQLite database via the Repository
        userRepository.save(user);

        // 4. Redirect the user to the login page upon successful creation
        return "redirect:/login?success=accountCreated";
    }

    /**
     * Processes the login form submission.
     * Uses HttpSession to maintain the user's logged-in state across the platform.
     */
    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {

        User user = userRepository.findByEmail(email);

        if (user != null) {
            String hashedInputPassword = SecurityUtils.hashPassword(password);

            if (user.getPassword().equals(hashedInputPassword)) {
                // IMPORTANT: Save the user in the session (server memory)
                session.setAttribute("loggedInUser", user);
                return "redirect:/dashboard";
            }
        }

        return "redirect:/login?error=invalidCredentials";
    }
}