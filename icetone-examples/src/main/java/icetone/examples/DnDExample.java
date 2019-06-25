package icetone.examples;

import java.util.Arrays;

import org.lwjgl.opengl.Display;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont.Align;
import com.jme3.math.ColorRGBA;

import icetone.controls.containers.Panel;
import icetone.controls.extras.DragElement;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Measurement.Unit;
import icetone.core.Position;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.ToolKit;
import icetone.core.layout.FillLayout;
import icetone.core.layout.GridLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.css.StyleManager.CursorType;
import icetone.extras.appstates.PopupMessageAppState;
import icetone.extras.appstates.PopupMessageAppState.Channel;

/**
 * This example shows some an example of Drag and Drop
 */
public class DnDExample extends SimpleApplication {

	class Player {
		int score;
		char symbol;
		ColorRGBA color;

		public Player(int score, char symbol, ColorRGBA color) {
			super();
			this.score = score;
			this.symbol = symbol;
			this.color = color;
		}

	}

	public static void main(String[] args) {
		DnDExample app = new DnDExample();
		app.start();
	}

	private Label score1;
	private Label score2;
	private Label turn;
	private Player player1;
	private Player player2;
	private Element playerDetails;
	private int pieceNo;
	private Player lastWinner;
	private Element grid;
	private PopupMessageAppState popup;

	@Override
	public void simpleInitApp() {
		Display.setResizable(true);

		/*
		 * We are only using a single screen, so just initialise it (and you don't need
		 * to provide the screen instance to each control).
		 * 
		 * It is passed to the buildExample method in this way to help ExampleRunner so
		 * this example can be run from there and as a standalone JME application
		 */
		Screen screen = new Screen(this);
		buildExample(screen);

		getStateManager().attach(new PopupMessageAppState(screen));

	}

	protected void buildExample(ElementContainer<?, ?> container) {

		popup = getStateManager().getState(PopupMessageAppState.class);

		player1 = new Player(0, 'O', ColorRGBA.Green);
		player2 = new Player(0, 'X', ColorRGBA.Cyan);

		/* Player */
		playerDetails = new Panel(new MigLayout("wrap 2", "[:64:][:64:]", "[][]"));
		playerDetails.setResizable(false);
		playerDetails.addElement(new Label("Player 1:"));
		playerDetails.addElement(score1 = new Label("0"), "ax 100%");
		score1.setFontColor(player1.color);
		playerDetails.addElement(new Label("Player 2:"));
		playerDetails.addElement(score2 = new Label("0"), "ax 100%");
		score2.setFontColor(player2.color);
		playerDetails.addElement(turn = new Label(), "span 2, ax 50%, gaptop 10, gapbottom 10");
		playerDetails.setMovable(true);
		playerDetails.setPosition(new Position(75, 10, Unit.PERCENT, Unit.PERCENT));

		// Initial piece
		createPiece(player1);

		/* Grid */
		grid = new Element(new GridLayout(3, 3));
		grid.setIndent(9);
		grid.setTextPadding(9);
		grid.setPreferredDimensions(new Size(512, 512));
		grid.setTexture("/icetone/examples/DnDExample.png");
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				Element el = new Element(new FillLayout());
				el.setTextPadding(12);
				el.setUserData("x", x);
				el.setUserData("y", y);
				el.setDragDropDropElement(true);
				el.setTextAlign(Align.Center);
				grid.addElement(el);
			}
		}
		grid.setMovable(true);
		grid.setPosition(new Position(10, 10, Unit.PERCENT, Unit.PERCENT));
		grid.sizeToContent();

		/* Add to screen */
		container.showElement(grid);
		container.showElement(playerDetails);
	}

	protected void updateScores() {
		score1.setText(String.valueOf(player1.score));
		score2.setText(String.valueOf(player2.score));
	}

	protected DragElement createPiece(Player player) {
		DragElement piece = new DragElement();
		piece.onEnd(evt -> {
			if (evt.getTarget() != null && evt.getTarget().getElements().isEmpty()) {
				/* Indicate we accept the drop */
				evt.setConsumed();

				/* Stop the placed piece from being dragged again */
				piece.setDragDropDragElement(false);
				piece.setCursor(CursorType.POINTER);
			}
		});
		piece.onComplete(evt -> {

			pieceNo++;

			if (isLine(player, 0, 1, 2) || isLine(player, 3, 4, 5) || isLine(player, 6, 7, 8) || isLine(player, 0, 3, 6)
					|| isLine(player, 1, 4, 7) || isLine(player, 2, 5, 8) || isLine(player, 0, 4, 8)
					|| isLine(player, 2, 4, 6)) {
				/* A win. Fade the losing cells, then restart */
				ToolKit.get().getAlarm().timed(() -> {
					popup.message(Channel.SUCCESS, player == player1 ? "Player 1 Wins" : "Player 2 Wins");
					player.score++;
					updateScores();
					gameOver(player);
				}, 1f);
			} else {
				if (pieceNo == 9) {
					/* No win */
					popup.message(Channel.ERROR, "A Draw");

					/* Fade all cells */
					for (BaseElement el : grid.getElements()) {
						if (!el.getElements().isEmpty())
							el.getElements().get(0).setElementAlpha(0.1f);
					}

					/* Restart game */
					ToolKit.get().getAlarm().timed(() -> gameOver(player), 1f);
				} else {
					/* Not finished */
					if (player == player1)
						createPiece(player2);
					else
						createPiece(player1);
				}
			}
		});
		piece.setCursor(CursorType.HAND);
		piece.setTextAlign(Align.Center);
		piece.setUserData("symbol", String.valueOf(player.symbol));
		piece.setUseSpringBackEffect(true);
		piece.setUseLockToDropElementEffect(true);
		piece.setDefaultColor(player.color);
		piece.setPreferredDimensions(new Size(128, 128));
		piece.setTexture("/icetone/examples/DnDExample_" + player.symbol + ".png");

		turn.setText(player == player1 ? "Player 1's Turn" : "Player 2's Turn");

		playerDetails.addElement(piece, "span 2, ax 50%");
		playerDetails.sizeToContent();

		return piece;
	}

	protected void gameOver(Player player) {
		for (BaseElement c : grid.getElements())
			c.removeAllChildren();
		pieceNo = 0;
		createPiece(lastWinner == null ? (player == player1 ? player2 : player1) : lastWinner);
	}

	protected boolean isLine(Player player, Integer... cells) {
		boolean win = true;
		for (Integer cell : cells) {
			BaseElement el = grid.getElements().get(cell);
			if (el.getElements().isEmpty()
					|| !el.getElements().get(0).getUserData("symbol").equals(String.valueOf(player.symbol))) {
				win = false;
				break;
			}
		}
		if (win) {
			int idx = 0;
			for (BaseElement el : grid.getElements()) {
				if (!Arrays.asList(cells).contains(Integer.valueOf(idx)) && !el.getElements().isEmpty())
					el.getElements().get(0).setElementAlpha(0.25f);
				idx++;
			}
		}
		return win;
	}

}
