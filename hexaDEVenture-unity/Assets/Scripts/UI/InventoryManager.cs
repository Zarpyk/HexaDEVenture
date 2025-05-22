using System;
using System.Collections.Generic;
using Network;
using Network.In.Game.Combat;
using Network.In.Game.DTOEnum;
using Network.In.Game.Inventory;
using TMPro;
using UnityEngine;
using UnityEngine.InputSystem;
using UnityEngine.Pool;
using UnityEngine.UI;
using World;

namespace UI {
    public class InventoryManager : MonoBehaviour {
        private GameSystem _gameSystem;
        private NetworkSystem _networkSystem;
        private InputSystemActions _input;

        [Header("Characters Panel")]
        [SerializeField]
        private GameObject _characterBackgroundPanel;
        [SerializeField]
        private GameObject _characterPanel;
        [SerializeField]
        private GameObject _characterStatsPanel;
        private bool _isCharacterPanelActive;
        [SerializeField]
        private RectTransform _characterContentPanel;
        [SerializeField]
        private CharacterButton _characterButtonPrefab;
        public CharacterDataDTO SelectedCharacter { get; private set; }

        [Header("Character Stats Panel")]
        [SerializeField]
        private GameObject _characterStatsContentPanel;
        [SerializeField]
        private WeaponStatsPanel _characterWeaponStatsPanel;
        [SerializeField]
        private TMP_Text _healthText;
        [SerializeField]
        private TMP_Text _speedText;

        [Header("Items Panel")]
        [SerializeField]
        private ItemButton _itemButtonPrefab;

        [Header("Weapons Panel")]
        [SerializeField]
        private GameObject _weaponsBackgroundPanel;
        [SerializeField]
        private GameObject _weaponsPanel;
        [SerializeField]
        private GameObject _weaponStatsPanel;
        private bool _isWeaponPanelActive;
        [SerializeField]
        private RectTransform _weaponsContentPanel;
        [SerializeField]
        private WeaponStatsPanel _weaponStatsContentPanel;
        private WeaponDataDTO _selectedWeapon;

        [Header("Potions Panel")]
        [SerializeField]
        private GameObject _potionBackgroundPanel;
        [SerializeField]
        private GameObject _potionPanel;
        [SerializeField]
        private GameObject _potionEffectPanel;
        private bool _isPotionPanelActive;
        [SerializeField]
        private RectTransform _potionContentPanel;
        [SerializeField]
        private PotionEffectPanel _potionEffectContentPanel;
        private PotionDataDTO _selectedPotion;

        [Header("Food Panel")]
        [SerializeField]
        private GameObject _foodBackgroundPanel;
        [SerializeField]
        private GameObject _foodPanel;
        [SerializeField]
        private GameObject _foodEffectPanel;
        private bool _isFoodPanelActive;
        [SerializeField]
        private RectTransform _foodContentPanel;
        [SerializeField]
        private FoodEffectPanel _foodEffectContentPanel;
        private FoodDataDTO _selectedFood;

        [Header("Crafting Panel")]
        [SerializeField]
        private GameObject _materialsBackgroundPanel;
        [SerializeField]
        private GameObject _materialsPanel;
        [SerializeField]
        private GameObject _materialInfoPanel;
        [SerializeField]
        private RectTransform _materialsContentPanel;
        [SerializeField]
        private MaterialInfoPanel _materialInfoContentPanel;
        [SerializeField]
        private GameObject _recipesBackgroundPanel;
        [SerializeField]
        private GameObject _recipesPanel;
        [SerializeField]
        private GameObject _recipesInfoPanel;
        [SerializeField]
        private RectTransform _recipesContentPanel;
        [SerializeField]
        private RecipeInfoPanel _recipeInfoContentPanel;
        [SerializeField]
        private RecipeButton _recipeButtonPrefab;
        private bool _isCraftingPanelActive;
        private int _selectedRecipe;

        [Header("Usage Buttons")]
        [SerializeField]
        private Button _equipWeaponButton;
        [SerializeField]
        private Button _unequipWeaponButton;
        [SerializeField]
        private Button _usePotionButton;
        [SerializeField]
        private Button _eatFoodButton;
        [SerializeField]
        private Button _craftButton;

        private ObjectPool<CharacterButton> _characterPool;
        private List<CharacterButton> _characterButtons = new();
        private ObjectPool<ItemButton> _itemPool;
        private List<ItemButton> _itemButtons = new();
        private ObjectPool<RecipeButton> _recipePool;
        private List<RecipeButton> _recipeButtons = new();

