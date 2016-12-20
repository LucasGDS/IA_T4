package INF1771_GameAI;

import java.util.ArrayList;

import INF1771_GameAI.Map.*;

public class PathFinder {
	private static PathFinder instance;
	private ArrayList<Position> proximas_casas = new ArrayList<Position>();
	MapInfo mapa;
	
	private PathFinder(){
		this.mapa = MapInfo.GetInstance();
	}
	
	//Pegar instancia. PathFinder eh um singleton
	public static PathFinder GetInstance(){
		if(instance == null){
			instance = new PathFinder();
		}
		return instance;
	}
	
	//Funcao auxiliar que calcula o custo guloso da busca
	private int g(int x, int y){
		if(this.mapa.mapa[x][y] == '.')
			return 20;
		else if(this.mapa.mapa[x][y] == 'O')
			return 0;
		else
			return 1000;
	}
	
	//Funcao auxiliar que calcula o custo heuristico da busca
	private int h(int x, int y, Position destino){
		return Math.abs(x-destino.x)+Math.abs(y-destino.y);
	}
	
	//Funcao que faz uma busca A* da origem para o destino, preenchendo a lista proximas_casas com o caminho
	private void AEstrela(Position origem, Position destino){
		//Estrutura para a busca, um index para as listas representa um no
		ArrayList<Integer> nos_x = new ArrayList<Integer>();
		ArrayList<Integer> nos_y = new ArrayList<Integer>();
		ArrayList<Integer> nos_g = new ArrayList<Integer>();
		ArrayList<Integer> nos_h = new ArrayList<Integer>();
		ArrayList<Integer> nos_pai = new ArrayList<Integer>();
		boolean nos_abertos[][] = new boolean[59][34];
		int corrente = 0;
		
		//Inicializa os valores para a busca
		nos_x.add(origem.x);
		nos_y.add(origem.y);
		nos_g.add(0);
		nos_h.add(h(origem.x,origem.y,destino));
		nos_pai.add(-1);
		for(int i = 0; i < 59; i++) {
		    for(int j = 0; j < 34; j++) {
		    	nos_abertos[i][j] = true;
		    }
		}
		
		//Enquanto nao se encontra o destino
		while(nos_x.get(corrente) != destino.x || nos_y.get(corrente) != destino.y){
			
			//Adiciona casa de cima
			int x = nos_x.get(corrente)+1;
			int y = nos_y.get(corrente);
			if(x < 59){
				if(nos_abertos[x][y]){
					nos_x.add(x);
					nos_y.add(y);
					if(nos_pai.get(corrente) == -1)
						nos_g.add(g(x,y));
					else
						nos_g.add(g(x,y) + nos_g.get(nos_pai.get(corrente)));
					nos_h.add(h(x,y,destino));
					nos_pai.add(corrente);
				}
			}
			
			//Adiciona casa de baixo
			x = nos_x.get(corrente)-1;
			y = nos_y.get(corrente);
			if(x >= 0){
				if(nos_abertos[x][y]){
					nos_x.add(x);
					nos_y.add(y);
					if(nos_pai.get(corrente) == -1)
						nos_g.add(g(x,y));
					else
						nos_g.add(g(x,y) + nos_g.get(nos_pai.get(corrente)));
					nos_h.add(h(x,y,destino));
					nos_pai.add(corrente);
				}
			}
			
			//Adiciona casa da direita
			x = nos_x.get(corrente);
			y = nos_y.get(corrente)+1;
			if(y < 34){
				if(nos_abertos[x][y]){
					nos_x.add(x);
					nos_y.add(y);
					if(nos_pai.get(corrente) == -1)
						nos_g.add(g(x,y));
					else
						nos_g.add(g(x,y) + nos_g.get(nos_pai.get(corrente)));
					nos_h.add(h(x,y,destino));
					nos_pai.add(corrente);
				}
			}
			
			//Adiciona casa da esquerda
			x = nos_x.get(corrente);
			y = nos_y.get(corrente)-1;
			if(y >= 0){
				if(nos_abertos[x][y]){
					nos_x.add(x);
					nos_y.add(y);
					if(nos_pai.get(corrente) == -1)
						nos_g.add(g(x,y));
					else
						nos_g.add(g(x,y) + nos_g.get(nos_pai.get(corrente)));
					nos_h.add(h(x,y,destino));
					nos_pai.add(corrente);
				}
			}
			
			//Corrente vira no fechado, para nao repetir
			nos_abertos[nos_x.get(corrente)][nos_y.get(corrente)] = false;
			
			//Proximo corrente eh o index do no de menor custo
			int custo_minimo = 9999;
			int index_do_custo_minimo = 0;
			for (int i = 0; i < nos_x.size(); i++) {
				if(nos_abertos[nos_x.get(i)][nos_y.get(i)]){
					if((nos_g.get(i) + nos_h.get(i)) < custo_minimo){
						index_do_custo_minimo = i;
						custo_minimo = (nos_g.get(i) + nos_h.get(i));
					}
				}
			}
			corrente = index_do_custo_minimo;
		}
		
		//Preenche a lista proximas_casas com o caminho
		while(corrente != 0){
			this.proximas_casas.add(new Position(nos_x.get(corrente),nos_y.get(corrente)));
			corrente = nos_pai.get(corrente);
		}
		
	}
	
	//Retorna a proxima casa do caminho
	public Position ProximaCasa(Position atual){
		if(this.proximas_casas.size() == 0){
			Position prox = mapa.ProximoNoAberto();
			if(prox == null)
				return null;
			AEstrela(atual,prox);
		}
		if(this.proximas_casas.size() == 0)
			return null;
		return this.proximas_casas.remove(proximas_casas.size() - 1);
	}
	
	//Reinicia o caminho salvo
	public void ResetPathFinder(){
		this.proximas_casas.clear();
	}
	
}
