import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    private static void printlnLF(String texto) {
        System.out.print(texto + "\n");
    }

    public static void main(String[] args) throws Exception {
        ArvoreAVL arvore = new ArvoreAVL();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        String linha;

        while ((linha = br.readLine()) != null) {
            linha = linha.trim();
            if (linha.isEmpty()) continue;

            String[] parts = linha.split("\\s+");
            String cmd = parts[0].toLowerCase();

            if (cmd.equals("sair")) {
                printlnLF("Encerrado.");
                break;
            }

            try {
                switch (cmd) {
                    case "i": {
                        if (parts.length != 2) break;
                        int v = Integer.parseInt(parts[1]);
                        if (arvore.contem(v)) printlnLF("Ignorado: duplicado " + v);
                        else { arvore.inserir(v); printlnLF("OK: inserido " + v); }
                        printlnLF("Árvore:");
                        System.out.print(arvore.imprimirArvore());
                        break;
                    }
                    case "r": {
                        if (parts.length != 2) break;
                        int v = Integer.parseInt(parts[1]);
                        if (!arvore.contem(v)) printlnLF("Ignorado: inexistente " + v);
                        else { arvore.remover(v); printlnLF("OK: removido " + v); }
                        printlnLF("Árvore:");
                        System.out.print(arvore.imprimirArvore());
                        break;
                    }
                    case "b": {
                        if (parts.length != 2) break;
                        int v = Integer.parseInt(parts[1]);
                        arvore.buscar(v);
                        break;
                    }
                    case "pre": printlnLF("Pré-Ordem: " + arvore.percursoPreOrdem()); break;
                    case "em":  printlnLF("Em-Ordem: " + arvore.percursoEmOrdem()); break;
                    case "pos": printlnLF("Pós-Ordem: " + arvore.percursoPosOrdem()); break;
                    default: break;
                }
            } catch (NumberFormatException e) { /* ignora entradas inválidas */ }
        }
    }
}