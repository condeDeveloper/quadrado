package br.com.conde.quadrado;

import java.util.Locale;

/** O MEDIDOR: roda as varreduras e imprime as tabelas do README. */
public final class Medidor {

    private Medidor() {}

    public static void main(String[] argumentos) {
        Locale.setDefault(Locale.US);

        String passo = argumentos.length > 0 ? argumentos[0] : "tudo";

        switch (passo) {
            case "correcao" -> correcao();
            case "ordem3" -> ordem3();
            case "ordem4" -> ordem4();
            case "simetrias" -> simetrias();
            case "diabolicos" -> diabolicos();
            default -> {
                correcao();
                ordem3();
                ordem4();
                simetrias();
                diabolicos();
            }
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println(texto);
        System.out.println("=".repeat(texto.length()));
        System.out.println();
    }

    static void correcao() {
        titulo("1. tudo o que a varredura achou passa pela definicao crua");

        System.out.println("ordem  achados  nao magicos  tentativas");

        for (Medidas.Correcao linha : Medidas.correcao())
            System.out.printf("%5d  %7d  %11d  %10d%n", linha.ordem(), linha.achados(),
                    linha.naoMagicos(), linha.tentativas());

        System.out.println();
        System.out.println("cortar cedo e onde mora o risco: um corte errado descarta quadrado");
        System.out.println("sem avisar. por isso todo achado e reconferido somando tudo de novo.");
    }

    static void ordem3() {
        titulo("2. os oito de ordem 3, que sao um so");

        Medidas.Tres tres = Medidas.tres();

        System.out.printf("arrumacoes que somam 15: %d%n", tres.brutos());
        System.out.printf("quadrados realmente distintos: %d%n", tres.distintos());
        System.out.println();
        System.out.println("o primeiro deles:");
        System.out.println();
        System.out.print(Medidas.ordem3().get(0));
        System.out.println();
        System.out.printf("valores que aparecem no CENTRO, nos oito:       %s%n", tres.centros());
        System.out.printf("valores que aparecem nos CANTOS:                %s%n", tres.cantos());
        System.out.printf("valores que aparecem nos MEIOS DE BORDA:        %s%n",
                tres.meiosDeBorda());

        System.out.println();
        System.out.println("nada disso foi programado: sobrou da varredura. o centro e 5 porque");
        System.out.println("nao ha quadrado magico de ordem 3 com outra coisa ali.");
    }

    static void ordem4() {
        titulo("3. quantos existem, por ordem");

        System.out.println("ordem  constante  brutos  distintos");

        for (Medidas.PorOrdem linha : Medidas.porOrdem())
            System.out.printf("%5d  %9d  %6d  %9d%n", linha.ordem(), linha.constante(),
                    linha.brutos(), linha.distintos());

        Medidas.Quatro quatro = Medidas.quatro();

        System.out.println();
        System.out.printf("ordem 4: %d brutos, %d distintos, razao %.1f%n", quatro.brutos(),
                quatro.distintos(), quatro.porDistinto());
        System.out.println();
        System.out.println("880 e o numero que frenicle de bessy publicou em 1693, contando a");
        System.out.println("mao. a ordem 2 nao tem nenhum, e isso tambem e resultado.");

        System.out.println();
        System.out.println("o metodo siames constroi um, e so um:");

        for (Medidas.Siames linha : Medidas.siames())
            System.out.printf("  ordem %d: magico %s, diabolico %s%s%n", linha.ordem(),
                    linha.magico() ? "sim" : "nao", linha.diabolico() ? "sim" : "nao",
                    linha.ordem() == 3 ? (linha.estaEntreOsTodos()
                            ? " (e um dos 8 varridos)" : " (NAO esta entre os varridos)") : "");

        System.out.println();
        System.out.println("construir e facil. construir nao responde quantos existem.");
    }

    static void simetrias() {
        titulo("4. girar e espelhar nao cria quadrado novo");

        Medidas.Simetrias s = Medidas.simetrias();

        System.out.printf("quadrados de ordem 4          %d%n", s.quadrados());
        System.out.printf("formas testadas (oito cada)   %d%n", s.formasTestadas());
        System.out.printf("formas que deixaram de ser magicas  %d%n", s.formasNaoMagicas());
        System.out.printf("quadrados com menos de 8 formas     %d%n", s.comMenosDeOitoFormas());
        System.out.printf("grupos de oito                %d%n", s.gruposDeOito());

        System.out.println();
        System.out.printf("%d dividido por 8 da %d, exato.%n", s.quadrados(), s.gruposDeOito());
        System.out.println("e nao e acidente: NENHUM dos 880 e igual a si mesmo girado, entao");
        System.out.println("todo grupo tem oito membros e a divisao nao tem resto.");
    }

    static void diabolicos() {
        titulo("5. os diabolicos, onde ate a diagonal quebrada soma");

        System.out.println("ordem  magicos distintos  diabolicos distintos  fracao  brutos");

        for (Medidas.Diabolicos linha : Medidas.diabolicos())
            System.out.printf("%5d  %17d  %20d  %5.1f%%  %6d%n", linha.ordem(),
                    linha.totalDistintos(), linha.distintos(), 100 * linha.fracao(),
                    linha.brutos());

        System.out.println();
        System.out.println("diagonal quebrada e a que sai por uma borda e volta pela outra.");
        System.out.println("de ordem 3 nao existe nenhum, e isso e contagem, nao falta de");
        System.out.println("procura: os oito foram testados um por um.");
    }
}
