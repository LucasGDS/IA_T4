package INF1771_GameAI;
import INF1771_GameAI.Map.*;
import java.util.ArrayList;
import java.util.List;


public class GameAI
{
    Position player = new Position();
    String state = "ready";
    String dir = "north";
    long score = 0;
    int energy = 0;
    
    boolean blocked = false;
    boolean steps = false;
    boolean breeze = false;
    boolean flash = false;
    boolean ouro = false;
    boolean powerup = false;
    boolean enemy = false;
    boolean ultima_acao_andar = true;
    boolean ultima_acao_pegar_anel = false;
    boolean ultima_acao_atirar = false;
    Position destino;
    boolean primeira_decisao = true;
    boolean ready = false;
    
    MapInfo mapa = MapInfo.GetInstance();
    PathFinder pathfinder=  PathFinder.GetInstance();

    
    /**
     * Refresh player status
     * @param x			player position x
     * @param y			player position y
     * @param dir		player direction
     * @param state		player state
     * @param score		player score
     * @param energy	player energy
     */
    public void SetStatus(int x, int y, String dir, String state, long score, int energy)
    {
        player.x = x;
        player.y = y;
        this.dir = dir.toLowerCase();

        this.state = state;
        this.score = score;
        this.energy = energy;
    }

    /**
     * Get list of observable adjacent positions
     * @return List of observable adjacent positions 
     */
    public List<Position> GetObservableAdjacentPositions()
    {
        List<Position> ret = new ArrayList<Position>();

        ret.add(new Position(player.x - 1, player.y));
        ret.add(new Position(player.x + 1, player.y));
        ret.add(new Position(player.x, player.y - 1));
        ret.add(new Position(player.x, player.y + 1));

        return ret;
    }

    /**
     * Get list of all adjacent positions (including diagonal)
     * @return List of all adjacent positions (including diagonal)
     */
    public List<Position> GetAllAdjacentPositions()
    {
        List<Position> ret = new ArrayList<Position>();

        ret.add(new Position(player.x - 1, player.y - 1));
        ret.add(new Position(player.x, player.y - 1));
        ret.add(new Position(player.x + 1, player.y - 1));

        ret.add(new Position(player.x - 1, player.y));
        ret.add(new Position(player.x + 1, player.y));

        ret.add(new Position(player.x - 1, player.y + 1));
        ret.add(new Position(player.x, player.y + 1));
        ret.add(new Position(player.x + 1, player.y + 1));

        return ret;
    }

    /**
     * Get next forward position
     * @return next forward position
     */
    public Position CasaDaFrente()
    {
        Position ret = null;
        if(dir.equals("north"))
                ret = new Position(player.x, player.y - 1);
        else if(dir.equals("east"))
                ret = new Position(player.x + 1, player.y);
        else if(dir.equals("south"))
                ret = new Position(player.x, player.y + 1);
        else if(dir.equals("west"))
                ret = new Position(player.x - 1, player.y);

        return ret;
    }
    public Position CasaDaDireita()
    {
        Position ret = null;
        if(dir.equals("north"))
                ret = new Position(player.x + 1, player.y);
        else if(dir.equals("east"))
                ret = new Position(player.x, player.y + 1);
        else if(dir.equals("south"))
                ret = new Position(player.x - 1, player.y);
        else if(dir.equals("west"))
                ret = new Position(player.x, player.y - 1);

        return ret;
    }
    public Position CasaDaEsquerda()
    {
    	 Position ret = null;
         if(dir.equals("north"))
                 ret = new Position(player.x - 1, player.y);
         else if(dir.equals("east"))
                 ret = new Position(player.x, player.y - 1);
         else if(dir.equals("south"))
                 ret = new Position(player.x + 1, player.y);
         else if(dir.equals("west"))
                 ret = new Position(player.x, player.y + 1);

        return ret;
    }
    public Position CasaDeTras()
    {
        Position ret = null;
        if(dir.equals("north"))
                ret = new Position(player.x, player.y + 1);
        else if(dir.equals("east"))
                ret = new Position(player.x - 1, player.y);
        else if(dir.equals("south"))
                ret = new Position(player.x, player.y - 1);
        else if(dir.equals("west"))
                ret = new Position(player.x + 1, player.y);

        return ret;
    }

    /**
     * Player position
     * @return player position
     */
    public Position GetPlayerPosition()
    {
        return player;
    }
    
    /**
     * Set player position
     * @param x		x position
     * @param y		y position
     */
    public void SetPlayerPosition(int x, int y)
    {
        player.x = x;
        player.y = y;

    }

