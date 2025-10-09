package clf.integra.anaf.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnafService {

    private static final Logger logger = LoggerFactory.getLogger(AnafService.class);

    public int getUserSalary(UUID id) {
        int salary = (int) (Math.random() * (250000 - 2500 + 1) + 2500);
        logger.info("User with id {} has {} salary", id, salary);
        return salary;
    }
}
