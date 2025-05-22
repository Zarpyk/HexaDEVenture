using System;
using System.ComponentModel;
using System.Globalization;
using System.Text.RegularExpressions;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace Network.In.Game.Map {
    [TypeConverter(typeof(Vector2DTOConverter))]
    [JsonConverter(typeof(Vector2DTOJsonConverter))]
    public record Vector2DTO(int X, int Y);

    /// <summary>
    /// Custom type converter for Vector2DTO to allow conversion from string.
    /// </summary>
    /// <seealso href="https://stackoverflow.com/a/57319194/11451105"/>
    public class Vector2DTOConverter : TypeConverter {
        public override bool CanConvertFrom(ITypeDescriptorContext context, Type sourceType) {
            return sourceType == typeof(string) || base.CanConvertFrom(context, sourceType);
        }

        public override object ConvertFrom(ITypeDescriptorContext context, CultureInfo culture, object value) {
            string key = Convert.ToString(value).Trim('(').Trim(')');
            string[] parts = Regex.Split(key, ",");
            return new Vector2DTO(int.Parse(parts[0].Trim(), CultureInfo.InvariantCulture),
                                  int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture));
        }
    }

    /// <summary>
    /// Custom JSON converter for Vector2DTO to allow serialization and deserialization.<br/>
    /// Generated with Claude 3.5
    /// </summary>
    public class Vector2DTOJsonConverter : JsonConverter<Vector2DTO> {
        public override void WriteJson(JsonWriter writer, Vector2DTO value, JsonSerializer serializer) {
            if (writer.Path.Contains("Dictionary`2.Key")) {
                writer.WriteValue($"({value.X},{value.Y})");
            } else {
                var obj = new JObject {
                    ["x"] = value.X,
                    ["y"] = value.Y
                };
                obj.WriteTo(writer);
            }
        }

        public override Vector2DTO ReadJson(JsonReader reader, Type objectType, Vector2DTO existingValue, bool hasExistingValue,
                                            JsonSerializer serializer) {
            if (reader.TokenType == JsonToken.String) {
                if (reader.Value == null) return null;
                string value = reader.Value.ToString();
                string[] parts = value.Trim('(', ')').Split(',');
                return new Vector2DTO(int.Parse(parts[0]),
                                      int.Parse(parts[1]));
            }

            JObject obj = JObject.Load(reader);
            if (obj["x"] == null || obj["y"] == null) {
                throw new JsonSerializationException("Invalid Vector2DTO format");
            }
            return new Vector2DTO(obj["x"].Value<int>(),
                                  obj["y"].Value<int>());
        }
    }
}