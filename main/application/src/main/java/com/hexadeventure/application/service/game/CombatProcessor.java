package com.hexadeventure.application.service.game;

import com.hexadeventure.model.combat.CombatAction;
import com.hexadeventure.model.combat.CombatTerrain;
import com.hexadeventure.model.combat.TurnInfo;
import com.hexadeventure.model.inventory.characters.ChangedStats;
import com.hexadeventure.model.inventory.characters.CharacterCombatInfo;
import com.hexadeventure.model.inventory.characters.CharacterStatusChange;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.TreeSet;

public class CombatProcessor {
    public static final int NO_TARGET = -1;
    public static final int FIRST_ROW_INDEX = 0;
    public static final int SECOND_ROW_INDEX = 1;
    public static final int THIRD_ROW_INDEX = 2;
    
    private final CombatTerrain combatTerrain;
    @Getter
    private final List<TurnInfo> turnInfos = new ArrayList<>();
    
    @Getter
    private final TreeSet<CharacterCombatInfo> turnQueue = new TreeSet<>(CharacterCombatInfo::compareBySpeed);
    @Getter
    private final TreeSet<CharacterCombatInfo> characters = new TreeSet<>(CharacterCombatInfo::compareByAggro);
    @Getter
    private final TreeSet<CharacterCombatInfo> enemies = new TreeSet<>(CharacterCombatInfo::compareByAggro);
    
    
    public CombatProcessor(CombatTerrain combatTerrain) {
        this.combatTerrain = combatTerrain;
        calculateTurnQueue();
    }
    
    public void calculateTurnQueue() {
        for (int row = 0; row < combatTerrain.getRowSize(); row++) {
            for (int column = 0; column < combatTerrain.getColumnSize(); column++) {
                PlayableCharacter character = combatTerrain.getCharacterAt(row, column);
                if(character != null && character.getChangedStats().getHealth() > 0) {
                    CharacterCombatInfo c = new CharacterCombatInfo(character, row, column, false);
                    turnQueue.add(c);
                    characters.add(c);
                }
                PlayableCharacter enemy = combatTerrain.getEnemyTerrain()[row][column];
                if(enemy != null) {
                    ChangedStats enemyChangedStats = enemy.getChangedStats();
                    if(enemyChangedStats.getHealth() > 0 && !enemyChangedStats.isHypnotized()) {
                        CharacterCombatInfo e = new CharacterCombatInfo(enemy, row, column, true);
                        turnQueue.add(e);
                        enemies.add(e);
                    }
                }
            }
        }
    }
    
    public CharacterCombatInfo getFirstCharacter() {
        for (CharacterCombatInfo character : characters) {
            if(character.isDead()) continue;
            return character;
        }
        return null;
    }
    
    public CharacterCombatInfo getFirstEnemy() {
        for (CharacterCombatInfo enemy : enemies) {
            if(enemy.isDead()) continue;
            return enemy;
        }
        return null;
    }
    
    public void processTurn() {
        for (CharacterCombatInfo character : turnQueue) {
            if(character.isDead()) continue;
            switch (character.getWeaponType()) {
                case MELEE -> processMelee(character);
                case RANGED -> processRanged(character);
                case TANK -> processTank(character);
                case HEALER -> processHealer(character);
                case HYPNOTIZER -> processHypnotizer(character);
            }
        }
    }
    
    private void processMelee(CharacterCombatInfo character) {
        CharacterCombatInfo target = character.isEnemy() ? getFirstCharacter() : getFirstEnemy();
        processAttackTurn(character, target, character.getAggroGeneration());
    }
    
    private void processRanged(CharacterCombatInfo character) {
        switch (character.getRow()) {
            case FIRST_ROW_INDEX -> processFirstRowRanged(character);
            case SECOND_ROW_INDEX -> processSecondRowRanged(character);
            case THIRD_ROW_INDEX -> processThirdRowRanged(character);
            default -> throw new IllegalStateException("Unexpected value: " + character.getRow());
        }
    }
    
