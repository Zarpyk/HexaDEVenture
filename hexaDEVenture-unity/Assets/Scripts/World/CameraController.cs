using UnityEngine;
using UnityEngine.InputSystem;

namespace World {
    public class CameraController : MonoBehaviour {

        private InputSystemActions _input;

        [SerializeField]
        private Transform _playerTransform;

        [Header("Camera Settings")]
        [SerializeField]
        private float _smoothSpeed = 0.125f;
        [SerializeField]
        private float _height = 3.0f;
        [SerializeField]
        private float _distance = 10.0f;
        [SerializeField]
        private float _angleX = 55.0f;
        [SerializeField]
        private float _angleY = 55.0f;
        [SerializeField]
        private float _angleZ;

        [Header("Battlefield Settings")]
        [SerializeField]
        private Transform _battlefieldTransform;
        private Vector3 _originalPosition;
        private Quaternion _originalRotation;
        private bool _isBattlefieldActive;

        private Vector3 _currentVelocity;

        private void OnEnable() {
            _input ??= new InputSystemActions();
            _input.Player.RotateLeft.performed += RotateLeft;
            _input.Player.RotateRight.performed += RotateRight;
            _input.Player.RotateLeft.Enable();
            _input.Player.RotateRight.Enable();
        }

        private void OnDisable() {
            _input.Player.RotateLeft.performed -= RotateLeft;
            _input.Player.RotateRight.performed -= RotateRight;
            _input.Player.RotateLeft.Disable();
            _input.Player.RotateRight.Disable();
        }

        private void RotateLeft(InputAction.CallbackContext obj) {
            _angleY += 90f;
        }

        private void RotateRight(InputAction.CallbackContext obj) {
            _angleY -= 90f;
        }

        private void LateUpdate() {
            if (_playerTransform != null && !_isBattlefieldActive) {
                Vector3 targetPosition = _playerTransform.position;
                targetPosition.y += _height;

                Quaternion targetRotation = Quaternion.Euler(_angleX, _angleY, _angleZ);
                transform.rotation = Quaternion.Slerp(transform.rotation, targetRotation, _smoothSpeed);

                Vector3 desiredPosition = targetPosition - transform.forward * _distance;
                transform.position = Vector3.SmoothDamp(transform.position, desiredPosition, ref _currentVelocity, _smoothSpeed);
            } else if (_isBattlefieldActive) {
                transform.position = _battlefieldTransform.position;
                transform.rotation = _battlefieldTransform.rotation;
            }
        }

        public void Reset() {
            _playerTransform = null;
            _angleY = 55.0f;
            _angleX = 55.0f;
            _distance = 10.0f;
            _height = 3.0f;
            transform.position = Vector3.zero;
            transform.rotation = Quaternion.identity;
            
            _originalPosition = Vector3.zero;
            _originalRotation = Quaternion.identity;
            _isBattlefieldActive = false;
        }

        public void SetPlayerTransform(Transform mainCharacterTransform) {
            _playerTransform = mainCharacterTransform;
        }

        public void StartCombat() {
            _isBattlefieldActive = true;
            _battlefieldTransform.gameObject.SetActive(true);
            _originalPosition = transform.position;
            _originalRotation = transform.rotation;
            transform.position = _battlefieldTransform.position;
            transform.rotation = _battlefieldTransform.rotation;
        }
    
        public void EndCombat() {
            _isBattlefieldActive = false;
            _battlefieldTransform.gameObject.SetActive(false);
            transform.position = _originalPosition;
            transform.rotation = _originalRotation;
        }
    }
}