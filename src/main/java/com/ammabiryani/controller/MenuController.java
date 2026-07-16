package com.ammabiryani.controller;

import com.ammabiryani.model.MenuItem;
import com.ammabiryani.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // Public: only items customers can currently order
    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenu() {
        return ResponseEntity.ok(menuService.getAvailableMenu());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getById(id));
    }

    // Admin-style endpoints below (for the hotel owner / staff to manage the menu).
    // In a production build, protect these with Spring Security + an admin login.

    @GetMapping("/all")
    public ResponseEntity<List<MenuItem>> getAllForAdmin() {
        return ResponseEntity.ok(menuService.getAllMenu());
    }

    @PostMapping
    public ResponseEntity<MenuItem> create(@Valid @RequestBody MenuItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.create(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> update(@PathVariable Long id, @Valid @RequestBody MenuItem item) {
        return ResponseEntity.ok(menuService.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
