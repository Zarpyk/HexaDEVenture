using System;
using Combat;
using Network;
using Network.In.Game.Map;
using Network.In.Game.movement;
using UI;
using UnityEngine;
using UnityEngine.InputSystem;

namespace World {
    public class MovementManager : MonoBehaviour {
        [SerializeField]
        private WorldManager _worldManager;
        [SerializeField]
        private BattlefieldManager _battlefieldManager;
        [SerializeField]
        private InventoryManager _inventoryManager;
        [SerializeField]
        private UIManager _uiManager;
        private CameraController _cameraController;

        private Camera _mainCamera;
        private GameSystem _gameSystem;
        private NetworkSystem _networkSystem;
        private InputSystemActions _input;

        [Header("Movement Raycast")]
        [SerializeField]
        private LayerMask _groundLayer;
        [SerializeField]
        private float _maxDistance = 100f;

        [Header("Animation")]
        [SerializeField]
        private float _animationTime = 0.1f;

        private GameObject _mainCharacter;

        private void Awake() {
            _mainCamera = Camera.main;
            _cameraController = _mainCamera!.GetComponent<CameraController>();
        }

        private void Start() {
            _gameSystem = GameSystem.Instance;
            _networkSystem = NetworkSystem.Instance;
        }

        private void OnEnable() {
            _input ??= new InputSystemActions();
            _input.Player.Move.performed += OnClick;
            _input.Player.Move.Enable();
        }

        private void OnDisable() {
            _input.Player.Move.performed -= OnClick;
            _input.Player.Move.Disable();
        }

        public void Reset() {
            Destroy(_mainCharacter);
            _mainCharacter = null;
        }

        private async void OnClick(InputAction.CallbackContext context) {
            try {
                if (!_gameSystem.IsInGame() ||
                    _gameSystem.IsPaused() ||
                    _gameSystem.IsInCombat() ||
                    _gameSystem.IsLoading() ||
                    _gameSystem.IsInInventory()) return;
                Ray ray = _mainCamera.ScreenPointToRay(Mouse.current.position.ReadValue());

                if (!Physics.Raycast(ray, out RaycastHit hit, _maxDistance, _groundLayer)) return;

                // Convertir la posición del mundo a coordenadas de grid
                Vector3 worldPosition = hit.point;
                Vector2Int gridPosition = new(Mathf.RoundToInt(worldPosition.x + _worldManager.CenterOffset),
                                              Mathf.RoundToInt(worldPosition.z + _worldManager.CenterOffset));

                MovementResponseDTO response = await _networkSystem.Move(gridPosition);
                if (response == null) return;
                if (response.NewChunks) {
                    await _worldManager.LoadNewChunks();
                }
                foreach (MovementActionDTO action in response.Actions) {
                    Debug.Log($"Action: {action.OriginalPosition} -> {action.TargetPosition}");
                    await MoveCharacter(_mainCharacter, action.TargetPosition);

                    ResourceActionDTO resourceActionDTO = action.Resource;
                    if (resourceActionDTO != null) {
                        _worldManager.RemoveResource(action.TargetPosition);
                    }

                    foreach (EnemyMovementDTO enemyMovementDTO in action.EnemyMovements) {
                        Debug.Log($"Enemy: {enemyMovementDTO.OriginalPosition} -> {enemyMovementDTO.TargetPosition}");
                        GameObject enemy = _worldManager.GetEnemy(enemyMovementDTO.OriginalPosition);
                        // Enemy can be null if it was removed when unloading chunks
                        if (enemy == null) continue;
                        await MoveCharacter(enemy, enemyMovementDTO.TargetPosition);
                        _worldManager.UpdateEnemy(enemyMovementDTO);
                    }

                    Debug.Log($"StartCombat: {action.StartCombat}");
                    if (action.StartCombat) {
                        _worldManager.RemoveEnemy(action.TargetPosition);
                        _cameraController.StartCombat();
                        await _battlefieldManager.StartCombat();
                    }
                }
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        public void SetMainCharacter(GameObject character) {
            _mainCharacter = character;
        }

        private async Awaitable MoveCharacter(GameObject character, Vector2DTO targetPosition) {
            float elapsedTime = 0f;
            Vector3 startPosition = character.transform.position;
            Vector3 worldPos = new(targetPosition.X - _worldManager.CenterOffset,
                                   _worldManager.MainCharacterHeight,
                                   targetPosition.Y - _worldManager.CenterOffset);
            Quaternion startRotation = character.transform.rotation;
            Quaternion targetRotation = Quaternion.LookRotation(worldPos - startPosition);
            while (elapsedTime < _animationTime) {
                elapsedTime += Time.deltaTime;
                float t = Mathf.Clamp01(elapsedTime / _animationTime);
                character.transform.position = Vector3.Lerp(startPosition, worldPos, t);
                character.transform.rotation = Quaternion.Slerp(startRotation, targetRotation, t);
                await Awaitable.NextFrameAsync();
            }
        }
    }
}