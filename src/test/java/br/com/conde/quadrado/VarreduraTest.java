package br.com.conde.quadrado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A varredura corta cedo, e todo corte é um risco de perder resposta sem avisar.
 *
 * <p>O teste que importa aqui é o primeiro: gerar TODAS as 362.880 arrumações de 1 a 9, sem
 * corte nenhum, e conferir que elas dão exatamente os mesmos oito quadrados. Isso não prova
 * que o corte está certo para a ordem 4, mas prova que ele está certo onde dá para conferir,
 * e é a única maneira honesta de confiar nele onde não dá.
 */
class VarreduraTest {

    @Test
    @DisplayName("O CORTE NAO PERDE NADA: todas as 362.880 arrumacoes dao os mesmos 8")
    void corteNaoPerdeNada() {
        List<Quadrado> semCorte = new ArrayList<>();

        int[] grade = new int[9];
        boolean[] usado = new boolean[10];

        forcaBruta(grade, usado, 0, semCorte);

        List<Quadrado> comCorte = Varredura.todos(3);

        assertEquals(8, semCorte.size());
        assertEquals(semCorte.size(), comCorte.size());

        Set<String> deUm = new HashSet<>();
        Set<String> doOutro = new HashSet<>();

        for (Quadrado quadrado : semCorte) deUm.add(quadrado.chave());
        for (Quadrado quadrado : comCorte) doOutro.add(quadrado.chave());

        assertEquals(deUm, doOutro);
    }

    /** Todas as permutacoes de 1 a 9, sem corte nenhum: 362.880 arrumacoes. */
    private void forcaBruta(int[] grade, boolean[] usado, int posicao, List<Quadrado> achados) {
        if (posicao == 9) {
            Quadrado candidato = new Quadrado(3, grade);

            if (candidato.magico()) achados.add(candidato);

            return;
        }

        for (int valor = 1; valor <= 9; valor++) {
            if (usado[valor]) continue;

            grade[posicao] = valor;
            usado[valor] = true;

            forcaBruta(grade, usado, posicao + 1, achados);

            usado[valor] = false;
        }
    }

    @Test
    @DisplayName("e o corte vale a pena: 1.697 tentativas contra 986.409 sem ele")
    void corteValeAPena() {
        Varredura.todos(3);

        long comCorte = Varredura.tentativas;

        /* Sem corte, cada uma das 362.880 arrumacoes custa as nove posicoes preenchidas,
         * mais as tentativas descartadas pelo "ja usei este numero". */
        long semCorte = 0;

        for (int posicao = 0; posicao < 9; posicao++) {
            long arranjos = 1;

            for (int passo = 0; passo < posicao; passo++) arranjos *= 9 - passo;

            semCorte += arranjos * (9 - posicao);
        }

        assertEquals(1697, comCorte);
        assertTrue(semCorte > 500 * comCorte, semCorte + " contra " + comCorte);
    }

    @Test
    @DisplayName("nao existe quadrado magico de ordem 2, e isso e resultado")
    void ordemDoisNaoExiste() {
        assertEquals(0, Varredura.todos(2).size());

        /* Com quatro numeros, a soma de cada linha teria de ser 5, e o 4 so faz 5 com o 1;
         * entao 4 e 1 ficam na mesma linha e na mesma coluna, o que e impossivel. */
        assertEquals(5, Quadrado.constante(2));
    }

    @Test
    @DisplayName("ordem 1 tem exatamente um, que e o numero 1 sozinho")
    void ordemUm() {
        List<Quadrado> todos = Varredura.todos(1);

        assertEquals(1, todos.size());
        assertEquals(1, todos.get(0).em(0, 0));
        assertTrue(todos.get(0).magico());
    }

    @Test
    @DisplayName("todo achado da varredura passa pela definicao inteira")
    void todosSaoMagicos() {
        for (int ordem = 1; ordem <= 3; ordem++)
            for (Quadrado quadrado : Varredura.todos(ordem)) {
                assertTrue(quadrado.magico(), quadrado.toString());
                assertTrue(quadrado.completo(), quadrado.toString());
            }

        for (Quadrado quadrado : Medidas.ordem4()) assertTrue(quadrado.magico());
    }

    @Test
    @DisplayName("o siames de ordem 3 esta entre os oito que a varredura achou")
    void siamesEstaLa() {
        assertTrue(Varredura.todos(3).contains(Varredura.siames(3)));
    }
}
