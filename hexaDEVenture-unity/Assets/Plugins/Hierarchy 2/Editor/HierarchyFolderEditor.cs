using UnityEditor;
using UnityEngine;
using UnityEngine.UIElements;

namespace Hierarchy2 {
    [CustomEditor(typeof(HierarchyFolder))]
    internal class HierarchyFolderEditor : Editor {
        private void OnEnable() { }

        public override VisualElement CreateInspectorGUI() {
            HierarchyFolder script = target as HierarchyFolder;

            VisualElement root = new();

            IMGUIContainer imguiContainer = new(() => {
                script.flattenMode =
                    (HierarchyFolder.FlattenMode)EditorGUILayout.EnumPopup("Flatten Mode", script.flattenMode);
                if (script.flattenMode != HierarchyFolder.FlattenMode.None) {
                    script.flattenSpace =
                        (HierarchyFolder.FlattenSpace)EditorGUILayout.EnumPopup("Flatten Space", script.flattenSpace);
                    script.destroyAfterFlatten =
                        EditorGUILayout.Toggle("Destroy After Flatten", script.destroyAfterFlatten);
                }
            });
            root.Add(imguiContainer);

            return root;
        }

        [MenuItem("Tools/Hierarchy 2/Hierarchy Folder", priority = 0),
         MenuItem("GameObject/Hierarchy Folder", priority = 0)]
        private static void CreateInstance(MenuCommand command) {
            GameObject gameObject = new("Folder", typeof(HierarchyFolder));

            Undo.RegisterCreatedObjectUndo(gameObject, "Create Hierarchy Folder");
            if (command.context)
                Undo.SetTransformParent(gameObject.transform, ((GameObject)command.context).transform,
                                        "Create Hierarchy Folder");

            Selection.activeTransform = gameObject.transform;
        }
    }
}