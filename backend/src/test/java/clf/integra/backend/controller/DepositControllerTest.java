package clf.integra.backend.controller;

import clf.integra.backend.dto.DepositImportDTO;
import clf.integra.backend.dto.DepositImportRequest;
import clf.integra.backend.security.utils.JwtUtils;
import clf.integra.backend.service.DepositsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(DepositController.class)
public class DepositControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    DepositsService depositsService;

    @MockitoBean
    JwtUtils jwtUtils;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testImportDeposits_validRequest_returnOk() throws Exception {
        DepositImportDTO deposit1 = new DepositImportDTO(1000.0,3.5, UUID.randomUUID());
        DepositImportDTO deposit2 = new DepositImportDTO(2000.0,4.0, UUID.randomUUID());
        DepositImportRequest request = new DepositImportRequest(List.of(deposit1, deposit2));
        doNothing().when(depositsService).bulkImport(request.depositImports());

        mockMvc.perform(post("/deposits/import")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposits imported successfully"));
    }

    @Test
    void testImportDeposits_negativeAmount_returnBadRequest() throws Exception {
        DepositImportDTO deposit1 = new DepositImportDTO(-1000.0,3.5, UUID.randomUUID());
        DepositImportDTO deposit2 = new DepositImportDTO(2000.0,4.0, UUID.randomUUID());
        DepositImportRequest request = new DepositImportRequest(List.of(deposit1, deposit2));
        doNothing().when(depositsService).bulkImport(request.depositImports());

        mockMvc.perform(post("/deposits/import")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testImportDeposits_emptyList_returnBadRequest() throws Exception {
        DepositImportRequest request = new DepositImportRequest(List.of());
        mockMvc.perform(post("/deposits/import")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