        private void OnEnable() {
            _input = new InputSystemActions();
            _input.Inventory.CharacterInventory.performed += OpenCharacterInventory;
            _input.Inventory.WeaponInventory.performed += OpenWeaponInventory;
            _input.Inventory.PotionInventory.performed += OpenPotionInventory;
            _input.Inventory.FoodInventory.performed += OpenFoodInventory;
            _input.Inventory.CraftInventory.performed += OpenCraftInventory;
            _input.Inventory.Enable();

            _gameSystem = GameSystem.Instance;
            _gameSystem.OnGameStateChanged += OnGameStateChanged;
        }

        private void OnDisable() {
            _input.Inventory.CharacterInventory.performed -= OpenCharacterInventory;
            _input.Inventory.WeaponInventory.performed -= OpenWeaponInventory;
            _input.Inventory.PotionInventory.performed -= OpenPotionInventory;
            _input.Inventory.FoodInventory.performed -= OpenFoodInventory;
            _input.Inventory.CraftInventory.performed -= OpenCraftInventory;
            _input.Inventory.Disable();

            _gameSystem.OnGameStateChanged -= OnGameStateChanged;
        }

        private void Awake() {
            _equipWeaponButton.onClick.AddListener(EquipWeapon);
            _unequipWeaponButton.onClick.AddListener(UnequipWeapon);
            _usePotionButton.onClick.AddListener(UsePotion);
            _eatFoodButton.onClick.AddListener(EatFood);
            _craftButton.onClick.AddListener(CraftItem);

            _characterPool = new ObjectPool<CharacterButton>(() => Instantiate(_characterButtonPrefab, _characterContentPanel),
                                                             button => button.gameObject.SetActive(true),
                                                             button => button.gameObject.SetActive(false),
                                                             button => Destroy(button.gameObject));

            _itemPool = new ObjectPool<ItemButton>(() => Instantiate(_itemButtonPrefab),
                                                   button => button.gameObject.SetActive(true),
                                                   button => button.gameObject.SetActive(false),
                                                   button => Destroy(button.gameObject));

            _recipePool = new ObjectPool<RecipeButton>(() => Instantiate(_recipeButtonPrefab),
                                                       button => button.gameObject.SetActive(true),
                                                       button => button.gameObject.SetActive(false),
                                                       button => Destroy(button.gameObject));
        }

        private void Start() {
            _networkSystem = NetworkSystem.Instance;
            _characterStatsContentPanel.SetActive(false);
            _weaponStatsContentPanel.gameObject.SetActive(false);
            _potionEffectContentPanel.gameObject.SetActive(false);
            _foodEffectContentPanel.gameObject.SetActive(false);
            DisableAllInventory();
        }

        private void OnGameStateChanged(GameState oldState, GameState state) {
            if (oldState.HasFlag(GameState.InGame) && !state.HasFlag(GameState.InGame)) {
                DisableAllInventory();
            }
        }

        private void DisableAllInventory() {
            DisableCharacterInventory();
            DisableWeaponInventory();
            DisablePotionInventory();
            DisableFoodInventory();
            DisableCraftInventory();
        }

