package br.com.conde.quadrado;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A VARREDURA: todos os quadrados mágicos de uma ordem, achados por tentativa e corte.
 *
 * <p>Testar todas as arrumações é impossível de propósito: de ordem 4 são 16 fatoriais, uns 20
 * trilhões. O que torna a conta possível é cortar cedo, e o corte não é esperteza, é a própria
 * definição aplicada antes da hora:
 *
 * <ul>
 *   <li>linha cheia que não soma a constante: nenhum preenchimento adiante conserta;
 *   <li>coluna cheia idem;
 *   <li>diagonal cheia idem;
 *   <li>linha quase cheia: o que falta já está determinado, e se não for um número livre entre
 *       1 e n², acabou.
 * </ul>
 *
 * <p>Nenhum desses cortes descarta um quadrado mágico, porque cada um é a negação direta da
 * definição. É isso que mantém a varredura COMPLETA: ela é rápida sem deixar de ser exaustiva,
 * e a primeira medida confere o resultado contra a definição crua, sem corte nenhum.
 */
public final class Varredura {

    private Varredura() {}

    /** Quantas posições a varredura preencheu, para a medida saber o trabalho real. */
    public static long tentativas;

    /** Todos os quadrados mágicos desta ordem, contando giros e espelhos como distintos. */
    public static List<Quadrado> todos(int ordem) {
        List<Quadrado> achados = new ArrayList<>();

        cada(ordem, achados::add);

        return achados;
    }

    /** O mesmo, sem guardar: para contar ordens grandes sem encher a memória. */
    public static void cada(int ordem, Consumer<Quadrado> aoAchar) {
        tentativas = 0;

        int[] grade = new int[ordem * ordem];
        boolean[] usado = new boolean[ordem * ordem + 1];

        preencher(ordem, grade, usado, 0, aoAchar);
    }

    private static void preencher(int ordem, int[] grade, boolean[] usado, int posicao,
            Consumer<Quadrado> aoAchar) {
        if (posicao == grade.length) {
            Quadrado candidato = new Quadrado(ordem, grade);

            /* Mesmo com todos os cortes, a decisao final passa pela definicao inteira. */
            if (candidato.magico()) aoAchar.accept(candidato);

            return;
        }

        int linha = posicao / ordem;
        int coluna = posicao % ordem;

        for (int valor = 1; valor <= ordem * ordem; valor++) {
            if (usado[valor]) continue;

            tentativas++;

            grade[posicao] = valor;
            usado[valor] = true;

            if (podeContinuar(ordem, grade, usado, linha, coluna))
                preencher(ordem, grade, usado, posicao + 1, aoAchar);

            usado[valor] = false;
            grade[posicao] = 0;
        }
    }

    /**
     * Os cortes, todos consequência direta da definição.
     *
     * <p>O corte que paga a conta é o do PENÚLTIMO: quando falta uma só casa numa linha, numa
     * coluna ou numa diagonal, o valor dali já está determinado por subtração. Se o número que
     * falta estiver fora de 1 a n², ou já tiver sido usado, não há o que tentar. Isso troca
     * dezesseis tentativas por uma conta, em toda linha e em toda coluna.
     */
    private static boolean podeContinuar(int ordem, int[] grade, boolean[] usado, int linha,
            int coluna) {
        int alvo = Quadrado.constante(ordem);

        int somaLinha = 0;

        for (int c = 0; c <= coluna; c++) somaLinha += grade[linha * ordem + c];

        if (coluna == ordem - 1) {
            if (somaLinha != alvo) return false;
        } else if (coluna == ordem - 2) {
            if (!faltaCabe(ordem, usado, alvo - somaLinha)) return false;
        } else if (somaLinha > alvo - (ordem - 1 - coluna)) {
            return false;
        }

        /* A coluna só fica completa na última linha, e a casa anterior dela está (ordem - 1)
         * posições atrás, ou seja, já preenchida: dá para fazer a mesma conta. */
        int somaColuna = 0;

        for (int l = 0; l <= linha; l++) somaColuna += grade[l * ordem + coluna];

        if (linha == ordem - 1) {
            if (somaColuna != alvo) return false;
        } else if (linha == ordem - 2) {
            if (!faltaCabe(ordem, usado, alvo - somaColuna)) return false;
        } else if (somaColuna > alvo - (ordem - 1 - linha)) {
            return false;
        }

        if (linha == coluna) {
            int soma = 0;

            for (int passo = 0; passo <= linha; passo++) soma += grade[passo * ordem + passo];

            if (linha == ordem - 1) {
                if (soma != alvo) return false;
            } else if (linha == ordem - 2) {
                if (!faltaCabe(ordem, usado, alvo - soma)) return false;
            } else if (soma > alvo - (ordem - 1 - linha)) {
                return false;
            }
        }

        if (linha + coluna == ordem - 1) {
            int soma = 0;

            for (int passo = 0; passo <= linha; passo++)
                soma += grade[passo * ordem + (ordem - 1 - passo)];

            if (linha == ordem - 1) {
                if (soma != alvo) return false;
            } else if (linha == ordem - 2) {
                if (!faltaCabe(ordem, usado, alvo - soma)) return false;
            } else if (soma > alvo - (ordem - 1 - linha)) {
                return false;
            }
        }

        return true;
    }

    /** O número que falta para fechar existe e ainda está livre? */
    private static boolean faltaCabe(int ordem, boolean[] usado, int falta) {
        return falta >= 1 && falta <= ordem * ordem && !usado[falta];
    }

    /** Quantos quadrados distintos existem, juntando os que são giro ou espelho um do outro. */
    public record Contagem(int ordem, int brutos, int distintos, long tentativas) {}

    public static Contagem contar(int ordem) {
        List<Quadrado> todos = todos(ordem);

        java.util.Set<String> canonicos = new java.util.HashSet<>();

        for (Quadrado quadrado : todos) canonicos.add(quadrado.canonico().chave());

        return new Contagem(ordem, todos.size(), canonicos.size(), tentativas);
    }

    /**
     * O MÉTODO SIAMÊS, que constrói UM quadrado mágico de ordem ímpar.
     *
     * <p>Começa com o 1 no meio da primeira linha e anda na diagonal para cima e para a
     * direita, dando a volta pelas bordas; se a casa está ocupada, desce uma.
     *
     * <p>Ele serve a uma medida específica: construir é fácil, e construir não responde quantos
     * existem. Este método devolve um, sempre o mesmo, e para ordem par não devolve nada.
     */
    public static Quadrado siames(int ordem) {
        if (ordem % 2 == 0) throw new IllegalArgumentException("so vale para ordem impar");

        int[] grade = new int[ordem * ordem];

        int linha = 0;
        int coluna = ordem / 2;

        for (int valor = 1; valor <= ordem * ordem; valor++) {
            grade[linha * ordem + coluna] = valor;

            int proximaLinha = (linha - 1 + ordem) % ordem;
            int proximaColuna = (coluna + 1) % ordem;

            if (grade[proximaLinha * ordem + proximaColuna] != 0) linha = (linha + 1) % ordem;
            else {
                linha = proximaLinha;
                coluna = proximaColuna;
            }
        }

        return new Quadrado(ordem, grade);
    }
}
