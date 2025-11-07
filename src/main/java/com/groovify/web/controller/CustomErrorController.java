package com.groovify.web.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

/**
 * {@code CustomErrorController} is responsible for handling all unhandled exceptions
 * and HTTP error responses within the Groovify application.
 *
 * <p>Spring Boot automatically forwards any error (such as HTTP 404 or 500)
 * to the "/error" endpoint. This controller intercepts that route to render
 * a custom Thymeleaf view instead of the default "Whitelabel Error Page".</p>
 *
 * <p>The controller extracts error-related information (status code, message)
 * from the {@link HttpServletRequest} attributes and passes them to the view layer
 * through the {@link Model} for user-friendly display.</p>
 *
 * <p>Typical usage:
 * <ul>
 *   <li>Display a meaningful error page to end users.</li>
 *   <li>Preserve consistent application styling (CSS, layout, etc.).</li>
 *   <li>Hide sensitive technical details from the user.</li>
 * </ul>
 * </p>
 *
 * <p>Example of attributes passed to the view:
 * <pre>
 *   status  -> 404
 *   error   -> "Unexpected Error"
 *   message -> "Resource not found"
 * </pre>
 * </p>
 *
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Handles all error responses forwarded to the "/error" endpoint.
     *
     * <p>This method is automatically invoked by Spring Boot when any exception
     * or HTTP status error occurs (such as 404 Not Found, 500 Internal Server Error, etc.).</p>
     *
     * <p>It retrieves the following standard request attributes:</p>
     * <ul>
     *   <li>{@code javax.servlet.error.status_code} – the HTTP status code</li>
     *   <li>{@code javax.servlet.error.message} – an optional descriptive error message</li>
     * </ul>
     *
     * <p>Then, it adds them to the model for rendering in the Thymeleaf
     * {@code error.html} template.</p>
     *
     * @param request the current {@link HttpServletRequest}, used to extract error attributes
     * @param model the {@link Model} that holds attributes for the error view
     * @return the name of the Thymeleaf template to render (in this case, {@code "error"})
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Retrieve the HTTP status code (e.g., 404, 500) from the request attributes
        Object statusCode = request.getAttribute("javax.servlet.error.status_code");

        // Retrieve the error message (if any) associated with the request
        Object message = request.getAttribute("javax.servlet.error.message");

        // Populate the model with attributes to display on the error page
        model.addAttribute("status", statusCode != null ? statusCode : "Error");
        model.addAttribute("error", "Unexpected Error");
        model.addAttribute("message", message != null ? message : "An unexpected error occurred.");

        // Return the "error" Thymeleaf template for rendering the custom error page
        return "error";
    }
}
