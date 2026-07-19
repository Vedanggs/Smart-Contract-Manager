package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.services.ContactService;
import com.scm.services.UserService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    // get a single contact — only if it belongs to the logged-in user
    @GetMapping("/contacts/{contactId}")
    public ResponseEntity<Contact> getContact(@PathVariable String contactId,
            Authentication authentication) {

        User user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Contact contact;
        try {
            contact = contactService.getById(contactId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        // ownership check — prevents reading other users' contacts (IDOR)
        if (contact.getUser() == null || !contact.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(contact);
    }

}
