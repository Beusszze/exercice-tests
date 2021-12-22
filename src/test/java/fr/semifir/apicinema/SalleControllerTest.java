package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.SalleController;
import fr.semifir.apicinema.dtos.salle.SalleDTO;
import fr.semifir.apicinema.entities.Cinema;
import fr.semifir.apicinema.entities.Salle;
import fr.semifir.apicinema.services.SalleService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SalleController.class)
public class SalleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalleService service;

    private SalleDTO salleDTO () {
        return new SalleDTO(
                "1",
                1,
                100,
                new Cinema()
                );
    }
    private SalleDTO salleTOUpdate () {
        return new SalleDTO(
                "1",
                1,
                110,
                new Cinema()
        );
    }


    @Test
    public void testFindAllSalles() throws Exception {
        this.mockMvc.perform(get("/salles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testFindOneSalle() throws Exception {
        SalleDTO salle = this.salleDTO();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(salle));

        MvcResult mvcResult = this.mockMvc.perform(get("/salles/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();
        SalleDTO body = json.fromJson(mvcResult.getResponse().getContentAsString(), SalleDTO.class);

        Assertions.assertEquals(body.getId(), this.salleDTO().getId());
        Assertions.assertEquals(body.getCinema(), this.salleDTO().getCinema());
        Assertions.assertEquals(body.getNumDeSalle(), this.salleDTO().getNumDeSalle());
        Assertions.assertEquals(body.getNbrPlace(), this.salleDTO().getNbrPlace());
    }

    @Test
    public void testFindOneSalleWrongId() throws Exception {
        this.mockMvc.perform(get("/salles/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSaveSalle() throws Exception {
        SalleDTO salleDTO = this.salleDTO();

        Gson json = new GsonBuilder().create();
        String body = json.toJson(salleDTO);
        this.mockMvc.perform(post("/salles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateSalle() throws Exception {
        SalleDTO salleToUpdate = this.salleDTO();
        SalleDTO salleUpdated = this.salleTOUpdate();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(salleToUpdate));

        MvcResult mvcResult =this.mockMvc.perform(get("/salles/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();
        SalleDTO body = json.fromJson(mvcResult.getResponse().getContentAsString(), SalleDTO.class);

        BDDMockito.when(service.save(any(Salle.class)))
                .thenReturn(salleUpdated);


        body.setNbrPlace(110);
        String bodyJson = json.toJson(body);

        MvcResult result = this.mockMvc.perform(put("/salles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn();

        SalleDTO finalBody = json.fromJson(result.getResponse().getContentAsString(), SalleDTO.class);
        Assertions.assertEquals(finalBody.getNumDeSalle(), this.salleTOUpdate().getNumDeSalle());
        Assertions.assertEquals(finalBody.getId(), this.salleTOUpdate().getId());
        Assertions.assertEquals(finalBody.getCinema(), this.salleTOUpdate().getCinema());
        Assertions.assertEquals(finalBody.getNbrPlace(), this.salleTOUpdate().getNbrPlace());

    }

    @Test
    public void testDeleteSalle() throws Exception {
        Gson json = new GsonBuilder().create();
        String body = json.toJson(this.salleDTO());
        this.mockMvc.perform(delete("/salles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }







}

