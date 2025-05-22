using System.Globalization;
using Network.In.Game.DTOEnum;
using Network.In.Game.Inventory;
using TMPro;
using UnityEngine;

namespace UI {
    public class PotionEffectPanel : MonoBehaviour {
        [SerializeField]
        private TMP_Text _potionTypeText;
        [SerializeField]
        private TMP_Text _potionEffectText;

        public void UpdateStats(PotionDataDTO potionDataDTO) {
            _potionTypeText.text = potionDataDTO.PotionType.ToString();
            _potionEffectText.text = "+" + potionDataDTO.PotionPower.ToString(CultureInfo.CurrentCulture);
            if (potionDataDTO.PotionType == PotionType.Defense) _potionEffectText.text += "%";
        }
    }
}