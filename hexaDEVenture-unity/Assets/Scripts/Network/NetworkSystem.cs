using System;
using System.Net;
using System.Text;
using Network.In.Game.Combat;
using Network.In.Game.Inventory;
using Network.In.Game.Map;
using Network.In.Game.movement;
using Network.Out.Game;
using Network.Out.User;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using UnityEngine;
using UnityEngine.Networking;

namespace Network {
    public class NetworkSystem : MonoBehaviour {

        private static NetworkSystem _instance;
        public static NetworkSystem Instance {
            get {
                if (_instance == null) {
                    _instance = FindFirstObjectByType<NetworkSystem>();
                    if (_instance == null) {
                        GameObject obj = new(nameof(NetworkSystem));
                        _instance = obj.AddComponent<NetworkSystem>();
                    }
                }
                return _instance;
            }
        }

        private const string ContentType = "application/json";

        /// <summary>
        /// camelCase without dictionary key processing
        /// </summary>
        /// <seealso href="https://www.newtonsoft.com/json/help/html/NamingStrategySkipDictionaryKeys.htm"/>
        private static readonly DefaultContractResolver _contractResolver = new() {
            NamingStrategy = new CamelCaseNamingStrategy {
                ProcessDictionaryKeys = false
            }
        };
        private static readonly JsonSerializerSettings _serializerSettings = new() {
            ContractResolver = _contractResolver,
            Error = (_, args) => {
                Debug.LogError($"Error de serialización: {args.ErrorContext.Error.Message}");
                args.ErrorContext.Handled = true;
            }
        };

        [SerializeField]
        private string _uri = "localhost:8080";

        private string _email;
        private string _username;
        private string _password;

        public void SetEmail(string email) {
            _email = email;
        }

        public void SetUsername(string username) {
            _username = username;
        }

        public void SetPassword(string password) {
            _password = password;
        }

