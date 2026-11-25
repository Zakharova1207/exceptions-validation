package com.example.demo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private HashMap<Long, User> users = new HashMap<>();
    private Long nextId = 1L;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) throws ResourceNotFoundException {
        User user = users.get(id);
        if (user == null)
            throw new ResourceNotFoundException("Юзера не найдено");
        else return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> saveUser(@Valid @RequestBody User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws ConflictException {
        if (!users.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        if (users.get(id).orders == 0) throw new ConflictException("no active orders");

        users.remove(id);
        return ResponseEntity.noContent().build();

    }
}


