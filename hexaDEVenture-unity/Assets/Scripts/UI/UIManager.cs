using System;
using Combat;
using Network;
using Network.In.Game.Combat;
using TMPro;
using UnityEngine;
using UnityEngine.InputSystem;
using UnityEngine.UI;
using World;

namespace UI {
    public class UIManager : MonoBehaviour {
        private static readonly int _loginMenu = Animator.StringToHash("LoginMenu");
        private static readonly int _finishLoading = Animator.StringToHash("FinishLoading");

        [SerializeField]
        private WorldManager _worldManager;
        [SerializeField]
        private BattlefieldManager _battlefieldManager;
        private GameSystem _gameSystem;
        private NetworkSystem _networkSystem;
        private CameraController _cameraController;
        private InputSystemActions _input;

        private Animator _animator;

        [Header("Buttons")]
        [SerializeField]
        private Button _registerButton;
        [SerializeField]
        private Button _unregisterButton;
        [SerializeField]
        private Button _startGameButton;
        [SerializeField]
        private Button _finishGameButton;
        [SerializeField]
        private Button _startCombatButton;

        [Header("Text Fields")]
        [SerializeField]
        private TMP_InputField _emailText;
        [SerializeField]
        private TMP_InputField _usernameText;
        [SerializeField]
        private TMP_InputField _passwordText;

        [Header("Status")]
        [SerializeField]
        private TMP_Text _statusText;
        [SerializeField]
        private Color _registeringColor = Color.yellow;
        [SerializeField]
        private Color _successColor = Color.green;
        [SerializeField]
        private Color _errorColor = Color.red;

        private void Awake() {
            _animator = GetComponent<Animator>();
            _cameraController = Camera.main!.GetComponent<CameraController>();

            _emailText.onValueChanged.AddListener(OnEmailTextChanged);
            _usernameText.onValueChanged.AddListener(OnUsernameTextChanged);
            _passwordText.onValueChanged.AddListener(OnPasswordTextChanged);

            _registerButton.onClick.AddListener(Register);
            _unregisterButton.onClick.AddListener(Unregister);
            _startGameButton.onClick.AddListener(StartGame);
            _finishGameButton.onClick.AddListener(FinishGame);
            _startCombatButton.onClick.AddListener(StartCombat);
            _statusText.enabled = false;
        }

        private void Start() {
            _networkSystem = NetworkSystem.Instance;

            _networkSystem.SetEmail(_emailText.text);
            _networkSystem.SetUsername(_usernameText.text);
            _networkSystem.SetPassword(_passwordText.text);

            _gameSystem.PauseGame();
            _animator.SetBool(_loginMenu, true);
        }

        private void OnEnable() {
            _input ??= new InputSystemActions();
            _input.UI.Pause.performed += OnPause;
            _input.UI.Pause.Enable();

            _gameSystem = GameSystem.Instance;
            _gameSystem.OnGameStateChanged += OnGameStateChanged;
        }

        private void OnDisable() {
            _input.UI.Pause.performed -= OnPause;
            _input.UI.Pause.Disable();

            _gameSystem.OnGameStateChanged -= OnGameStateChanged;
        }

        private void OnGameStateChanged(GameState oldState, GameState gameState) {
            _startCombatButton.gameObject.SetActive(gameState.HasFlag(GameState.InCombat) &&
                                                    gameState.HasFlag(GameState.CharacterPlaced));
        }

        private void OnDestroy() {
            _emailText.onValueChanged.RemoveListener(OnEmailTextChanged);
            _usernameText.onValueChanged.RemoveListener(OnUsernameTextChanged);
            _passwordText.onValueChanged.RemoveListener(OnPasswordTextChanged);

            _registerButton.onClick.RemoveListener(Register);
            _unregisterButton.onClick.RemoveListener(Unregister);
            _startGameButton.onClick.RemoveListener(StartGame);
            _finishGameButton.onClick.RemoveListener(FinishGame);
        }

        public void Win() {
            _gameSystem.StartLoading();
            _animator.SetBool(_finishLoading, false);
            _animator.SetBool(_loginMenu, false);
            _worldManager.FinishGame();
            _animator.SetBool(_loginMenu, true);
            _animator.SetBool(_finishLoading, true);
            _gameSystem.EndCombat();
            _gameSystem.RemoveCharacter();
            _gameSystem.EndGame();
            _gameSystem.PauseGame();
            _gameSystem.EndLoading();
        }

