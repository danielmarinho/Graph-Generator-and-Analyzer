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
    ArrayList<Integer> conjuntoConexo = new ArrayList<>();

    boolean isBipartido = true;

    ArrayList<Integer> articulacoes = new ArrayList<>();
    ArrayList<ArrayList> blocos = new ArrayList<>();
    ArrayList<Aresta> componentesBloco = new ArrayList<>();
    ArrayList<Aresta> pontes = new ArrayList<>();

    ArrayList<Aresta> arestasProfundidade;
    ArrayList<Aresta> arestasRetorno;

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
        t = 0;
    }

    private Integer[] vizinhanca(int v) {
        ArrayList<Integer> vizinhos_aux = new ArrayList<>();
        Integer[] vizinhos;

        for (int lin = 0; lin < matrizDeAdjacencia.length; lin++) {
            if (matrizDeAdjacencia[lin][v] == 1) {
                vizinhos_aux.add(lin);
            }
            if (lin == v) {
                for (int col = v + 1; col < matrizDeAdjacencia.length; col++) {
                    if (matrizDeAdjacencia[v][col] == 1) {
                        vizinhos_aux.add(col);
                    }
                }
            }
        }
        vizinhos = new Integer[vizinhos_aux.size()];
        return vizinhos_aux.toArray(vizinhos);
    }

    public void buscaEmProfundidade(int v) {
        t++;
        pe[v] = t;
        back[v] = t;
        int filhos = 0;
        for (int w : vizinhanca(v)) {
            if (pe[w] == 0) {
                filhos++;
//                marcar vw como aresta "azul" de profundidade
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
        for (int i = 0; i < matrizDeAdjacencia.length; i++) {
            if (!(vizinhanca(i).length % 2 == 0)) {
                return false;
            }
        }
        return true;
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

    private boolean isConexo() {
        return componentesConexas.size() == 1;
    }

    private boolean possuiCiclos() {
        return !arestasRetorno.isEmpty();
    }

    private boolean isArvore() {
        if (arestasRetorno.isEmpty() && isConexo()) {
            return true;
        } else {
            return false;
        }
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
        System.out.println("É conexo? R:" + isConexo());
        System.out.println("É árvore? R:" + isArvore());
        System.out.println("Possui ciclos? R:" + possuiCiclos());
        System.out.println("É bipartido? R:" + isBipartido);
        System.out.println("Articulações: " + articulacoes.toString());
        System.out.println("Pontes: " + pontes.toString());
        System.out.println("Componentes Conexas: " + componentesConexas.toString());
        System.out.println("Blocos: " + blocos.toString());
        System.out.println("É Euleriano? R: " + isEuleriano());
    }

    public static void main(String[] args) {
        int[][] matriz = GeradorDeGrafo.executar();
//        int matriz[][] = {{0,0,1,1,0},{0,0,1,1,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};
        AnalisadorDeGrafo analisador = new AnalisadorDeGrafo(matriz);
        analisador.analisar();
    }

}
