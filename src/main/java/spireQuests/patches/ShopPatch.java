package spireQuests.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import spireQuests.ui.QuestBoardProp;

public class ShopPatch {
	private static QuestBoardProp questBoardProp;

	@SpirePatch(
			clz = ShopRoom.class,
			method = "render",
			paramtypez = SpriteBatch.class
	)
	public static class PostRender {
		@SpirePostfixPatch()
		public static void Render(ShopRoom original, SpriteBatch sb) {
			if (questBoardProp != null) {
				questBoardProp.render(sb);
			}
		}
	}

	@SpirePatch(
			clz = ShopRoom.class,
			method = "update"
	)
	public static class PostUpdate {
		@SpirePostfixPatch()
		public static void Update() {
			if (questBoardProp != null) {
				questBoardProp.update();
			}
		}
	}

	@SpirePatch(
			clz = ShopRoom.class,
			method = "onPlayerEntry"
	)
	public static class PostPlayerEntry {
		@SpirePostfixPatch()
		public static void PlayerEntry() {
			questBoardProp = new QuestBoardProp();
		}
	}
}