package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.CinemaController;
import fr.semifir.apicinema.controllers.FilmController;
import fr.semifir.apicinema.dtos.cinema.CinemaDTO;
import fr.semifir.apicinema.dtos.film.FilmDTO;
import fr.semifir.apicinema.entities.Cinema;
import fr.semifir.apicinema.entities.Film;
import fr.semifir.apicinema.entities.Seance;
import fr.semifir.apicinema.services.CinemaService;
import fr.semifir.apicinema.services.FilmService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService service;

    private FilmDTO filmDTO () {
        return new FilmDTO(
                "1",
                "Mon Film",
                120f,
                new Seance()
                );
    }
    private FilmDTO filmDTOUpdate () {
        return new FilmDTO(
                "1",
                "Mon Film avec bonus",
                150f,
                new Seance()
        );
    }


    @Test
    public void testFindAllFilms() throws Exception {
        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testFindOneFilm() throws Exception {
        FilmDTO filmDTO = this.filmDTO();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(filmDTO));

        MvcResult mvcResult = this.mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();
        FilmDTO body = json.fromJson(mvcResult.getResponse().getContentAsString(), FilmDTO.class);

        Assertions.assertEquals(body.getId(), this.filmDTO().getId());
        Assertions.assertEquals(body.getNom(), this.filmDTO().getNom());
        Assertions.assertEquals(body.getSeance(), this.filmDTO().getSeance());
        Assertions.assertEquals(body.getDuree(), this.filmDTO().getDuree());
    }

    @Test
    public void testFindOneFilmWrongId() throws Exception {
        this.mockMvc.perform(get("/films/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSaveFilm() throws Exception {
        FilmDTO filmDTO = this.filmDTO();

        Gson json = new GsonBuilder().create();
        String body = json.toJson(filmDTO);
        this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateFilm() throws Exception {
        FilmDTO filmToUpdate = this.filmDTO();
        FilmDTO filmUpdated = this.filmDTOUpdate();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(filmToUpdate));

        MvcResult mvcResult =this.mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();
        FilmDTO body = json.fromJson(mvcResult.getResponse().getContentAsString(), FilmDTO.class);

        BDDMockito.when(service.save(any(Film.class)))
                .thenReturn(filmUpdated);


        body.setDuree(150f);
        body.setNom("Mon Film avec bonus");
        String bodyJson = json.toJson(body);

        MvcResult result = this.mockMvc.perform(put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn();

        FilmDTO finalBody = json.fromJson(result.getResponse().getContentAsString(), FilmDTO.class);
        Assertions.assertEquals(finalBody.getNom(), this.filmDTOUpdate().getNom());
        Assertions.assertEquals(finalBody.getId(), this.filmDTOUpdate().getId());
        Assertions.assertEquals(finalBody.getDuree(), this.filmDTOUpdate().getDuree());
        Assertions.assertEquals(finalBody.getSeance(), this.filmDTOUpdate().getSeance());

    }

    @Test
    public void testDeleteFilm() throws Exception {
        Gson json = new GsonBuilder().create();
        String body = json.toJson(this.filmDTO());
        this.mockMvc.perform(delete("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }







}

