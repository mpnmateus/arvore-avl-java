public class No {
    private int valor; // dado armazenado
    private int altura; // altura do nó
    private No esquerda; // filho esquerdo
    private No direita; // filho direito

    public No(int valor) {
        this.valor = valor;
        this.altura = 1; // nó novo nasce com altura 1
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public No getEsquerda() {
        return esquerda;
    }

    public void setEsquerda(No esquerda) {
        this.esquerda = esquerda;
    }

    public No getDireita() {
        return direita;
    }

    public void setDireita(No direita) {
        this.direita = direita;
    }
}