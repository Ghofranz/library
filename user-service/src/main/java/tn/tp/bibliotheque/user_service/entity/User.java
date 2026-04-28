package tn.tp.bibliotheque.user_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;

    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int maxBooksAllowed;

    private int currentBooksCount;

    public enum UserStatus {
        ACTIVE,
        BLOCKED,
        SUSPENDED,
        PENDING
    }

    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
        this.role = Role.MEMBER;
        this.maxBooksAllowed = 3;
        this.currentBooksCount = 0;
    }

    public User(String username, String email, String firstName, String lastName) {
        this();
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) {
        this.role = role;
        // Premium members can borrow more books
        if (role == Role.PREMIUM_MEMBER) {
            this.maxBooksAllowed = 10;
        } else if (role == Role.ADMIN || role == Role.LIBRARIAN) {
            this.maxBooksAllowed = 20;
        }
    }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getMaxBooksAllowed() { return maxBooksAllowed; }
    public void setMaxBooksAllowed(int maxBooksAllowed) { this.maxBooksAllowed = maxBooksAllowed; }

    public int getCurrentBooksCount() { return currentBooksCount; }
    public void setCurrentBooksCount(int currentBooksCount) { this.currentBooksCount = currentBooksCount; }

    public boolean canBorrowBook() {
        return status == UserStatus.ACTIVE && currentBooksCount < maxBooksAllowed;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}