package com.toDolist.TheTsskList.service;

import com.toDolist.TheTsskList.model.Task;
import com.toDolist.TheTsskList.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public TaskService(TaskRepository taskRepository, JavaMailSender mailSender) {
        this.taskRepository = taskRepository;
        this.mailSender = mailSender;
    }

    // Get all tasks sorted by creation time (descending)
    public List<Task> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    // Save new task after validation
    public Task saveTask(Task task) {
        validateTask(task);
        return taskRepository.save(task);
    }

    // Delete task by ID
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    // Toggle task completion status
    public Task toggleTaskCompletion(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setCompleted(!task.isCompleted());
        return taskRepository.save(task);
    }

    // Get total task count
    public long getTotalTaskCount() {
        return taskRepository.count();
    }

    // Send all tasks to provided email address
    public void sendTasksToEmail(String recipientEmail) {
        List<Task> tasks = getAllTasks();

        if (tasks.isEmpty()) {
            throw new IllegalStateException("No tasks found to send");
        }

        String emailContent = tasks.stream()
                .map(task -> String.format("%s [%s] - %s",
                        task.getName(),
                        task.isCompleted() ? "✓" : " ",
                        task.getDescription()))
                .collect(Collectors.joining("\n"));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Your Current To-Do List");
        message.setText("Here are your tasks:\n\n" + emailContent);

        mailSender.send(message);
    }

    // Helper to validate task input
    private void validateTask(Task task) {
        if (task.getName() == null || task.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be empty");
        }
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
    }
}
