package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.SeanceController;
import fr.semifir.apicinema.dtos.seance.SeanceDTO;
import fr.semifir.apicinema.entities.Salle;
import fr.semifir.apicinema.entities.Seance;
import fr.semifir.apicinema.services.SeanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SeanceController.class)
public class SeanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeanceService service;

    private SeanceDTO seanceDTO () {
        return new SeanceDTO(
                "1",
                new Date(),
                new Salle());
    }

    private SeanceDTO seanceDTOUpdate () {
        return new SeanceDTO(
                "2",
                new Date(),
                new Salle());
    }



    @Test
    public void testFindAllSeances() throws Exception {
        this.mockMvc.perform(get("/seances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testFindOneSeance() throws Exception {
        SeanceDTO seanceDTO = this.seanceDTO();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(seanceDTO));

        MvcResult mvcResult = this.mockMvc.perform(get("/seances/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        SeanceDTO body = json.fromJson(mvcResult.getResponse().getContentAsString(), SeanceDTO.class);

        Assertions.assertEquals(body.getId(), this.seanceDTO().getId());
    }

    @Test
    public void testFindOneSeanceWrongId() throws Exception {
        this.mockMvc.perform(get("/seances/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSaveSeance() throws Exception {
        SeanceDTO seanceDTO = this.seanceDTO();

        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(seanceDTO);
        this.mockMvc.perform(post("/seances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateSeance() throws Exception {
        SeanceDTO seanceDTO = this.seanceDTO();
        SeanceDTO seanceUpdated = this.seanceDTOUpdate();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(seanceDTO));

        MvcResult mvcResult =this.mockMvc.perform(get("/seances/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        SeanceDTO body = json.fromJson(mvcResult.getResponse().getContentAsString(), SeanceDTO.class);

        BDDMockito.when(service.save(any(Seance.class)))
                .thenReturn(seanceUpdated);


        body.setId("2");
        String bodyJson = json.toJson(body);

        MvcResult result = this.mockMvc.perform(put("/seances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn();

        SeanceDTO finalBody = json.fromJson(result.getResponse().getContentAsString(), SeanceDTO.class);
        Assertions.assertEquals(finalBody.getId(), this.seanceDTOUpdate().getId());

    }

    @Test
    public void testDeleteSeance() throws Exception {
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(this.seanceDTO());
        this.mockMvc.perform(delete("/seances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

}