        private async void OpenCharacterInventory(InputAction.CallbackContext obj) {
            try {
                if (!_gameSystem.IsInGame() || _gameSystem.IsPaused() || _gameSystem.IsLoading()) return;
                if (_isCharacterPanelActive) {
                    DisableCharacterInventory();
                    return;
                }

                if (_isCraftingPanelActive) DisableCraftInventory();

                await EnableInventory(_characterPanel, _characterStatsPanel, _characterBackgroundPanel);
                _isCharacterPanelActive = true;
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private async void OpenWeaponInventory(InputAction.CallbackContext obj) {
            try {
                if (!_gameSystem.IsInGame() || _gameSystem.IsPaused() || _gameSystem.IsLoading() ||
                    _gameSystem.IsInCombat()) return;

                // Close the weapons panel if it's already open
                if (_isWeaponPanelActive) {
                    DisableWeaponInventory();
                    return;
                }

                // Close other panels
                if (_isPotionPanelActive) DisablePotionInventory();
                if (_isFoodPanelActive) DisableFoodInventory();
                if (_isCraftingPanelActive) DisableCraftInventory();

                // Open weapons panel
                await EnableInventory(_weaponsPanel, _weaponStatsPanel, _weaponsBackgroundPanel);
                _isWeaponPanelActive = true;
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private async void OpenPotionInventory(InputAction.CallbackContext obj) {
            try {
                if (!_gameSystem.IsInGame() || _gameSystem.IsPaused() || _gameSystem.IsLoading() ||
                    _gameSystem.IsInCombat()) return;

                // Close the potions panel if it's already open
                if (_isPotionPanelActive) {
                    DisablePotionInventory();
                    return;
                }

                // Close other panels
                if (_isWeaponPanelActive) DisableWeaponInventory();
                if (_isFoodPanelActive) DisableFoodInventory();
                if (_isCraftingPanelActive) DisableCraftInventory();

                // Open potions panel
                await EnableInventory(_potionPanel, _potionEffectPanel, _potionBackgroundPanel);
                _isPotionPanelActive = true;
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private async void OpenFoodInventory(InputAction.CallbackContext obj) {
            try {
                if (!_gameSystem.IsInGame() || _gameSystem.IsPaused() || _gameSystem.IsLoading() ||
                    _gameSystem.IsInCombat()) return;

                // Close the food panel if it's already open
                if (_isFoodPanelActive) {
                    DisableFoodInventory();
                    return;
                }

                // Close other panels
                if (_isWeaponPanelActive) DisableWeaponInventory();
                if (_isPotionPanelActive) DisablePotionInventory();
                if (_isCraftingPanelActive) DisableCraftInventory();

                // Open food panel
                await EnableInventory(_foodPanel, _foodEffectPanel, _foodBackgroundPanel);
                _isFoodPanelActive = true;
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private async void OpenCraftInventory(InputAction.CallbackContext obj) {
            try {
                if (!_gameSystem.IsInGame() || _gameSystem.IsPaused() || _gameSystem.IsLoading() ||
                    _gameSystem.IsInCombat()) return;

                // Close the food panel if it's already open
                if (_isCraftingPanelActive) {
                    DisableCraftInventory();
                    return;
                }

                // Close other panels
                if (_isCharacterPanelActive) DisableCharacterInventory();
                if (_isWeaponPanelActive) DisableWeaponInventory();
                if (_isPotionPanelActive) DisablePotionInventory();
                if (_isFoodPanelActive) DisableFoodInventory();

                int recipesCount = await _networkSystem.GetRecipesCount();
                RecipesDTO recipesDTO = await _networkSystem.GetRecipes(1, recipesCount);
                for (int i = 0; i < recipesDTO.Recipes.Length; i++) {
                    RecipeDTO recipe = recipesDTO.Recipes[i];
                    RecipeButton recipeButton = _recipePool.Get();
                    recipeButton.transform.SetParent(_recipesContentPanel);
                    recipeButton.transform.SetAsLastSibling();
                    recipeButton.Initialize(recipe, i, this);
                    _recipeButtons.Add(recipeButton);
                }

                // Open crafting panel + Materials panel
                _recipesPanel.SetActive(true);
                _recipesInfoPanel.SetActive(true);
                _recipesBackgroundPanel.SetActive(true);
                _materialsPanel.SetActive(true);
                _materialInfoPanel.SetActive(true);
                _materialsBackgroundPanel.SetActive(true);

                _gameSystem.OpenInventory();
                _isCraftingPanelActive = true;
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private void DisableCharacterInventory() {
            DisableInventory(_characterPanel, _characterStatsPanel, _characterBackgroundPanel);
            _isCharacterPanelActive = false;
            if (!_isWeaponPanelActive && !_isPotionPanelActive && !_isFoodPanelActive && !_isCraftingPanelActive)
                _gameSystem.CloseInventory();
        }

        private void DisableWeaponInventory() {
            DisableInventory(_weaponsPanel, _weaponStatsPanel, _weaponsBackgroundPanel);
            _isWeaponPanelActive = false;
            if (!_isCharacterPanelActive && !_isPotionPanelActive && !_isFoodPanelActive && !_isCraftingPanelActive)
                _gameSystem.CloseInventory();
        }

        private void DisablePotionInventory() {
            DisableInventory(_potionPanel, _potionEffectPanel, _potionBackgroundPanel);
            _isPotionPanelActive = false;
            if (!_isCharacterPanelActive && !_isWeaponPanelActive && !_isFoodPanelActive && !_isCraftingPanelActive)
                _gameSystem.CloseInventory();
        }

        private void DisableFoodInventory() {
            DisableInventory(_foodPanel, _foodEffectPanel, _foodBackgroundPanel);
            _isFoodPanelActive = false;
            if (!_isCharacterPanelActive && !_isWeaponPanelActive && !_isPotionPanelActive && !_isCraftingPanelActive)
                _gameSystem.CloseInventory();
        }

        private void DisableCraftInventory() {
            DisableInventory(_materialsPanel, _materialInfoPanel, _materialsBackgroundPanel);
            DisableInventory(_recipesPanel, _recipesInfoPanel, _recipesBackgroundPanel);
            _isCraftingPanelActive = false;
            if (!_isCharacterPanelActive && !_isWeaponPanelActive && !_isPotionPanelActive && !_isFoodPanelActive)
                _gameSystem.CloseInventory();
        }

        public async Awaitable UpdateInventory() {
            InventoryDTO inventoryDTO = await _networkSystem.GetInventory();
            ClearInventory();
            foreach (ItemDTO item in inventoryDTO.Items) {
                switch (item.ItemType) {
                    case ItemType.Weapon: AddWeapon(item); break;
                    case ItemType.Food: AddFood(item); break;
                    case ItemType.Potion: AddPotion(item); break;
                    case ItemType.Material: AddMaterial(item); break;
                    default: throw new ArgumentOutOfRangeException();
                }
            }
            foreach (CharacterDTO character in inventoryDTO.Characters) {
                AddCharacter(character);
            }
        }

        private void ClearInventory() {
            foreach (CharacterButton characterButton in _characterButtons) {
                _characterPool.Release(characterButton);
            }
            _characterButtons.Clear();
            foreach (ItemButton weaponButton in _itemButtons) {
                _itemPool.Release(weaponButton);
            }
            _itemButtons.Clear();
            foreach (RecipeButton recipeButton in _recipeButtons) {
                _recipePool.Release(recipeButton);
            }
            _recipeButtons.Clear();
            DisableCharacterStatsPanel();
            DisableWeaponStatsPanel();
            DisablePotionStatsPanel();
            DisableFoodStatsPanel();
            DisableMaterialsInfoPanel();
            DisableRecipeInfoPanel();
        }

        public void AddCharacter(CharacterDTO character) {
            CharacterButton characterButton = _characterPool.Get();
            characterButton.transform.SetAsLastSibling();
            characterButton.Initialize(character, this);
            _characterButtons.Add(characterButton);
        }

        private void AddWeapon(ItemDTO item) {
            ItemButton itemButton = _itemPool.Get();
            itemButton.transform.SetParent(_weaponsContentPanel);
            itemButton.transform.SetAsLastSibling();
            itemButton.Initialize(item, this);
            _itemButtons.Add(itemButton);
        }

        private void AddPotion(ItemDTO item) {
            ItemButton itemButton = _itemPool.Get();
            itemButton.transform.SetParent(_potionContentPanel);
            itemButton.transform.SetAsLastSibling();
            itemButton.Initialize(item, this);
            _itemButtons.Add(itemButton);
        }

        private void AddFood(ItemDTO item) {
            ItemButton itemButton = _itemPool.Get();
            itemButton.transform.SetParent(_foodContentPanel);
            itemButton.transform.SetAsLastSibling();
            itemButton.Initialize(item, this);
            _itemButtons.Add(itemButton);
        }

        private void AddMaterial(ItemDTO item) {
            ItemButton itemButton = _itemPool.Get();
            itemButton.transform.SetParent(_materialsContentPanel);
            itemButton.transform.SetAsLastSibling();
            itemButton.Initialize(item, this);
            _itemButtons.Add(itemButton);
        }

        public void UpdateCharacterStatsPanel(CharacterDataDTO characterData) {
            // Update character stats
            CharacterChangedStatDTO stats = characterData.ChangedStats;
            _healthText.text = $"{stats.Health:F}/{characterData.Health:F} (+{stats.BoostHealth}) ";
            _speedText.text = $"{characterData.Speed} (+{stats.BoostSpeed})";

            // Update weapon stats
            _characterWeaponStatsPanel.UpdateStats(characterData);

            // Update equip/unequip buttons
            _unequipWeaponButton.gameObject.SetActive(characterData.Weapon.Skin != -1);
            _equipWeaponButton.gameObject.SetActive(_selectedWeapon != null);

            _characterStatsContentPanel.SetActive(true);
            SelectedCharacter = characterData;
        }

        public void DisableCharacterStatsPanel() {
            _characterStatsContentPanel.SetActive(false);
            SelectedCharacter = null;
            _unequipWeaponButton.gameObject.SetActive(false);
        }

        public void UpdateWeaponStatsPanel(WeaponDataDTO weaponDataDTO) {
            _weaponStatsContentPanel.UpdateStats(weaponDataDTO);
            _weaponStatsContentPanel.gameObject.SetActive(true);
            _selectedWeapon = weaponDataDTO;
            _equipWeaponButton.gameObject.SetActive(SelectedCharacter != null);
        }

        public void DisableWeaponStatsPanel() {
            _weaponStatsContentPanel.gameObject.SetActive(false);
            _selectedWeapon = null;
            _equipWeaponButton.gameObject.SetActive(false);
        }

        public void UpdatePotionStatsPanel(PotionDataDTO potionDataDTO) {
            _potionEffectContentPanel.UpdateStats(potionDataDTO);
            _potionEffectContentPanel.gameObject.SetActive(true);
            _selectedPotion = potionDataDTO;
            _usePotionButton.gameObject.SetActive(SelectedCharacter != null);
        }

        public void DisablePotionStatsPanel() {
            _potionEffectContentPanel.gameObject.SetActive(false);
            _selectedPotion = null;
            _usePotionButton.gameObject.SetActive(false);
        }

        public void UpdateFoodStatsPanel(FoodDataDTO foodDataDTO) {
            _foodEffectContentPanel.UpdateStats(foodDataDTO);
            _foodEffectContentPanel.gameObject.SetActive(true);
            _selectedFood = foodDataDTO;
            _eatFoodButton.gameObject.SetActive(SelectedCharacter != null);
        }

        public void DisableFoodStatsPanel() {
            _foodEffectContentPanel.gameObject.SetActive(false);
            _selectedFood = null;
            _eatFoodButton.gameObject.SetActive(false);
        }

        public void UpdateRecipeInfoPanel(RecipeDTO recipeDTO, int index) {
            _recipeInfoContentPanel.UpdateInfo(recipeDTO);
            _recipeInfoContentPanel.gameObject.SetActive(true);
            _selectedRecipe = index;
        }

        public void DisableRecipeInfoPanel() {
            _recipeInfoContentPanel.gameObject.SetActive(false);
            _selectedRecipe = -1;
        }

        public void UpdateMaterialsInfoPanel(MaterialDataDTO materialDataDTO) {
            _materialInfoContentPanel.UpdateInfo(materialDataDTO);
            _materialInfoContentPanel.gameObject.SetActive(true);
        }

        public void DisableMaterialsInfoPanel() {
            _materialInfoContentPanel.gameObject.SetActive(false);
        }

        private async void EquipWeapon() {
            try {
                if (SelectedCharacter == null || _selectedWeapon == null) return;

                await _networkSystem.EquipWeapon(SelectedCharacter.ID, _selectedWeapon.ID);
                await UpdateInventory();
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private async void UnequipWeapon() {
            try {
                // Skin -1 is the default weapon, so it cannot be unequipped
                if (SelectedCharacter == null || SelectedCharacter.Weapon.Skin == -1) return;

                await _networkSystem.UnequipWeapon(SelectedCharacter.ID);
                await UpdateInventory();
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private async void UsePotion() {
            try {
                if (SelectedCharacter == null || _selectedPotion == null) return;

                await _networkSystem.UseItem(SelectedCharacter.ID, _selectedPotion.ID);
                await UpdateInventory();
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private async void EatFood() {
            try {
                if (SelectedCharacter == null || _selectedFood == null) return;

                await _networkSystem.UseItem(SelectedCharacter.ID, _selectedFood.ID);
                await UpdateInventory();
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private async void CraftItem() {
            try {
                await _networkSystem.Craft(_selectedRecipe, 1);
                await UpdateInventory();
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private async Awaitable EnableInventory(GameObject mainPanel, GameObject subPanel, GameObject backgroundPanel) {
            _gameSystem.OpenInventory();
            await UpdateInventory();
            mainPanel?.SetActive(true);
            subPanel?.SetActive(true);
            backgroundPanel?.SetActive(true);
        }

        private void DisableInventory(GameObject mainPanel, GameObject subPanel, GameObject backgroundPanel) {
            mainPanel?.SetActive(false);
            subPanel?.SetActive(false);
            backgroundPanel?.SetActive(false);
        }

    }
}