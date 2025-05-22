using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Combat;
using Network;
using Network.In.Game.DTOEnum;
using Network.In.Game.Map;
using Network.In.Game.movement;
using UnityEngine;
using UnityEngine.Pool;

namespace World {
    public class WorldManager : MonoBehaviour {
        private const int CellPerFrame = 16 * 4;

        [SerializeField]
        private CameraController _cameraController;
        [SerializeField]
        private MovementManager _movementManager;
        [SerializeField]
        private BattlefieldManager _battlefieldManager;
        private NetworkSystem _networkSystem;

        [Header("Start Game")]
        [SerializeField]
        private long _seed = 1234;
        [SerializeField]
        private int _size = 144;
        public float CenterOffset => _size / 2f;

        [Header("Prefabs")]
        [SerializeField]
        private GameObject _mainCharacterPrefab;
        public float MainCharacterHeight = 0.1f;
        [SerializeField]
        private GameObject _groundPrefab;
        [SerializeField]
        private GameObject _pathPrefab;
        [SerializeField]
        private GameObject _wallPrefab;
        [SerializeField]
        private GameObject _resourcePrefab;
        public float ResourceHeight = 0.1f;
        [SerializeField]
        private GameObject _enemyPrefab;
        public float EnemyHeight = 0.1f;

        private readonly HashSet<ChunkDTO> _loadedChunks = new();
        private readonly Dictionary<Vector2DTO, GameObject> _activeCells = new();
        private readonly Dictionary<Vector2DTO, GameObject> _activeResources = new();
        private readonly Dictionary<Vector2DTO, GameObject> _activeEnemies = new();

        private ObjectPool<GameObject> _groundPool;
        private ObjectPool<GameObject> _pathPool;
        private ObjectPool<GameObject> _wallPool;
        private ObjectPool<GameObject> _resourcePool;
        private ObjectPool<GameObject> _enemyPool;

        private bool _playerSpawned;

        private void Awake() {
            InitializePools();
        }

        private void Start() {
            _networkSystem = NetworkSystem.Instance;
        }

        private void InitializePools() {
            _groundPool = CreatePool(_groundPrefab, 16 * 16 * 3);
            _pathPool = CreatePool(_pathPrefab, 16 * 16 * 3);
            _wallPool = CreatePool(_wallPrefab, 16 * 16 * 3);
            _resourcePool = CreatePool(_resourcePrefab, 100);
            _enemyPool = CreatePool(_enemyPrefab, 100);
        }

        public async Awaitable<bool> StartGame() {
            bool startGame = await _networkSystem.StartGame(_seed, _size);
            if (!startGame) {
                Debug.LogError("Failed to start game");
                return false;
            }

            ChunkDataDTO chunkDataDTO = await _networkSystem.GetChunks();
            SpawnMainCharacter(chunkDataDTO.mainCharacterPosition);
            await ProcessNewChunks(chunkDataDTO.Chunks);
            return true;
        }

        public async Awaitable LoadNewChunks() {
            ChunkDataDTO chunkDataDTO = await _networkSystem.GetChunks();
            await ProcessNewChunks(chunkDataDTO.Chunks);
        }

        public void FinishGame() {
            _cameraController.Reset();
            _movementManager.Reset();
            _playerSpawned = false;

            foreach (ChunkDTO loadedChunk in _loadedChunks) {
                foreach (CellDataDTO[] cells in loadedChunk.Cells) {
                    foreach (CellDataDTO cell in cells) {
                        if (_activeCells.TryGetValue(cell.Position, out GameObject cellObj)) {
                            ReleaseCell(cell.Type, cellObj);
                            _activeCells.Remove(cell.Position);
                        }
                    }
                }
            }
            foreach (GameObject resourceObj in _activeResources.Values) {
                _resourcePool.Release(resourceObj);
            }
            foreach (GameObject enemyObj in _activeEnemies.Values) {
                _enemyPool.Release(enemyObj);
            }

            _loadedChunks.Clear();
            _activeCells.Clear();
            _activeResources.Clear();
            _activeEnemies.Clear();
        }

        public void RemoveResource(Vector2DTO resourcePosition) {
            if (_activeResources.TryGetValue(resourcePosition, out GameObject resource)) {
                _resourcePool.Release(resource);
                _activeResources.Remove(resourcePosition);
            }
        }

        public GameObject GetEnemy(Vector2DTO position) {
            return _activeEnemies.GetValueOrDefault(position);
        }

        public void UpdateEnemy(EnemyMovementDTO enemyMovementDTO) {
            if (_activeEnemies.Remove(enemyMovementDTO.OriginalPosition, out GameObject enemy)) {
                _activeEnemies[enemyMovementDTO.TargetPosition] = enemy;
            }
        }

        public void RemoveEnemy(Vector2DTO actionTargetPosition) {
            if (_activeEnemies.TryGetValue(actionTargetPosition, out GameObject enemy)) {
                _enemyPool.Release(enemy);
                _activeEnemies.Remove(actionTargetPosition);
            }
        }

