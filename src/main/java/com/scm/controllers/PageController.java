package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private com.scm.helpers.Helper helper;

    @Autowired
    private com.scm.services.EmailService emailService;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        System.out.println("Home page handler");
        // sending data to view
        model.addAttribute("name", "Substring Technologies");
        model.addAttribute("youtubeChannel", "Learn Code With Durgesh");
        model.addAttribute("githubRepo", "https://github.com/learncodewithdurgesh/");
        return "home";
    }

    // about route

    @RequestMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        System.out.println("About page loading");
        return "about";
    }

    // services

    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("services page loading");
        return "services";
    }

    // contact page

    @GetMapping("/contact")
    public String contact() {
        return new String("contact");
    }

    // this is showing login page
    @GetMapping("/login")
    public String login() {
        return new String("login");
    }

    // registration page
    @GetMapping("/register")
    public String register(Model model) {

        UserForm userForm = new UserForm();
        // default data bhi daal sakte hai
        // userForm.setName("Durgesh");
        // userForm.setAbout("This is about : Write something about yourself");
        model.addAttribute("userForm", userForm);

        return "register";
    }

    // processing register

    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult,
            HttpSession session) {
        System.out.println("Processing registration");
        // fetch form data
        // UserForm
        System.out.println(userForm);

        // validate form data
        if (rBindingResult.hasErrors()) {
            return "register";
        }

        String email = userForm.getEmail().trim();

        // reject an email that is already registered
        if (userService.isUserExistByEmail(email)) {
            rBindingResult.rejectValue("email", "email.exists",
                    "This email is already registered. Please login instead.");
            return "register";
        }

        // reject fake emails: the domain must actually be able to receive mail
        if (!isRealEmailDomain(email)) {
            rBindingResult.rejectValue("email", "email.unreachable",
                    "Please use a real email address — this domain cannot receive mail.");
            return "register";
        }

        // UserForm--> User
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(email);
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        // No stock photo: profilePic stays null so a name-based avatar is shown
        // until the user uploads their own photo from the profile page.

        User savedUser = userService.saveUser(user);

        // Send the verification email. The account is created disabled and only
        // activates when the user clicks this link (handled by AuthController).
        try {
            String link = helper.getLinkForEmailVerificatiton(savedUser.getEmailToken());
            String body = "Hello " + savedUser.getName() + ",\n\n"
                    + "Thanks for signing up for Smart Contact Manager.\n"
                    + "Please verify your email address by clicking the link below:\n\n"
                    + link + "\n\n"
                    + "If you did not create this account, you can safely ignore this email.";
            emailService.sendEmail(savedUser.getEmail(),
                    "Verify your Smart Contact Manager account", body);
        } catch (Exception e) {
            // Delivery failed — roll back the account so the user isn't left
            // stuck with a disabled account they can never verify.
            userService.deleteUser(savedUser.getUserId());
            session.setAttribute("message", Message.builder()
                    .content("We couldn't send a verification email to that address. "
                            + "Please double-check it and try again.")
                    .type(MessageType.red).build());
            return "redirect:/register";
        }

        session.setAttribute("message", Message.builder()
                .content("Almost there! We've sent a verification link to " + savedUser.getEmail()
                        + ". Please verify your email, then log in.")
                .type(MessageType.green).build());

        // send them to login; they can sign in after verifying
        return "redirect:/login";
    }

    /**
     * Checks that the domain part of an email address can actually receive mail
     * by looking up its DNS MX (or fallback A) records. This rejects obviously
     * fake addresses like name@notarealdomain12345.com while allowing any real
     * mail provider (gmail.com, outlook.com, company domains, etc.).
     */
    private boolean isRealEmailDomain(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String domain = email.substring(email.lastIndexOf('@') + 1).trim();
        if (domain.isEmpty()) {
            return false;
        }
        try {
            java.util.Hashtable<String, String> env = new java.util.Hashtable<>();
            env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
                    "com.sun.jndi.dns.DnsContextFactory");
            javax.naming.directory.DirContext ctx = new javax.naming.directory.InitialDirContext(env);
            javax.naming.directory.Attributes attrs = ctx.getAttributes(domain, new String[] { "MX", "A" });
            ctx.close();
            javax.naming.directory.Attribute mx = attrs.get("MX");
            javax.naming.directory.Attribute a = attrs.get("A");
            return (mx != null && mx.size() > 0) || (a != null && a.size() > 0);
        } catch (javax.naming.NamingException e) {
            // No DNS record found (or DNS lookup failed) -> treat as not deliverable
            return false;
        }
    }

}