        public void Lose() {
            _gameSystem.StartLoading();
            _animator.SetBool(_finishLoading, false);
            _animator.SetBool(_loginMenu, false);
            _worldManager.FinishGame();
            _animator.SetBool(_loginMenu, true);
            _animator.SetBool(_finishLoading, true);
            _gameSystem.EndCombat();
            _gameSystem.RemoveCharacter();
            _gameSystem.EndGame();
            _gameSystem.PauseGame();
            _gameSystem.EndLoading();
        }

        private void OnEmailTextChanged(string value) {
            _networkSystem.SetEmail(value);
        }

        private void OnUsernameTextChanged(string value) {
            _networkSystem.SetUsername(value);
        }

        private void OnPasswordTextChanged(string value) {
            _networkSystem.SetPassword(value);
        }

        private void OnPause(InputAction.CallbackContext obj) {
            if (_gameSystem.IsLoading()) return;
            if (_gameSystem.IsInGame() && _gameSystem.IsPaused()) {
                _gameSystem.UnpauseGame();
                _animator.SetBool(_loginMenu, false);
            } else if (!_gameSystem.IsInGame()) {
                return;
            }
            _statusText.enabled = false;
            _animator.SetBool(_loginMenu, true);
            _gameSystem.PauseGame();
        }

        private async void Register() {
            try {
                _statusText.enabled = true;
                _statusText.text = "Registering...";
                _statusText.color = _registeringColor;
                await _networkSystem.Register(_emailText.text,
                                              _usernameText.text,
                                              _passwordText.text);
                _statusText.text = "Player registered";
                _statusText.color = _successColor;
            } catch (Exception) {
                // ignored
            }
        }

        private async void Unregister() {
            try {
                _statusText.enabled = true;
                _statusText.text = "Unregistering...";
                _statusText.color = _registeringColor;
                await _networkSystem.Unregister();
                _statusText.text = "Player unregistered";
                _statusText.color = _successColor;
            } catch (Exception) {
                // ignored
            }
        }

        private async void StartGame() {
            try {
                _gameSystem.StartLoading();
                _animator.SetBool(_finishLoading, false);
                _animator.SetBool(_loginMenu, false);
                _statusText.enabled = false;
                _statusText.color = _registeringColor;
                bool startGame = await _worldManager.StartGame();

                CombatInfoDTO combatInfoDTO = await _networkSystem.GetCombatStatus();
                if (combatInfoDTO != null) {
                    _cameraController.StartCombat();
                    await _battlefieldManager.StartCombat(combatInfoDTO);
                }

                _animator.SetBool(_finishLoading, true);
                await Awaitable.NextFrameAsync();
                _animator.SetBool(_finishLoading, false);
                if (startGame) {
                    _gameSystem.UnpauseGame();
                    _gameSystem.StartGame();
                } else {
                    _statusText.enabled = true;
                    _statusText.text = "Failed to start game";
                    _statusText.color = _errorColor;
                    _animator.SetBool(_loginMenu, true);
                }
                _gameSystem.EndLoading();
            } catch (Exception) {
                // ignored
            }
        }

        private async void FinishGame() {
            try {
                _gameSystem.StartLoading();
                _animator.SetBool(_finishLoading, false);
                _animator.SetBool(_loginMenu, false);
                _worldManager.FinishGame();
                await _networkSystem.FinishGame();
                _animator.SetBool(_loginMenu, true);
                _animator.SetBool(_finishLoading, true);
                _gameSystem.EndCombat();
                _gameSystem.RemoveCharacter();
                _gameSystem.EndGame();
                _gameSystem.PauseGame();
                _gameSystem.EndLoading();
            } catch (Exception) {
                // ignored
            }
        }

        private async void StartCombat() {
            try {
                CombatProcessDTO processCombatTurn = await _networkSystem.ProcessCombatTurn();
                _battlefieldManager.UpdateCombat(processCombatTurn);
            } catch (Exception e) {
                Debug.LogError(e);
            }
        }
    }
}