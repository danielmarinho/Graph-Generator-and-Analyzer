/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphgeneratorandanalyzer;

import estruturadedados.Aresta;
import java.util.ArrayList;

/**
 *
 * @author danielmarinho
 */
public class AnalisadorDeGrafo {
    //-----------Para Floyd Warshall e Carteiro Chines------------
    int[][] D;
    int[][] P;
    ArrayList<Aresta> arestasTotais;
    ArrayList<Integer> verticesGrauImpar = new ArrayList<>();
    //------------------------------------------
    
    int[][] matrizDeAdjacencia;
    int[] pe;
    int[] ps;
    int[] pai;
    int[] back;
    int[] cor;
    int t;

    ArrayList<ArrayList> componentesConexas = new ArrayList<>();
    ArrayList<Integer> conjuntoConexo = new ArrayList<>();

    boolean isBipartido = true;

    ArrayList<Integer> articulacoes = new ArrayList<>();
    ArrayList<ArrayList> blocos = new ArrayList<>();
    ArrayList<Aresta> componentesBloco = new ArrayList<>();
    ArrayList<Aresta> pontes = new ArrayList<>();

    ArrayList<Aresta> arestasProfundidade;
    ArrayList<Aresta> arestasRetorno;

    //Construtor
    //Inicialização das variáveis   
    public AnalisadorDeGrafo(int[][] matrizDeAdjacencia) {
        this.matrizDeAdjacencia = matrizDeAdjacencia;
        this.pe = new int[matrizDeAdjacencia.length];
        this.ps = new int[matrizDeAdjacencia.length];
        this.pai = new int[matrizDeAdjacencia.length];
        for (int i = 0; i < matrizDeAdjacencia.length; i++) {
            pai[i] = -1;
        }
        this.back = new int[matrizDeAdjacencia.length];
        this.cor = new int[matrizDeAdjacencia.length];
        this.arestasProfundidade = new ArrayList<>();
        this.arestasRetorno = new ArrayList<>();
        this.arestasTotais = encontraArestasTotais();
        t = 0;
        for (int i = 0; i < matrizDeAdjacencia.length; i++) {
            if (!(vizinhanca(i).length % 2 == 0)) {
                verticesGrauImpar.add(i);
            }
        }
    }

    
    //Método que encontra a vizinhança de um determinado vértice
    //A matriz de adjacência é a variável da instância
    private Integer[] vizinhanca(int v) {
        ArrayList<Integer> vizinhos_aux = new ArrayList<>();
        Integer[] vizinhos;

        for (int lin = 0; lin < matrizDeAdjacencia.length; lin++) {
            if (matrizDeAdjacencia[lin][v] == 1) {
                if(!vizinhos_aux.contains(lin))
                    vizinhos_aux.add(lin);
            }
            if (lin == v) {
                for (int col = 0; col < matrizDeAdjacencia.length; col++) {
                    if (matrizDeAdjacencia[v][col] == 1) {
                        if(!vizinhos_aux.contains(col))
                            vizinhos_aux.add(col);
                    }
                }
            }
        }
        vizinhos = new Integer[vizinhos_aux.size()];
        return vizinhos_aux.toArray(vizinhos);
    }
    
    
    //Método que encontra a vizinhança de um determinado vértice 
    //A matriz de adjacência é passada como parâmetro
    private Integer[] vizinhanca(int [][] matrizDeAdjacencia, int v) {
        ArrayList<Integer> vizinhos_aux = new ArrayList<>();
        Integer[] vizinhos;

        for (int lin = 0; lin < matrizDeAdjacencia.length; lin++) {
            if (matrizDeAdjacencia[lin][v] == 1) {
                if(!vizinhos_aux.contains(lin))
                    vizinhos_aux.add(lin);
            }
            if (lin == v) {
                for (int col = 0; col < matrizDeAdjacencia.length; col++) {
                    if (matrizDeAdjacencia[v][col] == 1) {
                        if(!vizinhos_aux.contains(col))
                            vizinhos_aux.add(col);
                    }
                }
            }
        }
        vizinhos = new Integer[vizinhos_aux.size()];
        return vizinhos_aux.toArray(vizinhos);
    }

    
    //Método que executa a busca em profundidade
    //A busca foi modificada do algoritmo básico com adições para encontrar tudo o que foi pedido
    public void buscaEmProfundidade(int v) {
        t++;
        pe[v] = t;
        back[v] = t;
        
        //a variável filhos é usada para facilitar 
        int filhos = 0;
        for (int w : vizinhanca(v)) {
            if (pe[w] == 0) {
                filhos++;
                Aresta aresta = new Aresta(v, w, Aresta.TIPO.PROFUNDIDADE);
                arestasProfundidade.add(aresta);

                conjuntoConexo.add(w);
                componentesBloco.add(aresta);

                pai[w] = v;
                cor[w] = 1 - cor[v];

                buscaEmProfundidade(w);
                back[v] = Integer.min(back[v], back[w]);
                if (back[v] == back[w] - 1) {
                    pontes.add(aresta);
                }
                if (back[w] >= pe[v]) {
                    if (pai[v] == -1) {
                        if (filhos >= 2) {
                            if (!articulacoes.contains(v)) {
                                articulacoes.add(v);     //v é uma articulação
                            }
                        }
                    } else {
                        if (!articulacoes.contains(v)) {
                            articulacoes.add(v);     //v é uma articulação
                        }
                    }

                    ArrayList<Aresta> blocoAux = new ArrayList<>();
                    int index = componentesBloco.size() - 1;
                    Aresta arestaAux = componentesBloco.get(index);
                    while (!arestaAux.equals(aresta)) {
                        blocoAux.add(arestaAux);
                        componentesBloco.remove(index);
                        index--;
                        arestaAux = componentesBloco.get(index);
                    }
                    blocoAux.add(arestaAux);
                    componentesBloco.remove(index);
                    blocos.add(blocoAux);
                }
            } else if (ps[w] == 0 && w != pai[v]) {
//                marcar vw como aresta "vermelha" de retorno
                Aresta aresta = new Aresta(v, w, Aresta.TIPO.RETORNO);
                arestasRetorno.add(aresta);
                componentesBloco.add(aresta);

                back[v] = Integer.min(back[v], pe[w]);

                if (isBipartido && cor[w] == cor[v]) {
                    isBipartido = false;
                }
            }
        }
        t++;
        ps[v] = t;
    }

