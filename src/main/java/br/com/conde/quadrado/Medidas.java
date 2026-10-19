package br.com.conde.quadrado;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * AS MEDIDAS: tudo o que o repositório afirma sai de varrer todos os quadrados de uma ordem.
 *
 * <p>A varredura da ordem 4 é guardada, porque ela custa um segundo e é usada por quatro das
 * cinco medidas. Refazer quatro vezes seria o repositório inteiro levando cinco segundos para
 * dizer a mesma coisa.
 */
public final class Medidas {

    private Medidas() {}

    private static List<Quadrado> ordem4;

    /** Os 7.040 de ordem 4, varridos uma vez só. */
    public static List<Quadrado> ordem4() {
        if (ordem4 == null) ordem4 = List.copyOf(Varredura.todos(4));

        return ordem4;
    }

    private static List<Quadrado> ordem3;

    public static List<Quadrado> ordem3() {
        if (ordem3 == null) ordem3 = List.copyOf(Varredura.todos(3));

        return ordem3;
    }

    /* ===================== 1. correcao ===================== */

    public record Correcao(int ordem, int achados, int naoMagicos, long tentativas) {}

    private static List<Correcao> correcao;

    /**
     * TUDO O QUE A VARREDURA ACHOU PASSA PELA DEFINIÇÃO CRUA.
     *
     * <p>A varredura corta cedo, e cortar cedo é onde mora o risco: um corte errado descarta
     * quadrado mágico sem avisar. Então cada achado é reconferido pela definição inteira,
     * escrita do jeito mais burro possível, somando linha por linha sem reaproveitar nada.
     *
     * <p>Isso prova que nada FALSO entrou. Que nada VERDADEIRO ficou de fora é outra pergunta,
     * e ela é respondida pela contagem: 880 é o número que Frénicle de Bessy publicou em 1693,
     * contando à mão.
     */
    public static List<Correcao> correcao() {
        if (correcao != null) return correcao;

        List<Correcao> saida = new ArrayList<>();

        for (int ordem = 1; ordem <= 4; ordem++) {
            List<Quadrado> achados = ordem == 3 ? ordem3() : ordem == 4 ? ordem4()
                    : Varredura.todos(ordem);

            long tentativas = Varredura.tentativas;

            int naoMagicos = 0;

            for (Quadrado quadrado : achados)
                if (!quadrado.magico() || !quadrado.completo()) naoMagicos++;

            saida.add(new Correcao(ordem, achados.size(), naoMagicos, tentativas));
        }

        return correcao = List.copyOf(saida);
    }

    public static Correcao correcao(int ordem) {
        return correcao().stream().filter(x -> x.ordem() == ordem).findFirst().orElseThrow();
    }

    /* ===================== 2. ordem3 ===================== */

    public record Tres(int brutos, int distintos, Set<Integer> centros, Set<Integer> cantos,
            Set<Integer> meiosDeBorda) {}

    private static Tres tres;

    /**
     * OS OITO DE ORDEM 3, QUE SÃO UM SÓ.
     *
     * <p>Existem oito arrumações de 1 a 9 que somam 15 em toda linha, coluna e diagonal, e as
     * oito são a mesma figura girada e espelhada. Não existe "outro" quadrado mágico de ordem
     * 3, e isso não é uma dificuldade de achar: é uma contagem fechada.
     *
     * <p>As três últimas colunas mostram o que a varredura revela sem precisar de álgebra: o
     * centro é sempre 5, os cantos são sempre pares, os meios de borda sempre ímpares. Nenhuma
     * dessas três coisas foi programada; elas sobraram.
     */
    public static Tres tres() {
        if (tres != null) return tres;

        Set<Integer> centros = new TreeSet<>();
        Set<Integer> cantos = new TreeSet<>();
        Set<Integer> meios = new TreeSet<>();
        Set<String> canonicos = new HashSet<>();

        for (Quadrado quadrado : ordem3()) {
            centros.add(quadrado.em(1, 1));

            cantos.add(quadrado.em(0, 0));
            cantos.add(quadrado.em(0, 2));
            cantos.add(quadrado.em(2, 0));
            cantos.add(quadrado.em(2, 2));

            meios.add(quadrado.em(0, 1));
            meios.add(quadrado.em(1, 0));
            meios.add(quadrado.em(1, 2));
            meios.add(quadrado.em(2, 1));

            canonicos.add(quadrado.canonico().chave());
        }

        return tres = new Tres(ordem3().size(), canonicos.size(), centros, cantos, meios);
    }

    /* ===================== 3. ordem4 ===================== */

    public record Quatro(int brutos, int distintos, int constante) {

        public double porDistinto() {
            return brutos / (double) distintos;
        }
    }

    private static Quatro quatro;

