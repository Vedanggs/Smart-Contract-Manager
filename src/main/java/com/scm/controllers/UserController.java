package com.scm.controllers;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    // user dashbaord page

    @RequestMapping(value = "/dashboard")
    public String userDashboard(Model model, Authentication authentication) {
        System.out.println("User dashboard");
        
        // Get logged in user
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        
        // Get all contacts for the user
        List<Contact> allContacts = contactService.getByUserId(user.getUserId());
        if (allContacts == null) {
            allContacts = Collections.emptyList();
        }
        
        // Calculate statistics
        int totalContacts = allContacts.size();
        int favoriteContacts = (int) allContacts.stream().filter(Contact::isFavorite).count();
        
        // For recent contacts, we'll just show the last 5 added contacts
        // (Since Contact entity doesn't have createdDate, we show random 5 for demo)
        int recentContacts = Math.min(totalContacts, 5);
        
        // Get up to 5 contacts for display
        List<Contact> recentContactList = allContacts.stream()
            .limit(5)
            .toList();
        
        // Add attributes to model
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favoriteContacts", favoriteContacts);
        model.addAttribute("recentContacts", recentContacts);
        model.addAttribute("recentContactList", recentContactList);
        
        return "user/dashboard";
    }

    // user profile page

    @RequestMapping(value = "/profile")
    public String userProfile(Model model, Authentication authentication) {
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        model.addAttribute("loggedInUser", user);
        return "user/profile";
    }

    // Update profile
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("about") String about,
            @RequestParam(value = "emailVerified", required = false, defaultValue = "false") boolean emailVerified,
            @RequestParam(value = "phoneVerified", required = false, defaultValue = "false") boolean phoneVerified,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            Authentication authentication,
            HttpSession session) {
        
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        
        // Check if email was changed - if so, reset verification
        boolean emailChanged = !email.equals(user.getEmail());
        boolean phoneChanged = !phoneNumber.equals(user.getPhoneNumber());
        
        // Update user fields
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setAbout(about);
        
        // Set verification status
        // If email/phone was changed, it should be unverified unless explicitly verified
        user.setEmailVerified(emailChanged ? false : emailVerified);
        user.setPhoneVerified(phoneChanged ? false : phoneVerified);
        
        // Handle profile photo upload
        boolean photoUploaded = false;
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                String filename = java.util.UUID.randomUUID().toString();
                String imageUrl = imageService.uploadImage(profilePhoto, filename);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    user.setProfilePic(imageUrl);
                    photoUploaded = true;
                    logger.info("Profile photo uploaded successfully: " + imageUrl);
                } else {
                    logger.error("Image upload returned null or empty URL");
                    session.setAttribute("message", Message.builder()
                            .content("Profile updated but photo upload failed. Please check Cloudinary configuration.")
                            .type(MessageType.red)
                            .build());
                    return "redirect:/user/profile";
                }
            } catch (Exception e) {
                logger.error("Error uploading profile photo: " + e.getMessage());
                session.setAttribute("message", Message.builder()
                        .content("Profile updated but photo upload failed: " + e.getMessage())
                        .type(MessageType.red)
                        .build());
                return "redirect:/user/profile";
            }
        }
        
        // Save updated user
        userService.updateUser(user);
        
        String successMessage = photoUploaded 
                ? "Profile updated successfully with new photo!" 
                : "Profile updated successfully!";
        
        session.setAttribute("message", Message.builder()
                .content(successMessage)
                .type(MessageType.green)
                .build());
        
        return "redirect:/user/profile";
    }

    // user add contacts page

    // user view contacts

    // user edit contact

    // user delete contact

    // Direct Message page
    @RequestMapping(value = "/direct-message")
    public String directMessage(Model model, Authentication authentication) {
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        
        // Get all contacts for the user to display as message recipients
        List<Contact> allContacts = contactService.getByUserId(user.getUserId());
        if (allContacts == null) {
            allContacts = Collections.emptyList();
        }
        
        model.addAttribute("contacts", allContacts);
        return "user/direct_message";
    }

    // Feedback page
    @RequestMapping(value = "/feedback")
    public String feedback() {
        return "user/feedback";
    }

}
