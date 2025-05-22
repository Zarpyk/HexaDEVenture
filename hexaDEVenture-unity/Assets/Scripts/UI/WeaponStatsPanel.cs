using System.Globalization;
using Network.In.Game.Combat;
using Network.In.Game.DTOEnum;
using TMPro;
using UnityEngine;

namespace UI {
    public class WeaponStatsPanel : MonoBehaviour {
        [SerializeField]
        private TMP_Text _weaponTypeText;
        [SerializeField]
        private TMP_Text _damageText;
        [SerializeField]
        private TMP_Text _meleeDefenseText;
        [SerializeField]
        private TMP_Text _rangedDefenseText;
        [SerializeField]
        private TMP_Text _cooldownText;
        [SerializeField]
        private TMP_Text _aggroGenerationText;
        [SerializeField]
        private TMP_Text _initialAggroText;
        [SerializeField]
        private TMP_Text _healingPowerHeaderText;
        [SerializeField]
        private TMP_Text _healingPowerText;
        [SerializeField]
        private TMP_Text _hypnotizationPowerHeaderText;
        [SerializeField]
        private TMP_Text _hypnotizationPowerText;

        public void UpdateStats(CharacterDataDTO characterDataDTO) {
            UpdateStats(characterDataDTO.Weapon);
            CharacterChangedStatDTO stats = characterDataDTO.ChangedStats;
            _damageText.text += $" (+{stats.BoostStrength})";
            _meleeDefenseText.text += $" (+{stats.BoostDefense}%)";
            _rangedDefenseText.text += $" (+{stats.BoostDefense}%)";
            
        }

        public void UpdateStats(WeaponDataDTO weaponDataDTO) {
            _weaponTypeText.text = $"{weaponDataDTO.Name} ({weaponDataDTO.WeaponType:G})";
            _damageText.text = weaponDataDTO.Damage.ToString(CultureInfo.CurrentCulture);
            _meleeDefenseText.text = weaponDataDTO.MeleeDefense.ToString(CultureInfo.CurrentCulture) + "%";
            _rangedDefenseText.text = weaponDataDTO.RangedDefense.ToString(CultureInfo.CurrentCulture) + "%";
            _cooldownText.text = weaponDataDTO.Cooldown.ToString(CultureInfo.CurrentCulture) + " turn";
            _aggroGenerationText.text = weaponDataDTO.AggroGeneration.ToString(CultureInfo.CurrentCulture);
            _initialAggroText.text = weaponDataDTO.InitialAggro.ToString(CultureInfo.CurrentCulture);
            switch (weaponDataDTO.WeaponType) {
                case WeaponType.Healer:
                    _healingPowerText.text = weaponDataDTO.HealingPower.ToString(CultureInfo.CurrentCulture);
                    _healingPowerHeaderText.gameObject.SetActive(true);
                    _healingPowerText.gameObject.SetActive(true);
                    _hypnotizationPowerHeaderText.gameObject.SetActive(false);
                    _hypnotizationPowerText.gameObject.SetActive(false);
                    break;
                case WeaponType.Hypnotizer:
                    _hypnotizationPowerText.text = weaponDataDTO.HypnotizationPower.ToString(CultureInfo.CurrentCulture);
                    _hypnotizationPowerHeaderText.gameObject.SetActive(true);
                    _hypnotizationPowerText.gameObject.SetActive(true);
                    _healingPowerHeaderText.gameObject.SetActive(false);
                    _healingPowerText.gameObject.SetActive(false);
                    break;
                case WeaponType.Melee:
                case WeaponType.Ranged:
                case WeaponType.Tank:
                default:
                    _healingPowerHeaderText.gameObject.SetActive(false);
                    _healingPowerText.gameObject.SetActive(false);
                    _hypnotizationPowerHeaderText.gameObject.SetActive(false);
                    _hypnotizationPowerText.gameObject.SetActive(false);
                    break;
            }
        }
    }
}