    /**
     * Observations received
     * @param o	 list of observations
     */
    public void GetObservations(List<String> o)
    {
    	boolean blockedObserved = false;
    	boolean stepsObserved = false;
    	boolean breezeObserved = false;
    	boolean flashObserved = false;
    	boolean ouroObserved = false;
    	boolean powerupObserved = false;
    	boolean enemyObserved = false;
    	
        for (String s : o)
        {
            if(s.equals("blocked")){
            	
            	blockedObserved = true;
            	
            } else if(s.equals("steps")){
            	
            	stepsObserved = true;

            } else if(s.equals("breeze")){
            	
            	breezeObserved = true;

            } else if(s.equals("flash")){
            	
            	flashObserved = true;

            } else if(s.equals("blueLight")){
            	
            	powerupObserved = true;

            } else if(s.equals("redLight")){
            	
            	ouroObserved = true;

            } else if (s.indexOf("enemy#") > -1) {
				try{
					int steps = Integer.parseInt(s.replaceFirst("enemy#", ""));
					if(steps > 0)
						enemyObserved = true;	
				}
				catch(Exception ex){
					
				}
			}
        }
        
        if(blockedObserved == true){
        	this.blocked = true;
        	this.mapa.AddWall(CasaDaFrente());
        	this.pathfinder.ResetPathFinder();
        }
        else
        	this.blocked = false;
        if(stepsObserved == true)
        	this.steps = true;
        else
        	this.steps = false;
        if(breezeObserved == true){
        	this.breeze = true;
        	this.mapa.AddBreeze(this.player);
        }
        else
        	this.breeze = false;
        if(flashObserved == true){
        	this.flash = true;
        	this.mapa.AddFlash(this.player);
        }
        else
        	this.flash = false;
        if(ouroObserved == true){
        	this.ouro = true;
        	this.mapa.AddGold(this.player);
        }
        else
        	this.ouro = false;
        if(powerupObserved == true)
        	this.powerup = true;
        else
        	this.powerup = false;
        if(enemyObserved == true)
        	this.enemy = true;
        else
        	this.enemy = false;

        if(breezeObserved == false && flashObserved == false)
        	this.mapa.Nothing(this.player);
        
        this.ready = true;
    }

    /**
     * No observations received
     */
    public void GetObservationsClean()
    {
    	this.blocked = false;
    	this.steps = false;
    	this.breeze = false;
    	this.flash = false;
    	this.ouro = false;
    	this.powerup = false;
    	this.mapa.Nothing(this.player);
    	this.ready = true;
    }

    /**
     * Get Decision
     * @return command string to new decision
     */
	public String GetDecision()
	{
		if(this.ready == true){
		
		String best_decision;
		Position casa_da_frente = CasaDaFrente();
		Position casa_de_tras = CasaDeTras();
		Position casa_da_direita = CasaDaDireita();
		Position casa_da_esquerda = CasaDaEsquerda();
		if(this.primeira_decisao == true){
			this.primeira_decisao = false;
			this.mapa.InitialPosition(this.player);
			return "";
		}
		if(this.ultima_acao_andar == true)
			this.destino = this.pathfinder.ProximaCasa(this.player);
		this.mapa.CasaVisitada(this.player);
		
		if(this.ouro == true){
			if(this.ultima_acao_pegar_anel){
				best_decision = "pegar_ouro";
				this.ultima_acao_pegar_anel = false;
			}
			else{
				best_decision = "pegar_anel";
				this.ultima_acao_pegar_anel = true;
			}
			this.ultima_acao_andar = false;
		}
		else if(this.powerup == true){
			best_decision = "pegar_powerup";
			this.ultima_acao_andar = false;
		}
		else if(this.steps == true){
			if(this.ultima_acao_atirar){
				best_decision = "virar_direita";
				this.ultima_acao_atirar = false;
			}
			else{
				best_decision = "atacar";
				this.ultima_acao_atirar = true;
			}
			this.ultima_acao_andar = false;
		}
		else if(this.destino == null){
			best_decision = "andar";
		}
		else if(casa_da_frente.x == this.destino.x && casa_da_frente.y == this.destino.y){
			best_decision = "andar";
			this.ultima_acao_andar = true;
		}else if(casa_de_tras.x == this.destino.x && casa_de_tras.y == this.destino.y){
			best_decision = "andar_re";
			this.ultima_acao_andar = true;
		}else if(casa_da_direita.x == this.destino.x && casa_da_direita.y == this.destino.y){
			best_decision = "virar_direita";
			this.ultima_acao_andar = false;
		}else if(casa_da_esquerda.x == this.destino.x && casa_da_esquerda.y == this.destino.y){
			best_decision = "virar_esquerda";
			this.ultima_acao_andar = false;
		}
		else{
			best_decision = "";
        	this.pathfinder.ResetPathFinder();
			this.ultima_acao_andar = true;
		}
		
		System.out.println(best_decision);
		this.ready = false;
		return best_decision;
		
		}
		return "";
	}
	
	public void ResetGame(){
		this.blocked = false;
		this.steps = false;
		this.breeze = false;
		this.flash = false;
		this.ouro = false;
		this.powerup = false;
		this.ultima_acao_andar = true;
		this.primeira_decisao = true;
		
		this.pathfinder.ResetPathFinder();
		this.mapa.ResetMap();
	}
}
