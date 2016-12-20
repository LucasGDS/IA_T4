package INF1771_GameAI.Map;

import java.util.ArrayList;
import java.util.List;

public class MapInfo {
	private static MapInfo instance;
	public char mapa[][] = new char[59][34];
	List<Position> nos_abertos = new ArrayList<Position>();
	boolean nos_ja_visitados[][] = new boolean[59][34];
	int proximo_no_aberto;
	
	
	private MapInfo(){
		this.proximo_no_aberto = 0;
		for(int i = 0; i < 59; ++i) {
		    for(int j = 0; j < 34; ++j) {
		        mapa[i][j] = '?';
		        nos_ja_visitados[i][j] = false;
		    }
		}
	}
	
	//Pegar instancia. MapInfo eh um singleton
	public static MapInfo GetInstance(){
		if(instance == null){
			instance = new MapInfo();
		}
		return instance;
	}
	
	//Adiciona os nos vizinhos ao inicial aos nos abertos
	public void InitialPosition(Position inicial){
		if(inicial.x<58)
			this.nos_abertos.add(new Position(inicial.x + 1, inicial.y));
		if(inicial.x>0)
			this.nos_abertos.add(new Position(inicial.x - 1, inicial.y));
		if(inicial.y<33)
			this.nos_abertos.add(new Position(inicial.x, inicial.y + 1));
		if(inicial.y>0)
			this.nos_abertos.add(new Position(inicial.x, inicial.y - 1));
	}
	
	//Funcao chamada se for observada uma brisa, para adicionar possiveis buracos em volta
	public void AddBreeze(Position position){
		int x = position.x;
		int y = position.y;
		this.mapa[x][y] = '.';
		if(x<58)
			if(this.mapa[x+1][y] == '?')
				this.mapa[x+1][y] = 'P';
		if(x>0)
			if(this.mapa[x-1][y] == '?')
				this.mapa[x-1][y] = 'P';
		if(y<33)
			if(this.mapa[x][y+1] == '?')
				this.mapa[x][y+1] = 'P';
		if(y>0)
			if(this.mapa[x][y-1] == '?')
				this.mapa[x][y-1] = 'P';
	}
	
	//Funcao chamada se for observada uma flash, para adicionar possiveis teletransportes em volta
	public void AddFlash(Position position){
		int x = position.x;
		int y = position.y;
		this.mapa[x][y] = '.';
		if(x<58)
			if(this.mapa[x+1][y] == '?')
				this.mapa[x+1][y] = 'T';
		if(x>0)
			if(this.mapa[x-1][y] == '?')
				this.mapa[x-1][y] = 'T';
		if(y<33)
			if(this.mapa[x][y+1] == '?')
				this.mapa[x][y+1] = 'T';
		if(y>0)
			if(this.mapa[x][y-1] == '?')
				this.mapa[x][y-1] = 'T';
	}
	
	//Funcao chamada se for observado um ouro
	public void AddGold(Position position){
		int x = position.x;
		int y = position.y;
		this.mapa[x][y] = 'O';
	}
	
	//Funcao chamada se for observado uma parede
	public void AddWall(Position position){
		int x = position.x;
		int y = position.y;
		if(x < 59 && y < 34 && x > 0 && y > 0)
			this.mapa[x][y] = 'W';
	}
	
	//Funcao chamada se nada for observado, para liberar as casas vizinhas
	public void Nothing(Position position){
		int x = position.x;
		int y = position.y;
		this.mapa[x][y] = '.';
		if(x<58){
			if(this.mapa[x+1][y] != 'O' && this.mapa[x+1][y] != 'W'){
				this.mapa[x+1][y] = '.';
				Position elem = new Position(x+1, y);
				if(this.nos_abertos.contains(elem) == false)
					this.nos_abertos.add(elem);
			}
		}
		if(x>0){
			if(this.mapa[x-1][y] != 'O' && this.mapa[x-1][y] != 'W'){
				this.mapa[x-1][y] = '.';
				Position elem = new Position(x-1, y);
				if(this.nos_abertos.contains(elem) == false)
					this.nos_abertos.add(elem);
			}
		}
		if(y<33){
			if(this.mapa[x][y+1] != 'O' && this.mapa[x][y+1] != 'W'){
				this.mapa[x][y+1] = '.';
				Position elem = new Position(x, y+1);
				if(this.nos_abertos.contains(elem) == false)
					this.nos_abertos.add(elem);
			}
		}
		if(y>0){
			if(this.mapa[x][y-1] != 'O' && this.mapa[x][y-1] != 'W'){
				this.mapa[x][y-1] = '.';
				Position elem = new Position(x, y-1);
				if(this.nos_abertos.contains(elem) == false)
					this.nos_abertos.add(elem);
			}
		}
	}
	
	//Retorna true se a casa nao for um possivel burao ou teletransporte
	public boolean CasaSegura(Position position){
		if(position.x < 59 && position.y < 34 && position.x > 0 && position.y > 0){
			if(this.mapa[position.x][position.y] == 'O' || this.mapa[position.x][position.y] == '.')
					return true;
		}
		return false;
	}
	
	//Marca que a casa ja foi visitada
	public void CasaVisitada(Position position){
		this.nos_ja_visitados[position.x][position.y] = true;
	}
	
	//Pega o proximo no ainda nao visitado. Essa funcao eh chamada pelo pathfinder, para calcular o A* ate esse no
	public Position ProximoNoAberto(){
		if(this.nos_abertos.size() == proximo_no_aberto)
			return null;
		Position prox = this.nos_abertos.get(this.proximo_no_aberto);
		this.proximo_no_aberto ++;
		
		while(this.nos_ja_visitados[prox.x][prox.y]){
			prox = this.nos_abertos.get(this.proximo_no_aberto);
			this.proximo_no_aberto ++;
		}
		return prox;
	}
	
	//Reseta os nos abertos e visitados, mas mantem as informacoes do mapa
	public void ResetMap(){
		this.proximo_no_aberto = 0;
		this.nos_abertos.clear();
		for(int i = 0; i < 59; ++i) {
		    for(int j = 0; j < 34; ++j) {
		        nos_ja_visitados[i][j] = false;
		    }
		}
	}
	
}
