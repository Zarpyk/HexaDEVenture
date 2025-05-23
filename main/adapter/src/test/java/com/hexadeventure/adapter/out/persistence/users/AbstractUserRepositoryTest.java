package com.hexadeventure.adapter.out.persistence.users;

import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractUserRepositoryTest {
    private static final String TEST_USER_EMAIL = "test@test.com";
    private static final String TEST_USER2_EMAIL = "test2@test.com";
    private static final User USER = new User(TEST_USER_EMAIL, "test", "test");
    private static final User USER2 = new User(TEST_USER_EMAIL, "test", "test");
    private static final String NON_EXISTING_EMAIL = "nonexist";
    
    @Autowired
    public UserRepository repo;
    
    @AfterEach
    public void afterEach() {
        repo.deleteAll();
    }
    
    @Test
    public void givenAnExistingEmail_whenFindByEmail_thenReturnAnUser() {
        repo.save(USER);
        
        Optional<User> user = repo.findByEmail(TEST_USER_EMAIL);
        assertThat(user).isNotEmpty();
        assertThat(user.get().getId()).isEqualTo(USER.getId());
        assertThat(user.get().getEmail()).isEqualTo(USER.getEmail());
        assertThat(user.get().getUsername()).isEqualTo(USER.getUsername());
        assertThat(user.get().getPassword()).isEqualTo(USER.getPassword());
        assertThat(user.get().getMapId()).isEqualTo(USER.getMapId());
        assertThat(user.get().getWins()).isEqualTo(USER.getWins());
        assertThat(user.get().getPlayedGames()).isEqualTo(USER.getPlayedGames());
        assertThat(user.get().getPlayedTime()).isEqualTo(USER.getPlayedTime());
        assertThat(user.get().getCurrentGameStartTime()).isEqualTo(USER.getCurrentGameStartTime());
        assertThat(user.get().getTravelledDistance()).isEqualTo(USER.getTravelledDistance());
        assertThat(user.get().getCollectedResources()).isEqualTo(USER.getCollectedResources());
    }
    
    @Test
    public void givenAnNonExistingEmail_whenFindByEmail_thenReturnAnEmptyOptional() {
        Optional<User> user = repo.findByEmail(NON_EXISTING_EMAIL);
        assertThat(user).isEmpty();
    }
    
    @Test
    public void givenId_whenFindById_thenReturnAnUser() {
        repo.save(USER);
        
        Optional<User> user = repo.findById(USER.getId());
        assertThat(user).isNotEmpty();
    }
    
    @Test
    public void givenAnNonExistingId_whenFindById_thenReturnAnEmptyOptional() {
        Optional<User> user = repo.findById(USER.getId());
        assertThat(user).isEmpty();
    }
    
    @Test
    public void givenAnUser_whenSave_thenUserIsPersisted() {
        repo.save(USER);
        Optional<User> user = repo.findByEmail(TEST_USER_EMAIL);
        assertThat(user).isNotEmpty();
    }
    
    @Test
    public void givenAnUser_whenDeleteByEmail_thenUserIsDeleted() {
        repo.save(USER);
        
        repo.deleteByEmail(TEST_USER_EMAIL);
        
        Optional<User> user = repo.findByEmail(TEST_USER_EMAIL);
        assertThat(user).isEmpty();
    }
    
    @Test
    public void whenDeleteAll_thenAllUsersAreDeleted() {
        repo.save(USER);
        repo.save(USER2);
        
        repo.deleteAll();
        
        Optional<User> user = repo.findByEmail(TEST_USER_EMAIL);
        assertThat(user).isEmpty();
        
        user = repo.findByEmail(TEST_USER2_EMAIL);
        assertThat(user).isEmpty();
    }
}
