using Network.In.Game.Combat;
using UnityEngine;
using UnityEngine.UI;

namespace Combat {
    public class CharacterController : MonoBehaviour {
        [SerializeField]
        private Image _healthBar;
        [SerializeField]
        private Color _hypnotizedColor = new(1f, 0.7f, 1f, 1f);

        public CharacterDataDTO CharacterData { get; private set; }

        private SkinnedMeshRenderer[] _renderer;
        private bool _newMaterial;

        private void Awake() {
            _renderer = GetComponentsInChildren<SkinnedMeshRenderer>();
        }

        public void UpdateHealth(double health) {
            _healthBar.fillAmount = Mathf.Clamp01((float) (health / CharacterData.Health));
        }

        public void Hypnotize(bool isHypnotized) {
            foreach (SkinnedMeshRenderer skinnedRenderer in _renderer) {
                if (_newMaterial) {
                    skinnedRenderer.material.color = isHypnotized ? _hypnotizedColor : Color.white;
                } else {
                    skinnedRenderer.material = new Material(skinnedRenderer.material) {
                        color = isHypnotized ? _hypnotizedColor : Color.white
                    };
                    _newMaterial = true;
                }
            }
        }

        public void SetData(CharacterDataDTO characterDTO) {
            CharacterData = characterDTO;
            UpdateHealth(characterDTO.ChangedStats.Health);
            Hypnotize(characterDTO.ChangedStats.Hypnotized);
        }
    }

}