        private void SpawnMainCharacter(Vector2DTO position) {
            if (_playerSpawned) return;
            _playerSpawned = true;
            GameObject mainCharacter = Instantiate(_mainCharacterPrefab,
                                                   new Vector3(position.X - CenterOffset, MainCharacterHeight,
                                                               position.Y - CenterOffset),
                                                   Quaternion.identity);
            _cameraController.SetPlayerTransform(mainCharacter.transform);
            _movementManager.SetMainCharacter(mainCharacter);
        }

        private async Awaitable ProcessNewChunks(IReadOnlyList<ChunkDTO> newChunks) {
            HashSet<ChunkDTO> chunksToRemove = new(_loadedChunks);

            foreach (ChunkDTO chunk in newChunks) {
                // Don't load if loaded previously
                if (chunksToRemove.RemoveWhere(x => x.ChunkPosition == chunk.ChunkPosition) > 0) {
                    continue;
                }
                _loadedChunks.Add(chunk);

                await ProcessCells(chunk);
                Debug.Log($"Chunks loaded");
                ProcessResources(chunk);
                ProcessEnemies(chunk);
            }

            UnloadChunks(chunksToRemove);
        }

        private async Awaitable ProcessCells(ChunkDTO chunk) {
            int cellCount = 0;
            foreach (CellDataDTO[] cells in chunk.Cells) {
                foreach (CellDataDTO cell in cells) {
                    CreateCell(cell);
                    cellCount++;

                    // Generate only a certain number of cells per frame
                    if (cellCount % CellPerFrame == 0) {
                        await Awaitable.NextFrameAsync();
                    }
                }
            }
        }

        private void ProcessResources(ChunkDTO chunk) {
            foreach (ResourceDTO resource in chunk.Resources) {
                if (_activeResources.ContainsKey(resource.Position)) continue;

                Vector3 worldPos = new(resource.Position.X - CenterOffset, ResourceHeight,
                                       resource.Position.Y - CenterOffset);

                GameObject resourceObj = _resourcePool.Get();
                resourceObj.transform.position = worldPos;
                _activeResources[resource.Position] = resourceObj;
            }
        }

        private void ProcessEnemies(ChunkDTO chunk) {
            foreach (EnemyDTO enemy in chunk.Enemies) {
                if (_activeEnemies.ContainsKey(enemy.Position)) continue;

                Vector3 worldPos = new(enemy.Position.X - CenterOffset, EnemyHeight,
                                       enemy.Position.Y - CenterOffset);

                GameObject enemyObj = _enemyPool.Get();
                enemyObj.transform.position = worldPos;
                _activeEnemies[enemy.Position] = enemyObj;
            }
        }

        private void CreateCell(CellDataDTO cell) {
            if (_activeCells.ContainsKey(cell.Position)) return;

            // Spawn cell on the center
            Vector3 worldPos = new(cell.Position.X - CenterOffset, 0,
                                   cell.Position.Y - CenterOffset);

            GameObject cellObj = GetCell(cell.Type);
            cellObj.transform.position = worldPos;

            // Save the cell to release it later
            _activeCells[cell.Position] = cellObj;
        }

        private GameObject GetCell(CellType type) {
            return type switch {
                CellType.Ground or CellType.Ground2 => _groundPool.Get(),
                CellType.Path => _pathPool.Get(),
                CellType.Wall => _wallPool.Get(),
                _ => throw new ArgumentOutOfRangeException(nameof(type), type, null)
            };
        }

        private void UnloadChunks(HashSet<ChunkDTO> chunksToRemove) {
            foreach (ChunkDTO chunk in chunksToRemove) {
                foreach (CellDataDTO[] cells in chunk.Cells) {
                    foreach (CellDataDTO cell in cells) {
                        if (_activeCells.TryGetValue(cell.Position, out GameObject cellObj)) {
                            ReleaseCell(cell.Type, cellObj);
                            _activeCells.Remove(cell.Position);
                        }
                        if (_activeResources.TryGetValue(cell.Position, out GameObject resourceObj)) {
                            _resourcePool.Release(resourceObj);
                            _activeResources.Remove(cell.Position);
                        }
                        if (_activeEnemies.TryGetValue(cell.Position, out GameObject enemyObj)) {
                            _enemyPool.Release(enemyObj);
                            _activeEnemies.Remove(cell.Position);
                        }
                    }
                }
                _loadedChunks.Remove(chunk);
            }
        }

        private void ReleaseCell(CellType type, GameObject cell) {
            switch (type) {
                case CellType.Ground:
                case CellType.Ground2: _groundPool.Release(cell); break;
                case CellType.Path: _pathPool.Release(cell); break;
                case CellType.Wall: _wallPool.Release(cell); break;
                default: throw new ArgumentOutOfRangeException(nameof(type), type, null);
            }
        }

        private static ObjectPool<GameObject> CreatePool(GameObject prefab, int size) {
            return new ObjectPool<GameObject>(() => Instantiate(prefab),
                                              obj => obj.SetActive(true),
                                              obj => obj.SetActive(false),
                                              Destroy,
                                              true,
                                              size);
        }

    }
}