    private void processFirstRowRanged(CharacterCombatInfo character) {
        CharacterCombatInfo target = character.isEnemy() ? getFirstCharacter() : getFirstEnemy();
        processAttackTurn(character, target, character.getAggroGeneration());
    }
    
    @SuppressWarnings("DuplicatedCode")
    private void processSecondRowRanged(CharacterCombatInfo character) {
        CharacterCombatInfo target = null;
        TreeSet<CharacterCombatInfo> targets = character.isEnemy() ? characters : enemies;
        CharacterCombatInfo firstRowTarget = null;
        for (CharacterCombatInfo info : targets) {
            if(info.isDead()) continue;
            if(info.getRow() == FIRST_ROW_INDEX) {
                firstRowTarget = info;
                continue;
            }
            target = info;
            break;
        }
        if(target == null && firstRowTarget != null) target = firstRowTarget;
        processAttackTurn(character, target, character.getAggroGeneration());
    }
    
    @SuppressWarnings("DuplicatedCode")
    private void processThirdRowRanged(CharacterCombatInfo character) {
        CharacterCombatInfo target = null;
        CharacterCombatInfo firstRowTarget = null;
        CharacterCombatInfo secondRowTarget = null;
        TreeSet<CharacterCombatInfo> targets = character.isEnemy() ? characters : enemies;
        for (CharacterCombatInfo info : targets) {
            if(info.isDead()) continue;
            if(info.getRow() == FIRST_ROW_INDEX) {
                firstRowTarget = info;
                continue;
            }
            if(info.getRow() == SECOND_ROW_INDEX) {
                secondRowTarget = info;
                continue;
            }
            target = info;
            break;
        }
        if(target == null) {
            if(secondRowTarget != null) target = secondRowTarget;
            else if(firstRowTarget != null) target = firstRowTarget;
        }
        processAttackTurn(character, target, character.getAggroGeneration());
    }
    
    private void processTank(CharacterCombatInfo character) {
        CharacterCombatInfo target = character.isEnemy() ? getFirstCharacter() : getFirstEnemy();
        processAttackTurn(character, target, character.getAggroGeneration());
    }
    
    private void processHealer(CharacterCombatInfo character) {
        List<CharacterCombatInfo> firstRowHealable = new ArrayList<>();
        List<CharacterCombatInfo> secondRowHealable = new ArrayList<>();
        List<CharacterCombatInfo> thirdRowHealable = new ArrayList<>();
        TreeSet<CharacterCombatInfo> target = character.isEnemy() ? enemies : characters;
        for (CharacterCombatInfo info : target) {
            if(info.isDead()) continue;
            if(info.getHealth() == info.getCharacter().getHealth()) continue;
            if(info.getRow() == FIRST_ROW_INDEX) firstRowHealable.add(info);
            else if(info.getRow() == SECOND_ROW_INDEX) secondRowHealable.add(info);
            else thirdRowHealable.add(info);
        }
        boolean healed;
        switch (character.getRow()) {
            case FIRST_ROW_INDEX -> healed = processFirstRowHealer(character,
                                                                   firstRowHealable);
            case SECOND_ROW_INDEX -> healed = processSecondRowHealer(character,
                                                                     firstRowHealable,
                                                                     secondRowHealable);
            case THIRD_ROW_INDEX -> healed = processThirdRowHealer(character,
                                                                   firstRowHealable,
                                                                   secondRowHealable,
                                                                   thirdRowHealable);
            default -> throw new IllegalStateException("Unexpected value: " + character.getRow());
        }
        
        if(healed) return;
        
        // If no healable characters, attack
        CharacterCombatInfo attackTarget = character.isEnemy() ? getFirstCharacter() : getFirstEnemy();
        processAttackTurn(character, attackTarget, character.getDamage());
    }
    
    private boolean processFirstRowHealer(CharacterCombatInfo character, List<CharacterCombatInfo> firstRowHealable) {
        CharacterCombatInfo target = getHealTarget(firstRowHealable);
        if(target == null) return false;
        processHealingTurn(character, target);
        return true;
    }
    
