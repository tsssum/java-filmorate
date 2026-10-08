package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class MpaService {
    private final MpaRepository mpaRepository;

    @Autowired
    public MpaService(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    public Collection<MPA> getAll() {
        return mpaRepository.getAll();
    }

    public Optional<MPA> getById(int id) {
        if (mpaRepository.getById(id).isEmpty()) {
            throw new NotFoundException("MPA with id " + id + " not found");
        } else return mpaRepository.getById(id);
    }

}