    private void localizaPontes() {
        for (Aresta aresta : arestasProfundidade) {
            if (articulacoes.contains(aresta.getOrigem()) && articulacoes.contains(aresta.getDestino())) {
                pontes.add(aresta);
            }
        }
    }

    private boolean isEulerianoCircuito() {
        if (isConexo()) {               //deve ser conexo
            for (int i = 0; i < matrizDeAdjacencia.length; i++) {
                if (!(vizinhanca(i).length % 2 == 0)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean isEuleriano() {
        int numDeVerticesDeGrauImpar = 0;
        for (int i = 0; i < matrizDeAdjacencia.length; i++) {
            if (!(vizinhanca(i).length % 2 == 0)) {
                numDeVerticesDeGrauImpar++;
                if (numDeVerticesDeGrauImpar > 2) {
                    return false;
                }

            }
        }
        return true;
    }

    private boolean isBipartido(){
        return isBipartido && isConexo();
    }
    
    private boolean isConexo() {
        return componentesConexas.size() == 1;
    }

    private boolean possuiCiclos() {
        return !arestasRetorno.isEmpty();
    }

    private boolean isArvore() {
        return arestasRetorno.isEmpty() && isConexo();
    }
    
    private ArrayList<Aresta> encontraArestasTotais(){
        ArrayList<Aresta> temp = new ArrayList<>();
        for (int v = 0; v < matrizDeAdjacencia.length; v++) {
            for (int w : vizinhanca(v)) {
                if(w > v)
                    temp.add(new Aresta(v,w));
            }
        }
        return temp;
    }
    
    //------------------------CIRCUITO EULERIANO------------------------------------
    
    private ArrayList<Integer> hierholzer(){
        int [][] matrizAdjacenciaLocal = matrizDeAdjacencia.clone();
        ArrayList<Aresta> arestasTotaisLocal = new ArrayList<>(arestasTotais);
        
        ArrayList<Integer> circuito = new ArrayList<>();
        ArrayList<Integer> circuitoLinha = new ArrayList<>();

        int v0 = arestasTotaisLocal.get(0).getOrigem();
        int origem = v0;
        int destino;
        int vIndex = 0;

        circuito.add(origem);
        circuitoLinha.add(origem);
        
        while (!arestasTotaisLocal.isEmpty()) {
            destino = vizinhanca(matrizAdjacenciaLocal, origem)[0];
            atualizaMatrizAdjacencia(removeAresta(arestasTotaisLocal, origem, destino),matrizAdjacenciaLocal);
            origem = destino;
            circuitoLinha.add(destino);
            if (destino == v0) {
                if (vizinhanca(matrizAdjacenciaLocal, destino).length == 0) {
                    circuito.remove(vIndex);
                    circuito.addAll(vIndex, circuitoLinha);
                    circuitoLinha = new ArrayList<>();
                    for (Integer v : circuito) {
                        if (vizinhanca(matrizAdjacenciaLocal, v).length > 0) {
                            v0 = v;
                            origem = v0;
                            vIndex = circuito.indexOf(v);
                            circuitoLinha.add(v0);
                            break;
                        }
                    }
                }
            }
        }
        return circuito;
    }
//------------------------------------------------------------------------------------
    
//-----------------------CARTEIRO CHINÊS----------------------------------------------
    private ArrayList<Integer> carteiroChines(){
        ArrayList<Integer> circuito = new ArrayList<>();
        
        if(isEulerianoCircuito())
            circuito = hierholzer();
        else{
            //deve ser conexo
            if (!isConexo()) 
                return circuito;
            
            // Se no grafo só existirem dois vértices de grau ímpar, devemos encontrar 
            // o menor caminho entre eles e duplicar as arestas ao longo desse caminho
            if(verticesGrauImpar.size() == 2){
                floydWarshall(verticesGrauImpar.get(0));
                ArrayList<Integer> caminho = caminhoEntre(verticesGrauImpar.get(0),verticesGrauImpar.get(1));
                duplicarArestasCaminho(caminho);
                // após termos duplicado as arestas do caminho, devemos rodar
                // novamente o algoritmo para encontrar um circuito euleriano visto
                // que o grafo virou um grafo euleriano e assim podemos encontrar um caminho ótimo
                circuito = hierholzer();
            }else{
            // Se o número de vértices com grau ímpar for maior que 2, devemos fazer uma
            // combinação 2 a 2 para cada par desses vértices e encontrar aqueles que dão o menor caminho
                
            }
        }
        return circuito;
    }
    
    // Utiliza a matriz de predecessores gerada pelo floyd warshall para encontrar o caminho mínimo
    // Retorna um array de vértices em sequência, formando o caminho
    private ArrayList<Integer> caminhoEntre(int v, int w){
        ArrayList<Integer> caminho = new ArrayList<>();
        caminho.add(w);
        int predecessor = P[v][w];
        caminho.add(predecessor);
        int i = predecessor;
        while(predecessor != v){
            predecessor = P[v][i];
            caminho.add(predecessor);
            i = predecessor;
        }
        return caminho;
    }
    
    // Recebe o caminho mínimo entre dois vértices e duplica todas as arestas
    // que o caminho percorre
    private void duplicarArestasCaminho(ArrayList<Integer> caminho) {
        ArrayList<Aresta> aux = new ArrayList<>();
        int origem = caminho.remove(caminho.size() - 1);
        while (!caminho.isEmpty()) {
            int destino = caminho.remove(caminho.size() - 1);

            for (Aresta aresta : arestasTotais) {
                // como não é um dígrafo, devemos testar os dois casos de origem e destino para então
                // adicionar uma nova aresta
                if (aresta.getOrigem() == origem && aresta.getDestino() == destino) {
                    aux.add(new Aresta(destino, origem));
                    matrizDeAdjacencia[destino][origem] = 1;
                } else if (aresta.getOrigem() == destino && aresta.getDestino() == origem) {
                    aux.add(new Aresta(origem, destino));
                    matrizDeAdjacencia[origem][destino] = 1;
                }
            }
            origem = destino;
        }
        arestasTotais.addAll(aux);
    }
    //----------------------------------------------------------------------------------
    
    //---------------------------------FLOYD WARSHALL-------------------------------
    private void floydWarshall(int v){
         D = inicializa_pesos_D0();
        int[][] D_anterior = D.clone();
         P = inicializa_predecessores();
        int[][] P_anterior = P.clone();
        
        for (int k = 0; k < matrizDeAdjacencia.length; k++) {
            for (int i = 0; i < matrizDeAdjacencia.length; i++) {
                for (int j = 0; j < matrizDeAdjacencia.length; j++) {
                    if(D_anterior[i][j] <= (D_anterior[i][k] + D_anterior[k][j])){
                        D[i][j] = D_anterior[i][j];
                        P[i][j] = P_anterior[i][j];
                    }else{
                        D[i][j] = (D_anterior[i][k] + D_anterior[k][j]);
                        P[i][j] = P_anterior[k][j];
                    }
                }
            }
            D_anterior = D.clone();
            P_anterior = P.clone();
        }
    }
    
    // Todos os pesos são iguais a 1
    // Inicializa a matriz D0 do floyd warshall
    private int[][] inicializa_pesos_D0(){
        int[][] w = new int[matrizDeAdjacencia.length][matrizDeAdjacencia.length];
        for (int i = 0; i < matrizDeAdjacencia.length; i++) {
            for (int j = 0; j < matrizDeAdjacencia.length; j++) {
                
                if(i==j)
                    w[i][j] = 0;
                else if( (matrizDeAdjacencia[i][j] == 1) || (matrizDeAdjacencia[j][i] == 1) ){ //nossa matriz de adjacência é pela metade
                    w[i][j] = 1; //nossas arestas não são direcionadas, portanto se existe ligação, ela vai e volta
                    w[j][i] = 1;
                }
                else
                    w[i][j] = 100000; //infinito
            }
        }
        return w;
    }
    
    // inicializa a matriz de predecessores do floyd warshall
    private int[][] inicializa_predecessores(){
    int[][] temp = new int[matrizDeAdjacencia.length][matrizDeAdjacencia.length];
        for (int i = 0; i < matrizDeAdjacencia.length; i++) {
            for (int j = 0; j < matrizDeAdjacencia.length; j++) {
                if( (matrizDeAdjacencia[i][j] == 1) || (matrizDeAdjacencia[j][i] == 1) ){ //nossa matriz de adjacência é pela metade
                    temp[i][j] = i; //nossas arestas não são direcionadas, portanto se existe ligação, ela vai e volta
                    temp[j][i] = j;
                }else{
                    temp[i][j] = -1;
                    temp[j][i] = -1;
                }
                    
            }
        }
        return temp;    
    }
    //-------------------------------------------------------------------------------------------
    
//    private static Aresta removeAresta(ArrayList<Aresta> conjunto, ArrayList<Integer> verticesGrauImpar, int origem,int destino){
    private static Aresta removeAresta(ArrayList<Aresta> conjunto, int origem, int destino) {
        for (Aresta aresta : conjunto) {
            //testa ambas as possibilidades pois não é um digrafo!!!
            if ((aresta.getOrigem() == origem && aresta.getDestino() == destino) || (aresta.getOrigem() == destino && aresta.getDestino() == origem)) {
                return conjunto.remove(conjunto.indexOf(aresta));
            }
        }
        return null;
    }
    
    private static void atualizaMatrizAdjacencia(Aresta aresta, int[][] matrizAdjacencia) {
        matrizAdjacencia[aresta.getOrigem()][aresta.getDestino()] = 0;
    }
    
    public void analisar() {
        conjuntoConexo.add(0);
        buscaEmProfundidade(0);
        localizaPontes();

        //TODO: rever nome variavel aux
        ArrayList<Integer> aux = new ArrayList<>(conjuntoConexo);
        componentesConexas.add(aux);
        conjuntoConexo.clear();
        for (int v = 1; v < matrizDeAdjacencia.length; v++) {
            if (pe[v] == 0) {
                conjuntoConexo.add(v);
                buscaEmProfundidade(v);
                aux = new ArrayList<>(conjuntoConexo);
                componentesConexas.add(aux);
                conjuntoConexo.clear();
            }
        }
//        System.out.println("É conexo? R:" + isConexo());
//        System.out.println("É árvore? R:" + isArvore());
//        System.out.println("Possui ciclos? R:" + possuiCiclos());
//        System.out.println("É bipartido? R:" + isBipartido());
//        System.out.println("Articulações: " + articulacoes.toString());
//        System.out.println("Pontes: " + pontes.toString());
//        System.out.println("Componentes Conexas: " + componentesConexas.toString());
//        System.out.println("Quantidade de blocos -> #" + blocos.size() + ": " + blocos.toString());
//        System.out.println("É Euleriano? R: " + isEulerianoCircuito());
//        if(isEulerianoCircuito())
//            System.out.println("Circuito Euleriano: " + hierholzer().toString());
        
        System.out.println("Circuito carteiro chinês: " + carteiroChines().toString());
    }

    public static void main(String[] args) {
//        int[][] matriz = GeradorDeGrafo.executar();
//        int matriz[][] = {{0,0,1,1,0},{0,0,1,1,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};
//        int matriz[][] = {{0,1,1,1,1},{0,0,1,1,1},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};
//        int matriz[][] = {{0,1,1,1,1},{0,0,1,1,1},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};
//        int matriz[][] = {{0,1,1,0,0,0},{0,0,1,1,1,0},{0,0,0,1,1,0},{0,0,0,0,1,1},{0,0,0,0,0,1},{0,0,0,0,0,0}};
//        int matriz[][] = {{0,1,1,1,1,0},{0,0,1,1,1,0},{0,0,0,1,0,1},{0,0,0,0,1,0},{0,0,0,0,0,1},{0,0,0,0,0,0}};
        int matriz[][] = {{0,1,1,1,0},{0,0,0,1,0},{0,0,0,1,0},{0,0,0,0,1},{0,0,0,0,0}}; //grafo com dois vértices de grau ímpar
        AnalisadorDeGrafo analisador = new AnalisadorDeGrafo(matriz);
        analisador.analisar();
    }
}
