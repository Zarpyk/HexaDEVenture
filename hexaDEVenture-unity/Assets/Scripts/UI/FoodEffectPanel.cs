using System.Globalization;
using Network.In.Game.Inventory;
using TMPro;
using UnityEngine;

namespace UI {
    public class FoodEffectPanel : MonoBehaviour {
        [SerializeField]
        private TMP_Text _regenerationText;

        public void UpdateStats(FoodDataDTO foodDataDTO) {
            _regenerationText.text = "+" + foodDataDTO.HealthPoints.ToString(CultureInfo.CurrentCulture);
        }
    }
}