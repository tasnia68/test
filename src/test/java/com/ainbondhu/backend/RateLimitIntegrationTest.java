package com.ainbondhu.backend;

import com.ainbondhu.backend.service.AuthService;
import com.ainbondhu.backend.service.RateLimitingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RateLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimitingService rateLimitingService;

    @MockBean
    private AuthService authService;

    @Test
    public void testRateLimiting() throws Exception {
        // Mock AuthService to return success or handle call
        // We don't care about the actual login, just that the interceptor passes
        when(authService.login(any())).thenReturn(com.ainbondhu.backend.dto.LoginResponse.builder()
                .token("token")
                .type("Bearer")
                .role("CLIENT")
                .build());

        // The rate limit is 20 requests per minute per IP.
        // We will make 21 requests. The 21st should fail.

        String testEndpoint = "/api/v1/auth/login";
        String content = "{\"email\":\"test@test.com\", \"password\":\"password\"}";

        for (int i = 0; i < 20; i++) {
            int requestIndex = i + 1;
            mockMvc.perform(post(testEndpoint)
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isOk()) // Should be 200 OK because of Mock
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        if (status == 429) {
                            throw new AssertionError("Premature 429 at request " + requestIndex);
                        }
                    });
        }

        // The 21st request should be rate limited
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(testEndpoint)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isTooManyRequests());
    }
}
