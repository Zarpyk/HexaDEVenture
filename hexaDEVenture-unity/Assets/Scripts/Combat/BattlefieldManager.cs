using System;
using System.Collections.Generic;
using Network;
using Network.In.Game.Combat;
using Network.In.Game.DTOEnum;
using UI;
using UnityEngine;
using UnityEngine.InputSystem;
using World;

namespace Combat {
    public class BattlefieldManager : MonoBehaviour {
        private const int RowCount = 3;
        private const int ColumnCount = 4;

        private GameSystem _gameSystem;
        private NetworkSystem _networkSystem;
        private Camera _mainCamera;
        private CameraController _camera;
        private InputSystemActions _input;

        [Header("Battlefield Settings")]
        [SerializeField]
        private WorldManager _worldManager;
        [SerializeField]
        private InventoryManager _inventoryManager;
        [SerializeField]
        private UIManager _uiManager;
        [SerializeField]
        private Transform[] _playerPositions;
        [SerializeField]
        private Transform[] _enemyPositions;
        [SerializeField]
        public float CharactersHeight = 0.1f;

        [Header("Prefabs")]
        [SerializeField]
        private CharacterController _characterPrefab;
        [SerializeField]
        private Vector3 _characterRotation = Vector3.forward;
        [SerializeField]
        private CharacterController _enemyPrefab;
        [SerializeField]
        private Vector3 _enemyRotation = -Vector3.forward;

        [Header("Movement Raycast")]
        [SerializeField]
        private LayerMask _groundLayer;
        [SerializeField]
        private float _maxDistance = 100f;

        private CharacterController[][] _playerCharacters;
        private CharacterController[][] _enemyCharacters;

        private int _placedCharactersCount;
        private bool _combatStarted;

        private void Start() {
            _networkSystem = NetworkSystem.Instance;
            _mainCamera = Camera.main;
            _camera = _mainCamera!.GetComponent<CameraController>();

            _playerCharacters = new CharacterController[RowCount][];
            _enemyCharacters = new CharacterController[RowCount][];
            for (int i = 0; i < RowCount; i++) {
                _playerCharacters[i] = new CharacterController[ColumnCount];
                _enemyCharacters[i] = new CharacterController[ColumnCount];
            }

            for (int i = 0; i < RowCount; i++) {
                for (int j = 0; j < ColumnCount; j++) {
                    CombatTerrain combatTerrain = _playerPositions[i * ColumnCount + j].GetComponent<CombatTerrain>();
                    combatTerrain.SetUpTerrain(i, j);
                }
            }
        }

        private void OnEnable() {
            _input ??= new InputSystemActions();
            _input.Combat.Place.performed += OnPlace;
            _input.Combat.Remove.performed += OnRemove;
            _input.Combat.Enable();

            _gameSystem = GameSystem.Instance;
            _gameSystem.OnGameStateChanged += OnGameStateChanged;
        }

        private void OnDisable() {
            _input.Combat.Place.performed -= OnPlace;
            _input.Combat.Remove.performed -= OnRemove;
            _input.Combat.Disable();

            _gameSystem.OnGameStateChanged -= OnGameStateChanged;
        }

        private void OnGameStateChanged(GameState oldState, GameState state) {
            if (oldState.HasFlag(GameState.InGame) && !state.HasFlag(GameState.InGame)) {
                Reset();
            }
        }