        public async Awaitable Register(string email, string username, string password) {
            UserDTO userDTO = new(email, username, password);
            using (UnityWebRequest request = Post("/register", userDTO, false)) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n" +
                                   $"{request.downloadHandler.text}");
                } else {
                    Debug.Log("Registration successful.");
                }
            }
        }

        public async Awaitable Unregister() {
            using (UnityWebRequest request = Post("/unregister", null)) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n" +
                                   $"{request.downloadHandler.text}");
                } else {
                    Debug.Log("Unregistration successful.");
                }
            }
        }

        public async Awaitable<bool> StartGame(long seed, int size) {
            StartGameDTO startGameDTO = new(seed, size);
            using (UnityWebRequest request = Post("/start", startGameDTO)) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    if (request.responseCode == (int) HttpStatusCode.MethodNotAllowed) {
                        return true;
                    }
                    Debug.LogError($"Error: {request.error}\n" +
                                   $"{request.downloadHandler.text}");
                    return false;
                }

                Debug.Log("Game started successfully.");
                return true;
            }
        }

        public async Awaitable<ChunkDataDTO> GetChunks() {
            using (UnityWebRequest request = Get("/game/chunks")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n" +
                                   $"{request.downloadHandler.text}");
                    return null;
                }

                Debug.Log(request.downloadHandler.text);
                ChunkDataDTO response = JsonConvert.DeserializeObject<ChunkDataDTO>(request.downloadHandler.text,
                                                                                    _serializerSettings);
                Debug.Log("Chunks retrieved.");
                return response;
            }
        }

        public async Awaitable<MovementResponseDTO> Move(Vector2Int position) {
            Vector2DTO positionDto = new(position.x, position.y);
            using (UnityWebRequest request = Post("/game/move", positionDto)) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n" +
                                   $"{request.downloadHandler.text}");
                    return null;
                }
                MovementResponseDTO response = JsonConvert.DeserializeObject<MovementResponseDTO>(request.downloadHandler.text,
                                                                                                  _serializerSettings);
                Debug.Log("Movement response received.");
                return response;
            }
        }

        public async Awaitable FinishGame() {
            using (UnityWebRequest request = Delete("/finish")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n" +
                                   $"{request.downloadHandler.text}");
                } else {
                    Debug.Log("Game finished successfully.");
                }
            }
        }

        public async Awaitable<CombatInfoDTO> GetCombatStatus() {
            using (UnityWebRequest request = Get("/game/combat")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    if (request.responseCode == (int) HttpStatusCode.MethodNotAllowed) {
                        Debug.Log($"Combat Status: {request.error}\n{request.downloadHandler.text}");
                        return null;
                    }
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                } else {
                    CombatInfoDTO response = JsonConvert.DeserializeObject<CombatInfoDTO>(request.downloadHandler.text,
                                                                                          _serializerSettings);
                    Debug.Log($"Combat status retrieved.");
                    return response;
                }
            }
            return null;
        }

        public async Awaitable<bool> PlaceCharacter(int row, int column, string characterId) {
            PlaceCharacterDTO placeCharacterDTO = new(row, column, characterId);
            using (UnityWebRequest request = Post("/game/combat/character", placeCharacterDTO)) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                    return false;
                }

                Debug.Log("Character placed successfully.");
                return true;
            }
        }

        public async Awaitable<bool> RemoveCharacter(int row, int column) {
            using (UnityWebRequest request = Delete($"/game/combat/character?row={row}&column={column}")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                    return false;
                }
                Debug.Log("Character removed successfully.");
                return true;
            }
        }

        public async Awaitable<CombatProcessDTO> ProcessCombatTurn() {
            using (UnityWebRequest request = Post("/game/combat/process", null)) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                } else {
                    CombatProcessDTO response = JsonConvert.DeserializeObject<CombatProcessDTO>(request.downloadHandler.text,
                                                                                                _serializerSettings);
                    Debug.Log($"Combat turn processed.");
                    return response;
                }
            }
            return null;
        }

        public async Awaitable<int> GetRecipesCount() {
            using (UnityWebRequest request = Get("/game/craft/recipes/count")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                    return -1;
                }

                RecipeCountDTO response = JsonConvert.DeserializeObject<RecipeCountDTO>(request.downloadHandler.text,
                                                                                        _serializerSettings);
                Debug.Log("Recipes count retrieved successfully.");
                return response.Count;
            }
        }

        public async Awaitable<RecipesDTO> GetRecipes(int page, int size) {
            using (UnityWebRequest request = Get($"/game/craft/recipes?page={page}&size={size}")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                    return null;
                }

                RecipesDTO response = JsonConvert.DeserializeObject<RecipesDTO>(request.downloadHandler.text,
                                                                                _serializerSettings);
                Debug.Log("Recipes retrieved successfully.");
                return response;
            }
        }

        public async Awaitable Craft(int recipeIndex, int count) {
            using (UnityWebRequest request = Post($"/game/craft?recipeIndex={recipeIndex}&count={count}", null)) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                } else {
                    Debug.Log("Crafting successful.");
                }
            }
        }

        public async Awaitable<InventoryDTO> GetInventory() {
            using (UnityWebRequest request = Get("/game/inventory")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                    return null;
                }

                InventoryDTO response = JsonConvert.DeserializeObject<InventoryDTO>(request.downloadHandler.text,
                                                                                    _serializerSettings);
                Debug.Log("Inventory retrieved successfully.");
                return response;
            }
        }

        public async Awaitable<CharacterDataDTO> GetCharacter(string characterId) {
            using (UnityWebRequest request = Get($"/game/inventory/character?characterId={characterId}")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                    return null;
                }

                CharacterDataDTO response =
                    JsonConvert.DeserializeObject<CharacterDataDTO>(request.downloadHandler.text, _serializerSettings);
                Debug.Log("Character data retrieved successfully.");
                return response;
            }
        }

        public async Awaitable<WeaponDataDTO> GetWeapon(string weaponId) {
            using (UnityWebRequest request = Get($"/game/inventory/weapon?weaponId={weaponId}")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                    return null;
                }

                WeaponDataDTO response =
                    JsonConvert.DeserializeObject<WeaponDataDTO>(request.downloadHandler.text, _serializerSettings);
                Debug.Log("Weapon data retrieved successfully.");
                return response;
            }
        }

        public async Awaitable<PotionDataDTO> GetPotion(string potionId) {
            using (UnityWebRequest request = Get($"/game/inventory/potion?potionId={potionId}")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                    return null;
                }

                PotionDataDTO response =
                    JsonConvert.DeserializeObject<PotionDataDTO>(request.downloadHandler.text, _serializerSettings);
                Debug.Log("Potion data retrieved successfully.");
                return response;
            }
        }

        public async Awaitable<FoodDataDTO> GetFood(string foodId) {
            using (UnityWebRequest request = Get($"/game/inventory/food?foodId={foodId}")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                    return null;
                }

                FoodDataDTO response =
                    JsonConvert.DeserializeObject<FoodDataDTO>(request.downloadHandler.text, _serializerSettings);
                Debug.Log("Food data retrieved successfully.");
                return response;
            }
        }

        public async Awaitable<MaterialDataDTO> GetMaterial(string materialId) {
            using (UnityWebRequest request = Get($"/game/inventory/material?materialId={materialId}")) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                    return null;
                }

                MaterialDataDTO response =
                    JsonConvert.DeserializeObject<MaterialDataDTO>(request.downloadHandler.text, _serializerSettings);
                Debug.Log("Material data retrieved successfully.");
                return response;
            }
        }

        public async Awaitable EquipWeapon(string characterId, string weaponId) {
            EquipWeaponDTO equipWeaponDTO = new(characterId, weaponId);
            using (UnityWebRequest request = Post("/game/inventory/equip", equipWeaponDTO)) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                } else {
                    Debug.Log("Weapon equipped successfully.");
                }
            }
        }

        public async Awaitable UnequipWeapon(string characterId) {
            UnequipWeaponDTO unequipWeaponDTO = new(characterId);
            using (UnityWebRequest request = Post("/game/inventory/unequip", unequipWeaponDTO)) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                } else {
                    Debug.Log("Weapon unequipped successfully.");
                }
            }
        }

        public async Awaitable UseItem(string characterId, string itemId) {
            UseItemDTO useItemDTO = new(characterId, itemId);
            using (UnityWebRequest request = Post("/game/inventory/use", useItemDTO)) {
                await request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success) {
                    Debug.LogError($"Error: {request.error}\n{request.downloadHandler.text}");
                } else {
                    Debug.Log("Item used successfully.");
                }
            }
        }

        public UnityWebRequest Get(string uri, bool auth = true) {
            UnityWebRequest unityWebRequest = new(_uri + uri, UnityWebRequest.kHttpVerbGET);
            return AuthAndBody(null, auth, unityWebRequest);
        }

        private UnityWebRequest Post(string uri, object body, bool auth = true) {
            UnityWebRequest unityWebRequest = new(_uri + uri, UnityWebRequest.kHttpVerbPOST);
            return AuthAndBody(body, auth, unityWebRequest);
        }

        private UnityWebRequest Delete(string uri, bool auth = true) {
            UnityWebRequest unityWebRequest = new(_uri + uri, UnityWebRequest.kHttpVerbDELETE);
            return AuthAndBody(null, auth, unityWebRequest);
        }

        private UnityWebRequest AuthAndBody(object body, bool auth, UnityWebRequest unityWebRequest) {
            if (auth) {
                string authString = $"{_email}:{_password}";
                string base64Auth = Convert.ToBase64String(Encoding.UTF8.GetBytes(authString));
                unityWebRequest.SetRequestHeader("Authorization", "Basic " + base64Auth);
            }
            if (body != null) {
                string json = JsonConvert.SerializeObject(body, _serializerSettings);
                unityWebRequest.SetRequestHeader("Content-Type", ContentType);
                unityWebRequest.uploadHandler = new UploadHandlerRaw(Encoding.UTF8.GetBytes(json));
            }
            unityWebRequest.downloadHandler = new DownloadHandlerBuffer();
            return unityWebRequest;
        }
    }
}