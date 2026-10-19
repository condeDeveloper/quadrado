package br.com.conde.quadrado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A definição e as simetrias.
 *
 * <p>{@code magico()} é o juiz de tudo neste repositório, então ele precisa reprovar o que
 * deve: quadrado com número repetido, com soma errada numa linha só, com diagonal errada.
 */
class QuadradoTest {

    private static final Quadrado LOSHU = new Quadrado(3, new int[] {2, 7, 6, 9, 5, 1, 4, 3, 8});

    @Test
    @DisplayName("a constante e n vezes (n ao quadrado mais 1) sobre 2")
    void constante() {
        assertEquals(1, Quadrado.constante(1));
        assertEquals(5, Quadrado.constante(2));
        assertEquals(15, Quadrado.constante(3));
        assertEquals(34, Quadrado.constante(4));
        assertEquals(65, Quadrado.constante(5));

        /* E ela sai da soma de 1 ate n ao quadrado, repartida pelas n linhas. */
        for (int ordem = 1; ordem <= 6; ordem++) {
            int total = ordem * ordem * (ordem * ordem + 1) / 2;

            assertEquals(total / ordem, Quadrado.constante(ordem), "ordem " + ordem);
        }
    }

    @Test
    @DisplayName("o quadrado de lo shu e magico")
    void loShu() {
        assertTrue(LOSHU.magico());
        assertTrue(LOSHU.completo());
        assertEquals(15, LOSHU.constante());
        assertEquals(5, LOSHU.em(1, 1));
    }

    @Test
    @DisplayName("grade de tamanho errado nao e quadrado")
    void tamanhoErrado() {
        assertThrows(IllegalArgumentException.class, () -> new Quadrado(3, new int[8]));
        assertThrows(IllegalArgumentException.class, () -> new Quadrado(0, new int[0]));
    }

    @Test
    @DisplayName("O JUIZ REPROVA: numero repetido, linha errada, diagonal errada")
    void juizReprova() {
        /* Numero repetido: o 5 duas vezes, sem o 9. Todas as somas continuam certas? Nao,
         * mas o completo() ja barra antes. */
        assertFalse(new Quadrado(3, new int[] {2, 7, 6, 5, 5, 1, 4, 3, 8}).completo());
        assertFalse(new Quadrado(3, new int[] {2, 7, 6, 5, 5, 1, 4, 3, 8}).magico());

        /* Duas celulas trocadas: a soma total continua 45, e duas linhas quebram. */
        assertFalse(new Quadrado(3, new int[] {2, 7, 6, 9, 5, 1, 4, 8, 3}).magico());

        /* Fora da faixa: um 10 onde nao existe 10. */
        assertFalse(new Quadrado(3, new int[] {2, 7, 6, 10, 5, 1, 4, 3, 8}).completo());

        /* As linhas e colunas certas, as diagonais nao. */
        Quadrado semDiagonal = new Quadrado(3, new int[] {2, 7, 6, 9, 5, 1, 4, 3, 8}).espelhado();

        assertTrue(semDiagonal.magico(), "o espelho ainda e magico");
    }

    @Test
    @DisplayName("girar quatro vezes volta ao mesmo, e espelhar duas tambem")
    void simetriasSaoInvolucoes() {
        assertEquals(LOSHU, LOSHU.girado().girado().girado().girado());
        assertEquals(LOSHU, LOSHU.espelhado().espelhado());
    }

    @Test
    @DisplayName("TODA FORMA de um quadrado magico continua magica")
    void formasContinuamMagicas() {
        for (Quadrado forma : LOSHU.todasAsFormas()) assertTrue(forma.magico(), forma.toString());

        assertEquals(8, LOSHU.todasAsFormas().size());

        Set<String> distintas = new HashSet<>();

        for (Quadrado forma : LOSHU.todasAsFormas()) distintas.add(forma.chave());

        assertEquals(8, distintas.size(), "o lo shu tem oito formas diferentes");
    }

    @Test
    @DisplayName("a canonica e a mesma para todas as oito formas")
    void canonicaEhCompartilhada() {
        String canonica = LOSHU.canonico().chave();

        for (Quadrado forma : LOSHU.todasAsFormas())
            assertEquals(canonica, forma.canonico().chave(), forma.toString());
    }

    @Test
    @DisplayName("o diabolico exige mais: o lo shu nao e")
    void loShuNaoEhDiabolico() {
        assertTrue(LOSHU.magico());
        assertFalse(LOSHU.diabolico());

        /* Um diabolico de ordem 4 conhecido, de nasik. */
        Quadrado nasik = new Quadrado(4, new int[] {1, 8, 13, 12, 14, 11, 2, 7, 4, 5, 16, 9,
            15, 10, 3, 6});

        assertTrue(nasik.magico());
        assertTrue(nasik.diabolico());
    }

    @Test
    @DisplayName("todo diabolico e magico, nos 7.040 de ordem 4")
    void diabolicoImplicaMagico() {
        for (Quadrado quadrado : Medidas.ordem4())
            if (quadrado.diabolico()) assertTrue(quadrado.magico(), quadrado.toString());
    }

    @Test
    @DisplayName("o metodo siames so vale para ordem impar")
    void siamesSoImpar() {
        assertThrows(IllegalArgumentException.class, () -> Varredura.siames(4));

        for (int ordem : List.of(3, 5, 7, 9, 11))
            assertTrue(Varredura.siames(ordem).magico(), "ordem " + ordem);
    }
}
