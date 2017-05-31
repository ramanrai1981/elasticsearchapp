package com.hartron.elasticgate.service;

import com.hartron.elasticgate.AbstractCassandraTest;
import com.hartron.elasticgate.ElasticgateApp;
import com.hartron.elasticgate.domain.User;
import com.hartron.elasticgate.config.Constants;
import com.hartron.elasticgate.repository.UserRepository;
import com.hartron.elasticgate.service.dto.UserDTO;
import java.time.ZonedDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticgateApp.class)
public class UserServiceIntTest extends AbstractCassandraTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void assertThatAnonymousUserIsNotGet() {
        final List<UserDTO> allManagedUsers = userService.getAllManagedUsers();
        assertThat(allManagedUsers.stream()
            .noneMatch(user -> Constants.ANONYMOUS_USER.equals(user.getLogin())))
            .isTrue();
    }
}
