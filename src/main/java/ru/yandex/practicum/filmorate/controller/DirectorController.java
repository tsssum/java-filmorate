package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public Collection<Director> getDirectors() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Director> findById(@PathVariable Long id) {
        return directorService.findById(id);
    }

    @PostMapping
    public Optional<Director> create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    public Optional<Director> update(@RequestBody Director newDirector) {
        return directorService.update(newDirector);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        directorService.delete(id);
    }

}
