package com.hexadeventure.model.inventory;

import com.hexadeventure.common.GameMapFactory;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.potions.PotionType;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.resources.ResourceType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InventoryTest {
    public static final String TEST_WEAPON_NAME = "Sword";
    public static final String TEST_FOOD_NAME = "Apple";
    public static final String TEST_POTION_NAME = "Health Potion";
    public static final String TEST_MATERIAL_NAME = "Wood";
    
    @Test
    public void givenWeapon_whenAddItemToInventory_thenItemIsAdded() {
        Inventory inventory = new Inventory();
        Weapon weapon = new Weapon(TEST_WEAPON_NAME, WeaponType.MELEE, 1);
        
        inventory.addItem(weapon);
        
        assertThat(inventory.getItems()).hasSize(1);
        assertThat(inventory.getItems()).containsKey(weapon.getId());
    }
    
    @Test
    public void givenSameItem_whenAddItemToInventory_thenQuantityIsUpdated() {
        Inventory inventory = new Inventory();
        Weapon weapon = new Weapon(TEST_WEAPON_NAME, WeaponType.MELEE, 1);
        assertThat(weapon.getType()).isEqualTo(ItemType.WEAPON);
        
        inventory.addItem(weapon);
        inventory.addItem(weapon);
        
        assertThat(inventory.getItems().size()).isEqualTo(1);
        assertThat(inventory.getItems().get(weapon.getId()).getCount()).isEqualTo(2);
    }
    
    @Test
    public void givenFood_whenAddItemToInventory_thenItemIsAdded() {
        Inventory inventory = new Inventory();
        Food food = new Food(TEST_FOOD_NAME, 1);
        assertThat(food.getType()).isEqualTo(ItemType.FOOD);
        
        inventory.addItem(food);
        
        assertThat(inventory.getItems()).hasSize(1);
        assertThat(inventory.getItems()).containsKey(food.getId());
    }
    
    @Test
    public void givenPotion_whenAddItemToInventory_thenItemIsAdded() {
        Inventory inventory = new Inventory();
        Potion potion = new Potion(TEST_POTION_NAME, 1, PotionType.HEALING, 1);
        assertThat(potion.getType()).isEqualTo(ItemType.POTION);
        
        inventory.addItem(potion);
        
        assertThat(inventory.getItems()).hasSize(1);
        assertThat(inventory.getItems()).containsKey(potion.getId());
    }
    
    @Test
    public void givenMaterial_whenAddItemToInventory_thenItemIsAdded() {
        Inventory inventory = new Inventory();
        Material material = new Material(TEST_MATERIAL_NAME, 1, ResourceType.WOOD);
        assertThat(material.getType()).isEqualTo(ItemType.MATERIAL);
        
        inventory.addItem(material);
        
        assertThat(inventory.getItems()).hasSize(1);
        assertThat(inventory.getItems()).containsKey(material.getId());
    }
    
    @Test
    public void givenItem_whenRemoveItemFromInventory_thenItemIsRemoved() {
        Inventory inventory = new Inventory();
        Weapon weapon = new Weapon(TEST_WEAPON_NAME, WeaponType.MELEE, 1);
        
        inventory.addItem(weapon);
        inventory.removeItem(weapon, 1);
        
        assertThat(inventory.getItems()).isEmpty();
    }
    
    @Test
    public void givenItem_whenRemoveItemFromInventory_thenQuantityIsUpdated() {
        Inventory inventory = new Inventory();
        Weapon weapon = new Weapon(TEST_WEAPON_NAME, WeaponType.MELEE, 2);
        
        inventory.addItem(weapon);
        inventory.addItem(weapon);
        inventory.removeItem(weapon, 1);
        
        assertThat(inventory.getItems().get(weapon.getId()).getCount()).isEqualTo(1);
    }
    
    @Test
    public void givenCharacter_whenAddCharacterToInventory_thenCharacterIsAdded() {
        Inventory inventory = new Inventory();
        PlayableCharacter character = new PlayableCharacter("Warrior", 100, 10);
        
        inventory.addCharacter(character);
        
        assertThat(inventory.getCharacters()).hasSize(1);
        assertThat(inventory.getCharacters()).containsKey(character.getId());
    }
    
    @Test
    public void givenCharacter_whenRemoveCharacterFromInventory_thenCharacterIsRemoved() {
        Inventory inventory = new Inventory();
        PlayableCharacter character = new PlayableCharacter("Warrior", 100, 10);
        
        inventory.addCharacter(character);
        inventory.removeCharacter(character);
        
        assertThat(inventory.getCharacters()).isEmpty();
    }
    
    @Test
    public void givenCharacterAndWeapon_whenSetCharacterWeapon_thenWeaponIsSet() {
        Inventory inventory = new Inventory();
        PlayableCharacter character = new PlayableCharacter("Warrior", 100, 10);
        Weapon weapon = new Weapon(TEST_WEAPON_NAME, WeaponType.MELEE, 1);
        
        inventory.addCharacter(character);
        inventory.addItem(weapon);
        inventory.setCharacterWeapon(character, weapon.getId());
        
        assertThat(inventory.getCharacters().get(character.getId()).getWeapon()).isEqualTo(weapon);
    }
    
    @Test
    public void givenCharacter_whenRemoveCharacterWeapon_thenWeaponIsRemoved() {
        Inventory inventory = new Inventory();
        PlayableCharacter character = new PlayableCharacter("Warrior", 100, 10);
        Weapon weapon = new Weapon(TEST_WEAPON_NAME, WeaponType.MELEE, 1);
        
        inventory.addCharacter(character);
        inventory.addItem(weapon);
        inventory.setCharacterWeapon(character, weapon.getId());
        inventory.removeCharacterWeapon(character);
        
        assertThat(inventory.getCharacters()
                            .get(character.getId())
                            .getWeapon()).isEqualTo(Weapon.DEFAULT_WEAPON);
    }
    
    @Test
    public void whenCreateMap_thenInventoryIsCreated() {
        GameMap gameMap = GameMapFactory.createGameMap();
        
        assertThat(gameMap.getInventory()).isNotNull();
    }
}
