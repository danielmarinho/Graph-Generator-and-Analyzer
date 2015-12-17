/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geradordegrafo;

/**
 *
 * @author danielmarinho
 */
import java.util.Scanner;

public class GeradorDeGrafo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        System.out.println("Gerador de Grafos\n\n");
        System.out.println("Digite o número de vértices do Grafo:");
        int n = entrada.nextInt();
        System.out.println("Digite o número de arestas do Grafo: (opcional, 0 para não especificado)");
        int m = entrada.nextInt();
        System.out.println("Digite a probabilidade da aresta entrar:");
        float p = entrada.nextFloat();
        if (m == 0) {
            m = (int) (((n * (n - 1)) / 2) * Math.random());
            System.out.println(m);
        } else if (m > ((n * (n - 1)) / 2)) {
            System.out.println("Erro: Quantidade máxima de arestas para esses vértices é " + ((n * (n - 1)) / 2));
            return;
        }

        int[][] matrizDeAdjacencia = new int[n][n];
        
//        Inicializaçao da Matriz
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                matrizDeAdjacencia[i][j] = 0;
            }
        }

//        Inserçao das arestas de acordo com a probabilidade dada
        int mCounter = m;
        Sair:
        while (mCounter > 0) {
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (matrizDeAdjacencia[i][j] != 1) {
                        if (Math.random() <= p) {
                            matrizDeAdjacencia[i][j] = 1;
                            mCounter--;
                            if(mCounter ==0)
                                break Sair;
                        }
                    }
                }
            }
            
        }
        
//        Impressao da matriz de adjacencia
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                System.out.print(matrizDeAdjacencia[i][j]);                               
            }
            System.out.println("");
        }
        
    }

}
