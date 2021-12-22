package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import fr.semifir.apicinema.controllers.CinemaController;
import fr.semifir.apicinema.dtos.cinema.CinemaDTO;
import fr.semifir.apicinema.entities.Cinema;
import fr.semifir.apicinema.exceptions.NotFoundException;
import fr.semifir.apicinema.services.CinemaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CinemaController.class)
public class CinemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CinemaService service;



    private CinemaDTO cinemaDTO () {
        return new CinemaDTO(
                "1",
                "gaumont");
    }

    private CinemaDTO cinemaDTOUpdated () {
        return new CinemaDTO(
                "1",
                "kinepolis"
        );
    }


    @Test
    public void testFindAllCinemas() throws Exception {
        this.mockMvc.perform(get("/cinemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testFindOneCinema() throws Exception {
        CinemaDTO cinemaDTO = this.cinemaDTO();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(cinemaDTO));

        MvcResult result = this.mockMvc.perform(get("/cinemas/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();
        CinemaDTO body = json.fromJson(result.getResponse().getContentAsString(), CinemaDTO.class);

        Assertions.assertEquals(body.getId(), this.cinemaDTO().getId());
        Assertions.assertEquals(body.getNom(), this.cinemaDTO().getNom());

    }

    @Test
    public void testFindOneCinemaWrongId() throws Exception {
        this.mockMvc.perform(get("/cinemas/2"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testSaveCinema() throws Exception {
        CinemaDTO cinemaDTO = this.cinemaDTO();

        Gson json = new GsonBuilder().create();
        String body = json.toJson(cinemaDTO);
        this.mockMvc.perform(post("/cinemas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCinema() throws Exception {
        CinemaDTO cinemaToUpdateDTO = this.cinemaDTO();
        CinemaDTO cinemaUpdatedDTO = this.cinemaDTOUpdated();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(cinemaToUpdateDTO));

        MvcResult mvcResult =this.mockMvc.perform(get("/cinemas/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();
        CinemaDTO body = json.fromJson(mvcResult.getResponse().getContentAsString(), CinemaDTO.class);

        BDDMockito.when(service.save(any(Cinema.class)))
                .thenReturn(cinemaUpdatedDTO);

        body.setNom("kinepolis");
        String bodyJson = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(put("/cinemas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn();

        CinemaDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), CinemaDTO.class);
        Assertions.assertEquals(finalBody.getNom(), this.cinemaDTOUpdated().getNom());
        Assertions.assertEquals(finalBody.getId(), this.cinemaDTOUpdated().getId());
    }
    @Test
    public void testDeleteCinema() throws Exception {
        Gson json = new GsonBuilder().create();
        String body = json.toJson(this.cinemaDTO());
        this.mockMvc.perform(delete("/cinemas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

}
