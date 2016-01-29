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

    int[][] matrizDeAdjacencia;
    int[] pe;
    int[] ps;
    int[] pai;
    int[] back;
    int[] cor;
    int t;
    
    ArrayList<ArrayList> componentesConexas = new ArrayList<>();
    boolean isBipartido = true;
    ArrayList<Integer> articulacoes = new ArrayList<>();
    ArrayList<ArrayList> blocos = new ArrayList<>();
    
    ArrayList<Aresta> arestasProfundidade;
    ArrayList<Aresta> arestasRetorno;

    public AnalisadorDeGrafo(int[][] matrizDeAdjacencia) {
        this.matrizDeAdjacencia = matrizDeAdjacencia;
        this.pe = new int[matrizDeAdjacencia.length];
        this.ps = new int[matrizDeAdjacencia.length];
        this.pai = new int[matrizDeAdjacencia.length];
        this.back = new int[matrizDeAdjacencia.length];
        this.arestasProfundidade = new ArrayList<>();
        this.arestasRetorno = new ArrayList<>();
        t = 0;
    }

    private Integer[] vizinhanca(int v) {
        ArrayList<Integer> vizinhos_aux = new ArrayList<>();
        Integer[] vizinhos;

        for (int col = 0; col < matrizDeAdjacencia.length; col++) {
            if (matrizDeAdjacencia[v][col] == 1) {
                vizinhos_aux.add(col);
            }
        }
        vizinhos = new Integer[vizinhos_aux.size()];
        return vizinhos_aux.toArray(vizinhos);
    }

    public void buscaEmProfundidade(int v) {
        t++;
        pe[v] = t;
        back[v] = t;
        for (int w : vizinhanca(v)) {
            if (pe[w] == 0) {
//                marcar vw como aresta "azul" de profundidade
                Aresta aresta = new Aresta(v, w, Aresta.TIPO.PROFUNDIDADE);
                arestasProfundidade.add(aresta);
                
//                TODO: Falta!!!!!!
                //ArrayList<Aresta> componentesBloco = ;
                blocos.add(blocos);
                        
                ArrayList<Integer> conjuntoConexo = componentesConexas.get(componentesConexas.size());
                conjuntoConexo.add(w);
                componentesConexas.add(conjuntoConexo);
                pai[w] = v;
                cor[w] = 1 - cor[v];
                buscaEmProfundidade(w);
                if(back[w] >= pe[v]){
                    articulacoes.add(v);
                }
                back[v] = Integer.min(back[v], back[w]);
            } else if ( ps[w] == 0 && w != pai[v] ) {
//                marcar vw como aresta "vermelha" de retorno
                arestasRetorno.add(new Aresta(v, w, Aresta.TIPO.RETORNO));
                back[v] = Integer.min(back[v], pe[w]);
                
                if(isBipartido && cor[w] == cor[v]){
                    isBipartido = false;
                }
            }
        }
        t++;
        ps[v] = t;
    }

    private boolean isConexo(){
        return componentesConexas.size() == 1;
    }
    
    private boolean possuiCiclos(){
        return !arestasRetorno.isEmpty();
    }
    
    public void analisar(int[][] matrizDeAdjacencia){
        ArrayList<Integer> conjuntoConexo = new ArrayList<>();
        conjuntoConexo.add(0);
        componentesConexas.add(conjuntoConexo);
        buscaEmProfundidade(0);
        for (int v = 1; v < matrizDeAdjacencia.length; v++) {
            if(pe[v] == 0){
                conjuntoConexo.clear();
                conjuntoConexo.add(v);
                componentesConexas.add(conjuntoConexo);
                buscaEmProfundidade(v);
            }
        }
        
    }
    
    
    
    public static void main(String[] args) {
        
    }

}
