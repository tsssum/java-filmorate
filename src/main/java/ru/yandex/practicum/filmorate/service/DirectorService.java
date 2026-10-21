package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.EmptyStringException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class DirectorService {
    private DirectorRepository directorRepository;

    @Autowired
    public DirectorService(DirectorDbStorage directorStorage) {
        this.directorRepository = directorStorage;
    }

    public Collection<Director> findAll() {
        return directorRepository.findAll();
    }


    public Optional<Director> findById(Long id) {
        if (directorRepository.findById(id).isPresent()) {
            return directorRepository.findById(id);
        } else {
            throw new NotFoundException("Director with id = " + id + " not found");
        }
    }

    public Optional<Director> create(Director director) {
        return directorRepository.create(director);
    }

    public Optional<Director> update(Director newDirector) {
        if (newDirector.getId() == null) {
            throw new EmptyStringException("Id должен быть указан");
        }
        Optional<Director> oldDirector = directorRepository.findById(newDirector.getId());
        if (oldDirector.isPresent()) {
            if (newDirector.getName() == null || newDirector.getName().isBlank()) {
                newDirector.setName(oldDirector.get().getName());
            }
            return directorRepository.update(newDirector);
        }
        throw new NotFoundException("Режессёр с id = " + newDirector.getId() + " не найден");
    }

    public void delete(Long directorId) {
        if (directorRepository.findById(directorId).isEmpty()) {
            throw new NotFoundException("Режессёр с id = " + directorId + " не найден");
        }
        directorRepository.delete(directorId);
    }


}
