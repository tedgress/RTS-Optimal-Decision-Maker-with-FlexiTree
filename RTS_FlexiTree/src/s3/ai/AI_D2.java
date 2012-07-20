package s3.ai;


import gatech.mmpm.GameState;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import s3.base.S3;
import s3.base.S3Action;
import s3.entities.WPlayer;
import s3.mmpm.Game2D2Converter;


/**
 * IA class that uses D2 IA learnt before to decide its movements.
 *  
 * @author Marco Antonio Gmez Martn (adapted to S2 by Santi Ontan)
 */
public class AI_D2 implements AI {
	
	class S2GameStateProvider implements gatech.mmpm.learningengine.IMEExecutor.IGameStateProvider {
		public S3 game;
		public gatech.mmpm.IDomain idomainLocal;
		public GameState getGameState() {
//			return null;
			return Game2D2Converter.toGameState(game, idomain);
		}
		public int getGameCycle() {
			return game.getCycle();
		}
	}

	
	// Attributes
	String m_playerName;
	
	gatech.mmpm.learningengine.IMEExecutor m_d2AI;
	
	S2GameStateProvider stateProvider = null;
	
	List<gatech.mmpm.Action> d2Actions = new LinkedList<gatech.mmpm.Action>();
	
	gatech.mmpm.IDomain idomain;


	public AI_D2(gatech.mmpm.learningengine.IMEExecutor d2AI, String name, gatech.mmpm.IDomain idomain) {
		this.idomain = idomain;
		m_playerName = name;
		m_d2AI = d2AI;
	}
	
	public void game_cycle(S3 game, WPlayer player, List<S3Action> actions) throws ClassNotFoundException, IOException {
		if (stateProvider == null) {
			stateProvider = new S2GameStateProvider();
			stateProvider.game = game;
			stateProvider.idomainLocal = idomain;
		}
		d2Actions.clear();
		m_d2AI.getActions(stateProvider, d2Actions);
				
		// Convert to game actions and return.
		for (gatech.mmpm.Action a : d2Actions) {
			S3Action ac = Game2D2Converter.toGameAction(a,game); 
			if (ac!=null) {
				actions.add(ac);
			}
		}

	}
	
	public String getPlayerId() {
		return m_playerName;
	}

	public void gameStarts() {
		m_d2AI.gameStart(m_playerName);
	}

	public void gameEnd() {
		m_d2AI.gameEnd();
	}	
}
