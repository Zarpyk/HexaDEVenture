using System.Globalization;
using TMPro;
using UnityEngine;

namespace UI {
    public class MaterialInfoPanel : MonoBehaviour {
        [SerializeField]
        private TMP_Text _countText;

        public void UpdateInfo(MaterialDataDTO materialDataDTO) {
            _countText.text = materialDataDTO.count.ToString(CultureInfo.CurrentCulture);
        }
    }
}