    private boolean processSecondRowHealer(CharacterCombatInfo character, List<CharacterCombatInfo> firstRowHealable,
                                           List<CharacterCombatInfo> secondRowHealable) {
        CharacterCombatInfo target = getHealTarget(secondRowHealable);
        if(target == null) target = getHealTarget(firstRowHealable);
        if(target == null) return false;
        processHealingTurn(character, target);
        return true;
    }
    
    private boolean processThirdRowHealer(CharacterCombatInfo character, List<CharacterCombatInfo> firstRowHealable,
                                          List<CharacterCombatInfo> secondRowHealable,
                                          List<CharacterCombatInfo> thirdRowHealable) {
        CharacterCombatInfo target = getHealTarget(thirdRowHealable);
        if(target == null) target = getHealTarget(secondRowHealable);
        if(target == null) target = getHealTarget(firstRowHealable);
        if(target == null) return false;
        processHealingTurn(character, target);
        return true;
    }
    
    private static CharacterCombatInfo getHealTarget(List<CharacterCombatInfo> row) {
        if(row.isEmpty()) return null;
        CharacterCombatInfo target = row.getFirst();
        for (CharacterCombatInfo info : row) {
            if(info.isDead()) continue;
            if(info.getHealth() == info.getCharacter().getHealth()) continue;
            if(info.getHealth() > target.getHealth()) continue;
            if(info.getHealth() == target.getHealth() &&
               info.getCurrentAggro() <= target.getCurrentAggro()) continue;
            target = info;
            break;
        }
        if(target.isDead() || target.getHealth() == target.getCharacter().getHealth()) return null;
        return target;
    }
    
    private void processHypnotizer(CharacterCombatInfo character) {
        switch (character.getRow()) {
            case FIRST_ROW_INDEX -> processFirstRowHypnotizer(character);
            case SECOND_ROW_INDEX -> processSecondRowHypnotizer(character);
            case THIRD_ROW_INDEX -> processThirdRowHypnotizer(character);
            default -> throw new IllegalStateException("Unexpected value: " + character.getRow());
        }
    }
    
    private void processFirstRowHypnotizer(CharacterCombatInfo character) {
        // Enemy can't be hypnotizer
        CharacterCombatInfo target = getFirstEnemy();
        processHypnotizerTurn(character, target, character.getAggroGeneration());
    }
    
    @SuppressWarnings("DuplicatedCode")
    private void processSecondRowHypnotizer(CharacterCombatInfo character) {
        CharacterCombatInfo target = null;
        TreeSet<CharacterCombatInfo> targets = character.isEnemy() ? characters : enemies;
        for (CharacterCombatInfo info : targets) {
            if(info.isDead()) continue;
            if(info.getRow() == FIRST_ROW_INDEX) continue;
            target = info;
            break;
        }
        if(target == null && !targets.isEmpty()) target = targets.first();
        processHypnotizerTurn(character, target, character.getAggroGeneration());
    }
    
    @SuppressWarnings("DuplicatedCode")
    private void processThirdRowHypnotizer(CharacterCombatInfo character) {
        CharacterCombatInfo target = null;
        CharacterCombatInfo secondRowTarget = null;
        TreeSet<CharacterCombatInfo> targets = character.isEnemy() ? characters : enemies;
        for (CharacterCombatInfo info : targets) {
            if(info.isDead()) continue;
            if(info.getRow() == FIRST_ROW_INDEX) continue;
            if(info.getRow() == SECOND_ROW_INDEX) {
                secondRowTarget = info;
                continue;
            }
            target = info;
            break;
        }
        if(target == null) {
            if(secondRowTarget != null) target = secondRowTarget;
            else if(!targets.isEmpty()) target = targets.first();
        }
        processHypnotizerTurn(character, target, character.getAggroGeneration());
    }
    
