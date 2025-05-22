using Network.In.Game.Inventory;
using TMPro;
using UnityEngine;
using UnityEngine.UI;

namespace UI {
    public class RecipeButton : MonoBehaviour {
        [SerializeField]
        private Button _button;
        [SerializeField]
        private TMP_Text _buttonText;

        private RecipeDTO _recipeDTO;
        private int _index;
        private InventoryManager _inventoryManager;

        public void Initialize(RecipeDTO recipeDTO, int index, InventoryManager inventoryManager) {
            _recipeDTO = recipeDTO;
            _index = index;
            _inventoryManager = inventoryManager;
            _button.onClick.RemoveAllListeners();
            _button.onClick.AddListener(OnButtonClick);
            _buttonText.text = recipeDTO.ResultId;
        }

        private void OnButtonClick() {
            _inventoryManager.UpdateRecipeInfoPanel(_recipeDTO, _index);
        }
    }
}