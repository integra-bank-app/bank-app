package clf.integra.anaf.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

public class AnafServiceTest {

    private final AnafService salaryService = new AnafService();

    @Test
    void testGetUserSalary_returnsValueWithinRange() {
        UUID userId = UUID.randomUUID();

        int salary = salaryService.getUserSalary(userId);

        assertTrue(salary >= 2500 && salary <= 250000,
                "Salary should be between 2,500 and 250,000 but was " + salary);
    }

}
