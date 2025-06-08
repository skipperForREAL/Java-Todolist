package com.toDolist.TheTsskList.controller;

import com.toDolist.TheTsskList.model.Task;
import com.toDolist.TheTsskList.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String getAllTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        model.addAttribute("newTask", new Task());
        model.addAttribute("emailForm", new EmailForm()); // Add email form object
        model.addAttribute("totalTasks", taskService.getTotalTaskCount());
        return "tasks";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute Task newTask, RedirectAttributes redirectAttributes) {
        try {
            taskService.saveTask(newTask);
            redirectAttributes.addFlashAttribute("successMessage", "Task added successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            taskService.deleteTask(id);
            redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/toggle/{id}")
    public String toggleTaskCompletion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            taskService.toggleTaskCompletion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Task status updated!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/send-email")
    public String sendTasksByEmail(@ModelAttribute EmailForm emailForm, RedirectAttributes redirectAttributes) {
        try {
            taskService.sendTasksToEmail(emailForm.getEmail());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Tasks sent successfully to " + emailForm.getEmail());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to send email: " + e.getMessage());
        }
        return "redirect:/tasks";
    }

    // Inner class for email form handling
    public static class EmailForm {
        private String email;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}