        private async void OnPlace(InputAction.CallbackContext context) {
            try {
                Debug.Log($"Place character: {_combatStarted} - {_gameSystem.IsInGame()} - {_gameSystem.IsPaused()} - {_gameSystem.IsInCombat()} - {_gameSystem.IsLoading()}");
                if (_combatStarted) return;
                if (!_gameSystem.IsInGame() ||
                    _gameSystem.IsPaused() ||
                    !_gameSystem.IsInCombat() ||
                    _gameSystem.IsLoading()) return;
                Ray ray = _mainCamera.ScreenPointToRay(Mouse.current.position.ReadValue());

                if (!Physics.Raycast(ray, out RaycastHit hit, _maxDistance, _groundLayer)) return;

                if (!hit.transform.gameObject.TryGetComponent(out CombatTerrain combatTerrain)) return;

                CharacterDataDTO selectedCharacter = _inventoryManager.SelectedCharacter;

                if (selectedCharacter == null) {
                    Debug.LogWarning("Selected character is null");
                    return;
                }

                int row = combatTerrain.Row;
                int column = combatTerrain.Column;
                bool placeCharacter = await _networkSystem.PlaceCharacter(row,
                                                                          column,
                                                                          selectedCharacter.ID);
                if (placeCharacter) {
                    Vector3 position = _playerPositions[row * ColumnCount + column].position;
                    position.y += CharactersHeight;
                    _playerCharacters[row][column] = Instantiate(_characterPrefab,
                                                                 position,
                                                                 Quaternion.LookRotation(_characterRotation));
                    _playerCharacters[row][column].SetData(selectedCharacter);
                    await _inventoryManager.UpdateInventory();
                    _placedCharactersCount++;
                    _gameSystem.PlaceCharacter();
                }
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        private async void OnRemove(InputAction.CallbackContext obj) {
            try {
                if (_combatStarted) return;
                if (!_gameSystem.IsInGame() ||
                    _gameSystem.IsPaused() ||
                    !_gameSystem.IsInCombat() ||
                    _gameSystem.IsLoading()) return;
                Ray ray = _mainCamera.ScreenPointToRay(Mouse.current.position.ReadValue());

                if (!Physics.Raycast(ray, out RaycastHit hit, _maxDistance, _groundLayer)) return;

                if (!hit.transform.gameObject.TryGetComponent(out CombatTerrain combatTerrain)) return;

                CharacterController characterController = _playerCharacters[combatTerrain.Row][combatTerrain.Column];
                if (characterController != null) {
                    bool removeCharacter = await _networkSystem.RemoveCharacter(combatTerrain.Row, combatTerrain.Column);
                    if (removeCharacter) {
                        Destroy(characterController.gameObject);
                        _playerCharacters[combatTerrain.Row][combatTerrain.Column] = null;
                        await _inventoryManager.UpdateInventory();
                        _placedCharactersCount--;
                        if (_placedCharactersCount <= 0) _gameSystem.RemoveCharacter();
                    }
                }
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        public async Awaitable StartCombat(CombatInfoDTO combatInfoDTO = null) {
            try {
                combatInfoDTO ??= await _networkSystem.GetCombatStatus();
                if (combatInfoDTO == null) {
                    Debug.LogError("CombatInfoDTO is null");
                    return;
                }

                // Clear previous characters
                for (int i = 0; i < RowCount; i++) {
                    for (int j = 0; j < ColumnCount; j++) {
                        if (_playerCharacters[i][j] != null) Destroy(_playerCharacters[i][j].gameObject);
                        if (_enemyCharacters[i][j] != null) Destroy(_enemyCharacters[i][j].gameObject);
                    }
                }

                // Instantiate new characters
                for (int i = 0; i < RowCount; i++) {
                    for (int j = 0; j < ColumnCount; j++) {
                        CharacterDataDTO characterDTO = combatInfoDTO.PlayerCharacters[i][j];
                        if (characterDTO != null) {
                            Vector3 position = _playerPositions[i * ColumnCount + j].position;
                            position.y += CharactersHeight;
                            _playerCharacters[i][j] = Instantiate(_characterPrefab,
                                                                  position,
                                                                  Quaternion.LookRotation(_characterRotation));
                            _playerCharacters[i][j].SetData(characterDTO);
                            _gameSystem.PlaceCharacter();
                        }
                        CharacterDataDTO enemyDTO = combatInfoDTO.Enemies[i][j];
                        if (enemyDTO != null) {
                            Vector3 position = _enemyPositions[i * ColumnCount + j].position;
                            position.y += CharactersHeight;
                            _enemyCharacters[i][j] = Instantiate(_enemyPrefab,
                                                                 position,
                                                                 Quaternion.LookRotation(_enemyRotation));
                            _enemyCharacters[i][j].SetData(enemyDTO);
                            Debug.Log($"Enemy: {_enemyCharacters[i][j].name} - {_enemyCharacters[i][j].transform.position} " +
                                      $"- {_enemyCharacters[i][j].gameObject.activeSelf}");
                        }
                    }
                }
                _gameSystem.StartCombat();
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }

        public void UpdateCombat(CombatProcessDTO processCombatTurn) {
            Debug.Log($"Combat Process: {processCombatTurn.CombatFinished} - {processCombatTurn.Lose} - {processCombatTurn.IsBossBattle}");
            if (processCombatTurn.CombatFinished && !processCombatTurn.Lose && !processCombatTurn.IsBossBattle) {
                // Finish Normal Combat
                _camera.EndCombat();
                _gameSystem.EndCombat();
                _gameSystem.RemoveCharacter();
                Reset();
            } else if (processCombatTurn.Lose) {
                // Lose
                _uiManager.Lose();
                Reset();
            } else if (processCombatTurn.CombatFinished && processCombatTurn.IsBossBattle) {
                // Win
                _uiManager.Win();
                Reset();
            } else {
                _combatStarted = true;
                UpdateCombatStatus(processCombatTurn.Turns);
            }
        }

        private void UpdateCombatStatus(IReadOnlyList<TurnInfoDTO> turns) {
            foreach (TurnInfoDTO turnInfoDTO in turns) {
                switch (turnInfoDTO.Action) {
                    case CombatAction.Skip: break;
                    case CombatAction.Attack:
                        CharacterController[][] targetCharacters = turnInfoDTO.IsEnemyTurn ? _playerCharacters : _enemyCharacters;
                        CharacterController target = targetCharacters[turnInfoDTO.TargetRow][turnInfoDTO.TargetColumn];

                        foreach (CharacterStatusChangeDTO status in turnInfoDTO.TargetStatus) {
                            if (status.StatChanged == CharacterStat.Health) {
                                Debug.Log($"Target: {target.name} - Health: {status.NewValue}");
                                target.UpdateHealth(status.NewValue);
                                if (status.NewValue <= 0) {
                                    targetCharacters[turnInfoDTO.TargetRow][turnInfoDTO.TargetColumn] = null;
                                    Destroy(target.gameObject);
                                }
                            }
                        }
                        break;
                    case CombatAction.Heal: {
                        CharacterController[][] healCharacters = turnInfoDTO.IsEnemyTurn ? _enemyCharacters : _playerCharacters;
                        CharacterController healTarget = healCharacters[turnInfoDTO.TargetRow][turnInfoDTO.TargetColumn];
                        foreach (CharacterStatusChangeDTO status in turnInfoDTO.TargetStatus) {
                            if (status.StatChanged == CharacterStat.Health) {
                                healTarget.UpdateHealth(status.NewValue);
                            }
                        }
                        break;
                    }
                    case CombatAction.Hypnotize: {
                        CharacterController hypnoTarget = _enemyCharacters[turnInfoDTO.TargetRow][turnInfoDTO.TargetColumn];
                        foreach (CharacterStatusChangeDTO status in turnInfoDTO.TargetStatus) {
                            if (status.StatChanged == CharacterStat.Hypnotized) {
                                hypnoTarget.Hypnotize(status.NewValue > 0);
                            }
                        }
                        break;
                    }
                    default: throw new ArgumentOutOfRangeException();
                }
            }
        }

        public void Reset() {
            for (int i = 0; i < RowCount; i++) {
                for (int j = 0; j < ColumnCount; j++) {
                    if (_playerCharacters[i][j] != null) {
                        Destroy(_playerCharacters[i][j].gameObject);
                        _playerCharacters[i][j] = null;
                    }
                    if (_enemyCharacters[i][j] != null) {
                        Destroy(_enemyCharacters[i][j].gameObject);
                        _enemyCharacters[i][j] = null;
                    }
                }
            }
            _placedCharactersCount = 0;
            _combatStarted = false;
        }
    }
}