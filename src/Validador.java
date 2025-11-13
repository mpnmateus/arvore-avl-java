import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Validador {

    private static final String PKG = "";

    public static void main(String[] args) {
        System.out.println("Validador — AVL (simplificado)\n");
        boolean contratosOK = contratos();
        boolean testesOK = testes();
        System.out.println("\nResumo:");
        System.out.println("- Contratos essenciais: " + (contratosOK ? "OK" : "NOK"));
        System.out.println("- Testes de mesa: " + (testesOK ? "OK" : "NOK"));
    }

    // -------------------- CONTRATOS --------------------
    private static boolean contratos() {
        boolean ok = true;
        ok &= classeExiste("No");
        ok &= classeExiste("ArvoreAVL");
        ok &= classeExiste("Main");

        ok &= atributo("No", "valor", "int", Modifier.PRIVATE);
        ok &= atributo("No", "altura", "int", Modifier.PRIVATE);
        ok &= atributo("No", "esquerda", "No", Modifier.PRIVATE);
        ok &= atributo("No", "direita", "No", Modifier.PRIVATE);
        ok &= construtor("No", new String[] { "int" });

        ok &= atributo("ArvoreAVL", "raiz", "No", Modifier.PRIVATE);

        ok &= metodo("ArvoreAVL", "inserir", "void", arr("int"), true, false);
        ok &= metodo("ArvoreAVL", "remover", "void", arr("int"), true, false);
        ok &= metodo("ArvoreAVL", "buscar", "boolean", arr("int"), true, false);
        ok &= metodo("ArvoreAVL", "percursoPreOrdem", "String", arr(), true, false);
        ok &= metodo("ArvoreAVL", "percursoEmOrdem", "String", arr(), true, false);
        ok &= metodo("ArvoreAVL", "percursoPosOrdem", "String", arr(), true, false);
        ok &= metodo("ArvoreAVL", "imprimirArvore", "String", arr(), true, false);

        ok &= metodo("Main", "main", "void", arr("String[]"), true, true);

        System.out.println("\n[Contratos] Resultado: " + (ok ? "OK" : "NOK"));
        return ok;
    }

    // -------------------- TESTES --------------------
    private static boolean testes() {
        boolean ok = true;
        ok &= caso1();
        ok &= caso2();
        ok &= caso3();
        System.out.println("\n[Testes] Resultado: " + (ok ? "OK" : "NOK"));
        return ok;
    }

    private static boolean caso1() {
        String in = String.join("\n", List.of("i 45", "i 20", "i 60", "em", "pre", "pos", "sair")) + "\n";
        String out = runMain(in);
        boolean ok = true;
        ok &= mustContainInOrder(out, List.of(
                "OK: inserido 45", "Árvore:",
                "OK: inserido 20", "Árvore:",
                "OK: inserido 60", "Árvore:",
                "Em-Ordem: 20 45 60",
                "Pré-Ordem: 45 20 60",
                "Pós-Ordem: 20 60 45",
                "Encerrado."));
        ok &= hasTreeBlocks(out, 3);
        System.out.println("[Caso 1] " + (ok ? "OK" : "NOK"));
        return ok;
    }

    private static boolean caso2() {
        String in = String.join("\n", List.of("i 50", "i 30", "i 70", "i 65", "b 65", "b 10", "sair")) + "\n";
        String out = runMain(in);
        boolean ok = true;
        ok &= mustContainInOrder(out, List.of(
                "OK: inserido 50", "Árvore:",
                "OK: inserido 30", "Árvore:",
                "OK: inserido 70", "Árvore:",
                "OK: inserido 65", "Árvore:",
                "Caminho:", "Encontrado: sim",
                "Caminho:", "Encontrado: não",
                "Encerrado."));
        System.out.println("[Caso 2] " + (ok ? "OK" : "NOK"));
        return ok;
    }

    private static boolean caso3() {
        String in = String.join("\n", List.of("i 40", "i 20", "i 60", "i 20", "r 999", "r 40", "em", "sair")) + "\n";
        String out = runMain(in);
        boolean ok = true;
        ok &= mustContainInOrder(out, List.of(
                "OK: inserido 40", "Árvore:",
                "OK: inserido 20", "Árvore:",
                "OK: inserido 60", "Árvore:",
                "Ignorado: duplicado 20", "Árvore:",
                "Ignorado: inexistente 999", "Árvore:",
                "OK: removido 40", "Árvore:",
                "Em-Ordem: 20 60",
                "Encerrado."));
        System.out.println("[Caso 3] " + (ok ? "OK" : "NOK"));
        return ok;
    }

    // -------------------- EXECUÇÃO DO MAIN --------------------
    private static String runMain(String input) {
        try {
            Class<?> cls = Class.forName(PKG + "Main");
            Method main = cls.getDeclaredMethod("main", String[].class);

            PrintStream oldOut = System.out;
            InputStream oldIn = System.in;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);

            try {
                System.setOut(ps);
                System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
                main.invoke(null, (Object) new String[0]);
            } finally {
                System.setOut(oldOut);
                System.setIn(oldIn);
                ps.flush();
                ps.close();
            }

            // normaliza CRLF -> LF (compatível com todos os SOs)
            String result = baos.toString(StandardCharsets.UTF_8);
            return result.replace("\r\n", "\n");

        } catch (Throwable t) {
            return "EXCEPTION: " + t;
        }
    }

    // -------------------- AUXILIARES --------------------
    private static boolean hasTreeBlocks(String out, int min) {
        String[] lines = out.split("\\R");
        int count = 0;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().equals("Árvore:")) {
                for (int j = i + 1; j < Math.min(i + 4, lines.length); j++) {
                    if (lines[j].contains("└──") || lines[j].contains("├──")) {
                        count++;
                        break;
                    }
                }
            }
        }
        return count >= min;
    }

    private static boolean mustContainInOrder(String text, List<String> needles) {
        int idx = 0;
        for (String n : needles) {
            int found = text.indexOf(n, idx);
            if (found < 0)
                return false;
            idx = found + n.length();
        }
        return true;
    }

    private static boolean classeExiste(String nome) {
        try {
            Class.forName(PKG + nome);
            System.out.println("[OK] Classe " + nome);
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("[NOK] Classe " + nome);
            return false;
        }
    }

    private static boolean construtor(String classe, String[] params) {
        try {
            Class<?> c = Class.forName(PKG + classe);
            c.getDeclaredConstructor(resolveParams(params));
            System.out.println("[OK] Construtor " + classe + "(" + String.join(",", params) + ")");
            return true;
        } catch (Throwable t) {
            System.out.println("[NOK] Construtor " + classe + "(" + String.join(",", params) + ")");
            return false;
        }
    }

    private static boolean atributo(String classe, String nome, String tipo, int mod) {
        try {
            Class<?> c = Class.forName(PKG + classe);
            Field f = c.getDeclaredField(nome);
            boolean tipoOK = f.getType().equals(resolveTipo(tipo));
            boolean modOK = (mod == Modifier.PRIVATE && Modifier.isPrivate(f.getModifiers()))
                    || (mod == Modifier.PUBLIC && Modifier.isPublic(f.getModifiers()))
                    || (mod == Modifier.PROTECTED && Modifier.isProtected(f.getModifiers()));
            System.out.println((tipoOK && modOK ? "[OK] " : "[NOK] ") + "Atributo " + classe + "." + nome);
            return tipoOK && modOK;
        } catch (Throwable t) {
            System.out.println("[NOK] Atributo " + classe + "." + nome);
            return false;
        }
    }

    private static boolean metodo(String classe, String nome, String ret, String[] params, boolean pub, boolean stat) {
        try {
            Class<?> c = Class.forName(PKG + classe);
            Method m = c.getDeclaredMethod(nome, resolveParams(params));
            boolean retOK = m.getReturnType().equals(resolveTipo(ret));
            int mods = m.getModifiers();
            boolean pubOK = pub ? Modifier.isPublic(mods) : !Modifier.isPublic(mods);
            boolean statOK = stat ? Modifier.isStatic(mods) : true;
            System.out.println((retOK && pubOK && statOK ? "[OK] " : "[NOK] ") + "Método " + classe + "." + nome);
            return retOK && pubOK && statOK;
        } catch (Throwable t) {
            System.out.println("[NOK] Método " + classe + "." + nome);
            return false;
        }
    }

    private static Class<?> resolveTipo(String s) throws ClassNotFoundException {
        switch (s) {
            case "int":
                return int.class;
            case "void":
                return void.class;
            case "boolean":
                return boolean.class;
            case "String":
                return String.class;
            case "String[]":
                return String[].class;
            default:
                return Class.forName(PKG + s);
        }
    }

    private static Class<?>[] resolveParams(String[] ps) throws ClassNotFoundException {
        Class<?>[] a = new Class<?>[ps.length];
        for (int i = 0; i < ps.length; i++)
            a[i] = resolveTipo(ps[i]);
        return a;
    }

    private static String[] arr(String... a) {
        return a;
    }
}
