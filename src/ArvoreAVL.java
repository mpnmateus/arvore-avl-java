public class ArvoreAVL {
    private No raiz;

    // ------------ MÉTODOS PÚBLICOS (conforme contrato) ------------
    public void inserir(int v) {
        raiz = inserirRec(raiz, v);
    }

    public void remover(int v) {
        raiz = removerRec(raiz, v);
    }

    // Imprime "Caminho: ..." e "Encontrado: sim|não", retorna boolean
    public boolean buscar(int v) {
        StringBuilder caminho = new StringBuilder();
        No atual = raiz;
        boolean found = false;

        while (atual != null) {
            if (caminho.length() > 0) caminho.append(' ');
            caminho.append(atual.getValor());

            if (v == atual.getValor()) { found = true; break; }
            else if (v < atual.getValor()) atual = atual.getEsquerda();
            else atual = atual.getDireita();
        }

        System.out.println("Caminho: " + caminho.toString());
        System.out.println("Encontrado: " + (found ? "sim" : "não"));
        return found;
    }

    public String percursoPreOrdem() {
        StringBuilder sb = new StringBuilder();
        pre(raiz, sb);
        return sb.toString().trim();
    }

    public String percursoEmOrdem() {
        StringBuilder sb = new StringBuilder();
        em(raiz, sb);
        return sb.toString().trim();
    }

    public String percursoPosOrdem() {
        StringBuilder sb = new StringBuilder();
        pos(raiz, sb);
        return sb.toString().trim();
    }

    // Retorna somente o desenho (o cabeçalho "Árvore:" é do Main)
    public String imprimirArvore() {
        StringBuilder sb = new StringBuilder();
        if (raiz == null) {
            sb.append("(vazia)\n");
            return sb.toString();
        }
        // raiz sempre começa com "└──"
        sb.append("└── ").append(raiz.getValor()).append('\n');
        // filhos: decide conectores conforme existência do par
        desenhar(raiz.getEsquerda(), sb, "    ", temAmbos(raiz) ? '├' : '└');
        desenhar(raiz.getDireita(),  sb, "    ", '└');
        return sb.toString();
    }

    // Utilidade pública usada pelo Main para checar duplicados/inexistentes
    public boolean contem(int v) {
        No n = raiz;
        while (n != null) {
            if (v == n.getValor()) return true;
            n = (v < n.getValor()) ? n.getEsquerda() : n.getDireita();
        }
        return false;
    }

    // ------------ PRIVADOS (balanceamento) ------------
    private int altura(No n) { return (n == null) ? 0 : n.getAltura(); }

    private int fatorBalanceamento(No n) {
        return (n == null) ? 0 : altura(n.getEsquerda()) - altura(n.getDireita());
    }

    private No rotacaoDireita(No y) {
        No x = y.getEsquerda();
        No t2 = (x != null) ? x.getDireita() : null;

        // Rotaciona
        x.setDireita(y);
        y.setEsquerda(t2);

        // Atualiza alturas
        y.setAltura(1 + Math.max(altura(y.getEsquerda()), altura(y.getDireita())));
        x.setAltura(1 + Math.max(altura(x.getEsquerda()), altura(x.getDireita())));

        return x;
    }

    private No rotacaoEsquerda(No x) {
        No y = x.getDireita();
        No t2 = (y != null) ? y.getEsquerda() : null;

        // Rotaciona
        y.setEsquerda(x);
        x.setDireita(t2);

        // Atualiza alturas
        x.setAltura(1 + Math.max(altura(x.getEsquerda()), altura(x.getDireita())));
        y.setAltura(1 + Math.max(altura(y.getEsquerda()), altura(y.getDireita())));

        return y;
    }

    private No inserirRec(No no, int v) {
        if (no == null) return new No(v);

        if (v < no.getValor()) {
            no.setEsquerda(inserirRec(no.getEsquerda(), v));
        } else if (v > no.getValor()) {
            no.setDireita(inserirRec(no.getDireita(), v));
        } else {
            // duplicado: apenas retorna o próprio nó (Main já trata a mensagem)
            return no;
        }

        // atualiza altura
        no.setAltura(1 + Math.max(altura(no.getEsquerda()), altura(no.getDireita())));

        // balanceia
        int fb = fatorBalanceamento(no);

        // LL
        if (fb > 1 && v < no.getEsquerda().getValor())
            return rotacaoDireita(no);

        // LR
        if (fb > 1 && v > no.getEsquerda().getValor()) {
            no.setEsquerda(rotacaoEsquerda(no.getEsquerda()));
            return rotacaoDireita(no);
        }

        // RR
        if (fb < -1 && v > no.getDireita().getValor())
            return rotacaoEsquerda(no);

        // RL
        if (fb < -1 && v < no.getDireita().getValor()) {
            no.setDireita(rotacaoDireita(no.getDireita()));
            return rotacaoEsquerda(no);
        }

        return no; // já balanceado
    }

    private No removerRec(No no, int v) {
        if (no == null) return null;

        if (v < no.getValor()) {
            no.setEsquerda(removerRec(no.getEsquerda(), v));
        } else if (v > no.getValor()) {
            no.setDireita(removerRec(no.getDireita(), v));
        } else {
            // achou
            if (no.getEsquerda() == null || no.getDireita() == null) {
                No filho = (no.getEsquerda() != null) ? no.getEsquerda() : no.getDireita();
                no = filho; // pode virar null (0 filho) ou o único filho (1 filho)
            } else {
                // dois filhos: usa o sucessor (mínimo da direita)
                No sucessor = minValorNo(no.getDireita());
                no.setValor(sucessor.getValor());
                no.setDireita(removerRec(no.getDireita(), sucessor.getValor()));
            }
        }

        if (no == null) return null;

        // atualiza altura
        no.setAltura(1 + Math.max(altura(no.getEsquerda()), altura(no.getDireita())));

        // balanceia
        int fb = fatorBalanceamento(no);

        // LL
        if (fb > 1 && fatorBalanceamento(no.getEsquerda()) >= 0)
            return rotacaoDireita(no);

        // LR
        if (fb > 1 && fatorBalanceamento(no.getEsquerda()) < 0) {
            no.setEsquerda(rotacaoEsquerda(no.getEsquerda()));
            return rotacaoDireita(no);
        }

        // RR
        if (fb < -1 && fatorBalanceamento(no.getDireita()) <= 0)
            return rotacaoEsquerda(no);

        // RL
        if (fb < -1 && fatorBalanceamento(no.getDireita()) > 0) {
            no.setDireita(rotacaoDireita(no.getDireita()));
            return rotacaoEsquerda(no);
        }

        return no;
    }

    private No minValorNo(No n) {
        No atual = n;
        while (atual != null && atual.getEsquerda() != null) {
            atual = atual.getEsquerda();
        }
        return atual;
    }

    // ------------ PRIVADOS (percursos) ------------
    private void pre(No n, StringBuilder sb) {
        if (n == null) return;
        sb.append(n.getValor()).append(' ');
        pre(n.getEsquerda(), sb);
        pre(n.getDireita(), sb);
    }

    private void em(No n, StringBuilder sb) {
        if (n == null) return;
        em(n.getEsquerda(), sb);
        sb.append(n.getValor()).append(' ');
        em(n.getDireita(), sb);
    }

    private void pos(No n, StringBuilder sb) {
        if (n == null) return;
        pos(n.getEsquerda(), sb);
        pos(n.getDireita(), sb);
        sb.append(n.getValor()).append(' ');
    }

    // ------------ PRIVADOS (desenho ASCII) ------------
    private void desenhar(No n, StringBuilder sb, String prefixo, char conector) {
        if (n == null) return;

        sb.append(prefixo)
          .append(conector).append("── ")
          .append(n.getValor()).append('\n');

        String novoPref = prefixo + "    ";
        if (temAmbos(n)) {
            // quando tem os dois filhos: esquerda usa '├', direita usa '└'
            desenhar(n.getEsquerda(), sb, novoPref, '├');
            desenhar(n.getDireita(),  sb, novoPref, '└');
        } else if (n.getEsquerda() != null) {
            desenhar(n.getEsquerda(), sb, novoPref, '└');
        } else if (n.getDireita() != null) {
            desenhar(n.getDireita(),  sb, novoPref, '└');
        }
    }

    private boolean temAmbos(No n) {
        return n != null && n.getEsquerda() != null && n.getDireita() != null;
    }
}
