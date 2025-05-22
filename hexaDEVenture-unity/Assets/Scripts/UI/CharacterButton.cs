using System;
using Network;
using Network.In.Game.Combat;
using Network.In.Game.Inventory;
using TMPro;
using UnityEngine;
using UnityEngine.UI;

namespace UI {
    public class CharacterButton : MonoBehaviour {
        [SerializeField]
        private Button _button;
        [SerializeField]
        private TMP_Text _buttonText;

        private CharacterDTO _characterDto;
        private InventoryManager _inventoryManager;

        public void Initialize(CharacterDTO characterData, InventoryManager inventoryManager) {
            _characterDto = characterData;
            _inventoryManager = inventoryManager;
            _button.onClick.RemoveAllListeners();
            _button.onClick.AddListener(OnButtonClick);
            _buttonText.text = characterData.Name;
        }

        private async void OnButtonClick() {
            try {
                _inventoryManager.DisableCharacterStatsPanel();
                CharacterDataDTO characterData = await NetworkSystem.Instance.GetCharacter(_characterDto.ID);
                _inventoryManager.UpdateCharacterStatsPanel(characterData);
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }
    }
}