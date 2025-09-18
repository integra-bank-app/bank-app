package clf.integra.backend.controller;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;


@WebMvcTest(BranchController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class BranchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UUID branchId;
    private List<UserDTO> users;
    private List<UserDTO> usersEmpty;

    @BeforeEach
    public void setup() {
        branchId = UUID.randomUUID();
        users = List.of(
                new UserDTO("A", "B", "C"),
                new UserDTO("D", "E", "F"));
        usersEmpty = List.of();
    }

    @Test
    public void testCollectTaxesAndFeesFromBranch_Success_ReturnOkWithRevenue() throws Exception {
        when(userService.collectTaxesAndFeesFromBranch(eq(branchId))).thenReturn(1000.0);
        mockMvc.perform(post("/branches/{branchId}/collect-taxes-and-fees", branchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.0"));
    }

    @Test
    public void testCollectTaxesAndFeesFromBranch_ZeroRevenue_ReturnExpectationFailed() throws Exception {
        when(userService.collectTaxesAndFeesFromBranch(eq(branchId))).thenReturn(0.0);
        mockMvc.perform(post("/branches/{branchId}/collect-taxes-and-fees", branchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isExpectationFailed())
                .andExpect(content().string(""));
    }

    @Test
    public void testCollectTaxesAndFeesFromBranch_NotFound_ReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Branch not found"))
                .when(userService).collectTaxesAndFeesFromBranch(eq(branchId));
        mockMvc.perform(post("/branches/{branchId}/collect-taxes-and-fees", branchId))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetUserByBranch_Success_ReturnOkWithListUsers() throws Exception {
        when(userService.getAllUsersByBranch(eq(branchId))).thenReturn(this.users);
        this.mockMvc.perform(get("/branches/{branchId}/users", branchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("A"))
                .andExpect(jsonPath("$[1].lastName").value("F"));
    }

    @Test
    public void testGetUserByBranch_Empty_ReturnNotFound() throws Exception {
        when(userService.getAllUsersByBranch(eq(branchId))).thenReturn(this.usersEmpty);
        this.mockMvc.perform(get("/branches/{branchId}/users", branchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserByBranch_NotFound_ReturnNotFound() throws Exception {
        when(userService.getAllUsersByBranch(eq(branchId))).thenReturn(this.usersEmpty);
        this.mockMvc.perform(get("/branches/{branchId}/users", branchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

