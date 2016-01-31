/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estruturadedados;

/**
 *
 * @author danielmarinho
 */
public class Aresta {
 
   public enum TIPO{
        PROFUNDIDADE, RETORNO;
    }
   
   TIPO tipo;
   int origem, destino;

   public Aresta(int origem, int destino) {
        this.origem = origem;
        this.destino = destino;
    }
    
    public Aresta(int origem, int destino, TIPO tipo) {
        this.origem = origem;
        this.destino = destino;
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Aresta{" + tipo +" "+ origem +"-"+ destino + '}';
    }
   
    public TIPO getTipo() {
        return tipo;
    }

    public void setTipo(TIPO tipo) {
        this.tipo = tipo;
    }

    public int getOrigem() {
        return origem;
    }

    public void setOrigem(int origem) {
        this.origem = origem;
    }

    public int getDestino() {
        return destino;
    }

    public void setDestino(int destino) {
        this.destino = destino;
    }

    public boolean isProfundidade(){
        return this.tipo == Aresta.TIPO.PROFUNDIDADE;
    }
    
    public boolean isRetorno(){
        return this.tipo == Aresta.TIPO.RETORNO;
    }
}
