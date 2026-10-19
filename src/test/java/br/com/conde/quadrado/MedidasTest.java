package br.com.conde.quadrado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Os números que aparecem escritos no README, travados em teste. */
class MedidasTest {

    /* ===================== 1. correcao ===================== */

    @Test
    @DisplayName("nenhum achado da varredura falha na definicao crua")
    void correcao() {
        for (Medidas.Correcao linha : Medidas.correcao())
            assertEquals(0, linha.naoMagicos(), "ordem " + linha.ordem());

        assertEquals(1, Medidas.correcao(1).achados());
        assertEquals(0, Medidas.correcao(2).achados());
        assertEquals(8, Medidas.correcao(3).achados());
        assertEquals(7040, Medidas.correcao(4).achados());
    }

    /* ===================== 2. ordem 3 ===================== */

    @Test
    @DisplayName("OS OITO DE ORDEM 3 SAO UM SO quadrado, girado e espelhado")
    void oitoSaoUm() {
        Medidas.Tres tres = Medidas.tres();

        assertEquals(8, tres.brutos());
        assertEquals(1, tres.distintos());
    }

    @Test
    @DisplayName("o centro e sempre 5, os cantos sempre pares, as bordas sempre impares")
    void oQueSobrouDaVarredura() {
        Medidas.Tres tres = Medidas.tres();

        assertEquals(Set.of(5), tres.centros());
        assertEquals(Set.of(2, 4, 6, 8), tres.cantos());
        assertEquals(Set.of(1, 3, 7, 9), tres.meiosDeBorda());

        /* Nada disso foi programado: a varredura so testou a definicao. */
        for (Quadrado quadrado : Medidas.ordem3()) assertEquals(5, quadrado.em(1, 1));
    }

    /* ===================== 3. ordem 4 ===================== */

    @Test
    @DisplayName("OS 880 DE FRENICLE, publicados em 1693 e contados de novo aqui")
    void osOitocentosEOitenta() {
        Medidas.Quatro quatro = Medidas.quatro();

        assertEquals(7040, quatro.brutos());
        assertEquals(880, quatro.distintos());
        assertEquals(34, quatro.constante());

        assertEquals(8.0, quatro.porDistinto(), 1e-12);
    }

    @Test
    @DisplayName("a ordem 2 nao tem nenhum, e isso tambem e resultado")
    void ordemDois() {
        Medidas.PorOrdem dois = Medidas.porOrdem().stream()
                .filter(x -> x.ordem() == 2).findFirst().orElseThrow();

        assertEquals(0, dois.brutos());
        assertEquals(0, dois.distintos());
        assertEquals(5, dois.constante());
    }

    @Test
    @DisplayName("o siames constroi um magico em toda ordem impar, e nunca um diabolico")
    void siames() {
        for (Medidas.Siames linha : Medidas.siames()) {
            assertTrue(linha.magico(), "ordem " + linha.ordem());
            assertTrue(!linha.diabolico(), "ordem " + linha.ordem());
        }

        /* E o de ordem 3 e um dos oito: construir da um, varrer da todos. */
        Medidas.Siames tres = Medidas.siames().get(0);

        assertEquals(3, tres.ordem());
        assertTrue(tres.estaEntreOsTodos());
    }

    /* ===================== 4. simetrias ===================== */

    @Test
    @DisplayName("as 56.320 formas continuam magicas, sem uma excecao")
    void formasContinuamMagicas() {
        Medidas.Simetrias s = Medidas.simetrias();

        assertEquals(7040, s.quadrados());
        assertEquals(56_320, s.formasTestadas());
        assertEquals(0, s.formasNaoMagicas());
    }

    @Test
    @DisplayName("a divisao por 8 e exata porque NENHUM e igual a si mesmo girado")
    void divisaoExata() {
        Medidas.Simetrias s = Medidas.simetrias();

        assertEquals(0, s.comMenosDeOitoFormas());
        assertEquals(880, s.gruposDeOito());

        assertEquals(s.quadrados(), s.gruposDeOito() * 8);
    }

    /* ===================== 5. diabolicos ===================== */

    @Test
    @DisplayName("dos 880, so 48 sao diabolicos")
    void quarentaEOito() {
        Medidas.Diabolicos quatro = Medidas.diabolicos(4);

        assertEquals(880, quatro.totalDistintos());
        assertEquals(48, quatro.distintos());
        assertEquals(384, quatro.brutos());

        assertEquals(8, quatro.brutos() / quatro.distintos());

        assertTrue(quatro.fracao() < 0.06);
    }

    @Test
    @DisplayName("e de ordem 3 nao existe nenhum, com os oito testados um por um")
    void nenhumDeOrdemTres() {
        Medidas.Diabolicos tres = Medidas.diabolicos(3);

        assertEquals(1, tres.totalDistintos());
        assertEquals(0, tres.distintos());
        assertEquals(0, tres.brutos());
    }
}
