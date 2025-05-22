using UnityEngine;

namespace Combat {
    public class CombatTerrain : MonoBehaviour {
        public int Row { get; private set; }
        public int Column { get; private set; }

        public void SetUpTerrain(int row, int column) {
            Row = row;
            Column = column;
        }
    }
}