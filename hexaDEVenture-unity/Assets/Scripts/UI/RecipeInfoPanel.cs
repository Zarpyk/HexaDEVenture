using System.Globalization;
using Network.In.Game.Inventory;
using TMPro;
using UnityEngine;

namespace UI {
    public class RecipeInfoPanel : MonoBehaviour {
        [SerializeField]
        private TMP_Text _recipeMaterialsText;

        public void UpdateInfo(RecipeDTO recipeDTO) {
            _recipeMaterialsText.text = string.Empty;
            foreach (RecipeResourceDTO resource in recipeDTO.Materials) {
                _recipeMaterialsText.text += $"{resource.ID} x{resource.Count}\n";
            }
        }
    }
}