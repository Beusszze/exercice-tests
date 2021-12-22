package fr.semifir.apicinema.controllers;

import fr.semifir.apicinema.dtos.film.FilmDTO;
import fr.semifir.apicinema.entities.Film;
import fr.semifir.apicinema.exceptions.NotFoundException;
import fr.semifir.apicinema.services.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("films")
public class FilmController {

    @Autowired
    FilmService service;

    @GetMapping
    public List<FilmDTO> findAll() {
        return this.service.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<FilmDTO> findById(@PathVariable String id) {
        Optional<FilmDTO> filmDTO = null;
        try {
            filmDTO = this.service.findByID(id);
            return ResponseEntity.ok(filmDTO.get());
        } catch (NotFoundException e) {
           return ResponseEntity.notFound().header(e.getMessage()).build();
        } catch (NoSuchElementException es) {
            return ResponseEntity.notFound().header(es.getMessage()).build();
        }
    }

    @PostMapping
    public FilmDTO save(@RequestBody Film film) {
        return this.service.save(film);
    }

    @PutMapping
    public FilmDTO update(@RequestBody Film film) {
        return this.service.save(film);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestBody Film film) {
        this.service.delete(film);
        return ResponseEntity.ok(true);
    }
}
