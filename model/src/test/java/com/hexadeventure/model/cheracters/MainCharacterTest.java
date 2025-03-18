package com.hexadeventure.model.cheracters;

import org.junit.jupiter.api.Test;
import com.hexadeventure.model.characters.MainCharacter;
import com.hexadeventure.model.map.Vector2;

import static org.assertj.core.api.Assertions.assertThat;

public class MainCharacterTest {
    @Test
    public void givenACoordinates_whenCreatingAMainCharacter_thenCreatesItOnTheCoordinate() {
        Vector2 position = new Vector2(1, 1);
        MainCharacter mainCharacter = new MainCharacter(position);
        
        assertThat(mainCharacter.getPosition()).isEqualTo(position);
    }
}
