using System;
using Network;
using Network.In.Game.Combat;
using Network.In.Game.DTOEnum;
using Network.In.Game.Inventory;
using TMPro;
using UnityEditor.U2D.Animation;
using UnityEngine;
using UnityEngine.UI;

namespace UI {
    public class ItemButton : MonoBehaviour {
        [SerializeField]
        private Button _button;
        [SerializeField]
        private TMP_Text _buttonText;

        private ItemDTO _itemDTO;
        private InventoryManager _inventoryManager;

        public void Initialize(ItemDTO itemDTO, InventoryManager inventoryManager) {
            _itemDTO = itemDTO;
            _inventoryManager = inventoryManager;
            _button.onClick.RemoveAllListeners();
            switch (itemDTO.ItemType) {
                case ItemType.Weapon: _button.onClick.AddListener(OnWeaponButtonClick); break;
                case ItemType.Food: _button.onClick.AddListener(OnFoodButtonClick); break;
                case ItemType.Potion: _button.onClick.AddListener(OnPotionButtonClick); break;
                case ItemType.Material: _button.onClick.AddListener(OnMaterialButtonClick); break;
                default: throw new ArgumentOutOfRangeException(nameof(itemDTO.ItemType), itemDTO.ItemType, null);
            }
            _buttonText.text = itemDTO.Name;
        }

        private async void OnWeaponButtonClick() {
            try {
                _inventoryManager.DisableWeaponStatsPanel();
                WeaponDataDTO dto = await NetworkSystem.Instance.GetWeapon(_itemDTO.ID);
                _inventoryManager.UpdateWeaponStatsPanel(dto);
            } catch (Exception) {
                // Ignored
            }
        }

        private async void OnPotionButtonClick() {
            try {
                _inventoryManager.DisablePotionStatsPanel();
                PotionDataDTO dto = await NetworkSystem.Instance.GetPotion(_itemDTO.ID);
                _inventoryManager.UpdatePotionStatsPanel(dto);
            } catch (Exception) {
                // Ignored
            }
        }

        private async void OnFoodButtonClick() {
            try {
                _inventoryManager.DisableFoodStatsPanel();
                FoodDataDTO dto = await NetworkSystem.Instance.GetFood(_itemDTO.ID);
                _inventoryManager.UpdateFoodStatsPanel(dto);
            } catch (Exception) {
                // Ignored
            }
        }

        private async void OnMaterialButtonClick() {
            try {
                _inventoryManager.DisableMaterialsInfoPanel();
                MaterialDataDTO dto = await NetworkSystem.Instance.GetMaterial(_itemDTO.ID);
                _inventoryManager.UpdateMaterialsInfoPanel(dto);
            } catch (Exception) {
                // Ignored
            }
        }
    }
}