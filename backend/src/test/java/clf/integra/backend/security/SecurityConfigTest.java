package clf.integra.backend.security;

import clf.integra.backend.security.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void testPasswordEncoder_returnBCryptPasswordEncoder() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }

    @Test
    void testAuthenticationManager_returnManagerFromConfiguration() throws Exception {
        AuthenticationManager mockManager = org.mockito.Mockito.mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockManager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertEquals(mockManager, result);
    }
}