    private void processAttackTurn(CharacterCombatInfo character, CharacterCombatInfo target, double aggroGen) {
        // Target is null if all characters are dead
        if(target == null) return;
        
        // Skip turn if the character has cooldown
        if(character.getCooldown() > 0) {
            processCooldownCharacter(character);
            return;
        }
        
        // Attack
        double reducedDamage;
        switch (character.getWeaponType()) {
            case MELEE, TANK -> reducedDamage = 1 - target.getMeleeDefense() / 100;
            case RANGED, HEALER -> reducedDamage = 1 - target.getRangedDefense() / 100;
            default -> throw new IllegalStateException("Unexpected value: " + character.getWeaponType());
        }
        CharacterStatusChange targetHealthChange = target.damage(character.getDamage() * reducedDamage);
        
        // Update Tank Aggro Generation
        CharacterStatusChange targetAggroChange = null;
        if(target.getWeaponType() == WeaponType.TANK) {
            targetAggroChange = target.increaseAggro(target.getAggroGeneration());
        }
        
        // Reset character cooldown
        CharacterStatusChange cooldownChange = character.resetCooldown();
        
        // Update character aggro
        CharacterStatusChange aggroChange = character.increaseAggro(aggroGen);
        
        // Create turn info
        TurnInfo turnInfo = new TurnInfo(
                CombatAction.ATTACK,
                character.isEnemy(),
                character.getRow(),
                character.getColumn(),
                List.of(cooldownChange, aggroChange),
                target.getRow(),
                target.getColumn(),
                targetAggroChange == null ? List.of(targetHealthChange) : List.of(targetHealthChange, targetAggroChange)
        );
        
        turnInfos.add(turnInfo);
    }
    
    private void processHealingTurn(CharacterCombatInfo character, CharacterCombatInfo target) {
        CharacterStatusChange targetHealthChange = target.heal(character.getHealingPower());
        
        // Reset character cooldown
        CharacterStatusChange cooldownChange = character.resetCooldown();
        
        // Update character aggro
        CharacterStatusChange aggroChange = character.increaseAggro(character.getAggroGeneration());
        
        // Create turn info
        TurnInfo turnInfo = new TurnInfo(
                CombatAction.HEAL,
                character.isEnemy(),
                character.getRow(),
                character.getColumn(),
                List.of(cooldownChange, aggroChange),
                target.getRow(),
                target.getColumn(),
                List.of(targetHealthChange)
        );
        
        turnInfos.add(turnInfo);
    }
    
    private void processHypnotizerTurn(CharacterCombatInfo character, CharacterCombatInfo target, double aggreGen) {
        // Target is null if all characters are dead
        if(target == null) return;
        
        // Skip turn if the character has cooldown
        if(character.getCooldown() > 0) {
            processCooldownCharacter(character);
            return;
        }
        
        // Hypnotize
        CharacterStatusChange hypnotize = null;
        SplittableRandom random = new SplittableRandom();
        double chance = random.nextDouble(0, 100);
        if(chance < character.getHypnotizationPower() - target.getHypnotizationResistance()) {
            hypnotize = target.hypnotize();
        }
        
        // Reset character cooldown
        CharacterStatusChange cooldownChange = character.resetCooldown();
        
        // Update character aggro
        CharacterStatusChange aggroChange = character.increaseAggro(aggreGen);
        
        TurnInfo turnInfo = new TurnInfo(
                CombatAction.HYPNOTIZE,
                character.isEnemy(),
                character.getRow(),
                character.getColumn(),
                List.of(cooldownChange, aggroChange),
                target.getRow(),
                target.getColumn(),
                hypnotize == null ? List.of() : List.of(hypnotize)
        );
        
        turnInfos.add(turnInfo);
    }
    
    private void processCooldownCharacter(CharacterCombatInfo character) {
        // Reduce character cooldown
        CharacterStatusChange cooldownChange = character.reduceCooldown();
        
        // Create turn info
        TurnInfo turnInfo = new TurnInfo(
                CombatAction.SKIP,
                character.isEnemy(),
                character.getRow(),
                character.getColumn(),
                List.of(cooldownChange),
                NO_TARGET,
                NO_TARGET,
                List.of()
        );
        
        turnInfos.add(turnInfo);
    }
}
