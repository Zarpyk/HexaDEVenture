using System;
using UnityEngine;

namespace World {
    public class GameSystem : MonoBehaviour {
        private static GameSystem _instance;
        public static GameSystem Instance {
            get {
                if (_instance == null) {
                    _instance = FindFirstObjectByType<GameSystem>();
                    if (_instance == null) {
                        GameObject obj = new(nameof(GameSystem));
                        _instance = obj.AddComponent<GameSystem>();
                    }
                }
                return _instance;
            }
        }

        private GameState _gameState;
        public GameState GameState {
            get => _gameState;
            set {
                GameState oldState = _gameState;
                _gameState = value;
                OnGameStateChanged?.Invoke(oldState, value);
            }
        }
        public Action<GameState, GameState> OnGameStateChanged;

        public void PauseGame() {
            if (IsPaused()) return;
            GameState |= GameState.Pause;
        }

        public void UnpauseGame() {
            if (!IsPaused()) return;
            GameState &= ~GameState.Pause;
        }

        public bool IsPaused() {
            return (GameState & GameState.Pause) == GameState.Pause;
        }

        public void StartLoading() {
            if (IsLoading()) return;
            GameState |= GameState.Loading;
        }

        public void EndLoading() {
            if (!IsLoading()) return;
            GameState &= ~GameState.Loading;
        }

        public bool IsLoading() {
            return (GameState & GameState.Loading) == GameState.Loading;
        }

        public void StartGame() {
            if (IsInGame()) return;
            GameState |= GameState.InGame;
        }

        public void EndGame() {
            if (!IsInGame()) return;
            GameState &= ~GameState.InGame;
        }

        public bool IsInGame() {
            return (GameState & GameState.InGame) == GameState.InGame;
        }

        public void StartCombat() {
            if (IsInCombat()) return;
            GameState |= GameState.InCombat;
        }

        public void EndCombat() {
            if (!IsInCombat()) return;
            GameState &= ~GameState.InCombat;
        }

        public bool IsInCombat() {
            return (GameState & GameState.InCombat) == GameState.InCombat;
        }

        public void OpenInventory() {
            if (IsInInventory()) return;
            GameState |= GameState.InInventory;
        }

        public void CloseInventory() {
            if (!IsInInventory()) return;
            GameState &= ~GameState.InInventory;
        }

        public bool IsInInventory() {
            return (GameState & GameState.InInventory) == GameState.InInventory;
        }

        public void PlaceCharacter() {
            if (IsCharacterPlaced()) return;
            GameState |= GameState.CharacterPlaced;
        }

        public void RemoveCharacter() {
            if (!IsCharacterPlaced()) return;
            GameState &= ~GameState.CharacterPlaced;
        }

        public bool IsCharacterPlaced() {
            return (GameState & GameState.CharacterPlaced) == GameState.CharacterPlaced;
        }
    }
}