    /** OS 880 DE FRÉNICLE, contados de novo trezentos e poucos anos depois. */
    public static Quatro quatro() {
        if (quatro != null) return quatro;

        Set<String> canonicos = new HashSet<>();

        for (Quadrado quadrado : ordem4()) canonicos.add(quadrado.canonico().chave());

        return quatro = new Quatro(ordem4().size(), canonicos.size(), Quadrado.constante(4));
    }

    /** Quantos quadrados mágicos existem por ordem, incluindo o zero que surpreende. */
    public record PorOrdem(int ordem, int constante, int brutos, int distintos) {}

    public static List<PorOrdem> porOrdem() {
        List<PorOrdem> saida = new ArrayList<>();

        for (int ordem = 1; ordem <= 4; ordem++) {
            List<Quadrado> achados = ordem == 3 ? ordem3() : ordem == 4 ? ordem4()
                    : Varredura.todos(ordem);

            Set<String> canonicos = new HashSet<>();

            for (Quadrado quadrado : achados) canonicos.add(quadrado.canonico().chave());

            saida.add(new PorOrdem(ordem, Quadrado.constante(ordem), achados.size(),
                    canonicos.size()));
        }

        return saida;
    }

    /* ===================== 4. simetrias ===================== */

    public record Simetrias(int quadrados, int formasTestadas, int formasNaoMagicas,
            int comMenosDeOitoFormas, int gruposDeOito) {}

    private static Simetrias simetrias;

    /**
     * GIRAR E ESPELHAR NÃO CRIA QUADRADO NOVO.
     *
     * <p>Duas conferências: toda forma de um quadrado mágico continua mágica, e nenhum dos 880
     * tem menos de oito formas distintas. A segunda é a que faz a divisão por oito ser exata:
     * se algum quadrado fosse igual a si mesmo girado, ele contaria menos de oito vezes no
     * total bruto, e 7.040 dividido por 8 não daria um número redondo por acidente.
     */
    public static Simetrias simetrias() {
        if (simetrias != null) return simetrias;

        int formas = 0;
        int naoMagicas = 0;
        int comMenos = 0;

        for (Quadrado quadrado : ordem4()) {
            Set<String> distintas = new HashSet<>();

            for (Quadrado forma : quadrado.todasAsFormas()) {
                formas++;

                if (!forma.magico()) naoMagicas++;

                distintas.add(forma.chave());
            }

            if (distintas.size() < 8) comMenos++;
        }

        Set<String> canonicos = new HashSet<>();

        for (Quadrado quadrado : ordem4()) canonicos.add(quadrado.canonico().chave());

        return simetrias = new Simetrias(ordem4().size(), formas, naoMagicas, comMenos,
                canonicos.size());
    }

    /* ===================== 5. diabolicos ===================== */

    public record Diabolicos(int ordem, int brutos, int distintos, int totalBrutos,
            int totalDistintos) {

        public double fracao() {
            return totalDistintos == 0 ? 0 : distintos / (double) totalDistintos;
        }
    }

    private static List<Diabolicos> diabolicos;

    /**
     * OS DIABÓLICOS, em que até as diagonais quebradas somam a constante.
     *
     * <p>Diagonal quebrada é a que sai por uma borda e volta pela outra. Exigir isso é bem mais
     * forte que ser mágico, e a conta mostra o quanto: dos 880, só 48 aguentam. De ordem 3,
     * nenhum, e isso também é resultado e não falta de procura.
     */
    public static List<Diabolicos> diabolicos() {
        if (diabolicos != null) return diabolicos;

        List<Diabolicos> saida = new ArrayList<>();

        for (int ordem = 3; ordem <= 4; ordem++) {
            List<Quadrado> todos = ordem == 3 ? ordem3() : ordem4();

            int brutos = 0;

            Set<String> canonicos = new HashSet<>();
            Set<String> todosCanonicos = new HashSet<>();

            for (Quadrado quadrado : todos) {
                todosCanonicos.add(quadrado.canonico().chave());

                if (!quadrado.diabolico()) continue;

                brutos++;

                canonicos.add(quadrado.canonico().chave());
            }

            saida.add(new Diabolicos(ordem, brutos, canonicos.size(), todos.size(),
                    todosCanonicos.size()));
        }

        return diabolicos = List.copyOf(saida);
    }

    public static Diabolicos diabolicos(int ordem) {
        return diabolicos().stream().filter(x -> x.ordem() == ordem).findFirst().orElseThrow();
    }

    /** O método siamês constrói um, e um só. */
    public record Siames(int ordem, boolean magico, boolean diabolico, boolean estaEntreOsTodos) {}

    public static List<Siames> siames() {
        List<Siames> saida = new ArrayList<>();

        for (int ordem : new int[] {3, 5, 7, 9}) {
            Quadrado construido = Varredura.siames(ordem);

            boolean estaLa = ordem == 3 && ordem3().contains(construido);

            saida.add(new Siames(ordem, construido.magico(), construido.diabolico(), estaLa));
        }

        return saida;
    }
}
