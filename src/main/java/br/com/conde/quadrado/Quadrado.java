package br.com.conde.quadrado;

/**
 * UM QUADRADO: os números de 1 a n² arrumados numa grade.
 *
 * <p>Ele é mágico quando toda linha, toda coluna e as duas diagonais somam o mesmo. Esse
 * "mesmo" não é escolha: a soma de 1 até n² é {@code n²(n²+1)/2}, e ela se reparte igualmente
 * entre as n linhas, então a constante é obrigatoriamente {@code n(n²+1)/2}. Não existe
 * quadrado mágico com outra soma, e a terceira medida confere isso contando.
 *
 * <p>Esta classe não sabe construir quadrado mágico. Ela só sabe dizer se um é, e aplicar as
 * oito simetrias da grade. Quem constrói é a {@link Varredura}, e quem decide se o resultado
 * presta é o {@link #magico()} daqui, que é a definição e nada mais.
 */
public record Quadrado(int ordem, int[] celulas) {

    public Quadrado {
        if (ordem < 1) throw new IllegalArgumentException("ordem " + ordem);

        if (celulas.length != ordem * ordem)
            throw new IllegalArgumentException("esperava " + ordem * ordem + " celulas, veio "
                    + celulas.length);

        celulas = celulas.clone();
    }

    public int em(int linha, int coluna) {
        return celulas[linha * ordem + coluna];
    }

    /** A soma obrigatória de cada linha: n vezes (n² mais 1), dividido por 2. */
    public static int constante(int ordem) {
        return ordem * (ordem * ordem + 1) / 2;
    }

    public int constante() {
        return constante(ordem);
    }

    /** Os números são exatamente 1 até n², cada um uma vez? */
    public boolean completo() {
        boolean[] visto = new boolean[ordem * ordem + 1];

        for (int valor : celulas) {
            if (valor < 1 || valor > ordem * ordem) return false;

            if (visto[valor]) return false;

            visto[valor] = true;
        }

        return true;
    }

    /**
     * A DEFINIÇÃO, e o juiz de tudo neste repositório.
     *
     * <p>Escrita do jeito mais burro possível de propósito: soma linha por linha, coluna por
     * coluna, e as duas diagonais, e compara com a constante. Sem atalho, sem fórmula, sem
     * reaproveitar soma já calculada. Um juiz esperto pode errar junto com o construtor.
     */
    public boolean magico() {
        if (!completo()) return false;

        int alvo = constante();

        for (int linha = 0; linha < ordem; linha++) {
            int soma = 0;

            for (int coluna = 0; coluna < ordem; coluna++) soma += em(linha, coluna);

            if (soma != alvo) return false;
        }

        for (int coluna = 0; coluna < ordem; coluna++) {
            int soma = 0;

            for (int linha = 0; linha < ordem; linha++) soma += em(linha, coluna);

            if (soma != alvo) return false;
        }

        int principal = 0;
        int secundaria = 0;

        for (int passo = 0; passo < ordem; passo++) {
            principal += em(passo, passo);
            secundaria += em(passo, ordem - 1 - passo);
        }

        return principal == alvo && secundaria == alvo;
    }

    /**
     * DIABÓLICO, também chamado pandiagonal: além das duas diagonais, todas as diagonais
     * quebradas também somam a constante.
     *
     * <p>Diagonal quebrada é a que sai por um lado e volta pelo outro, como se a grade fosse
     * um cilindro nos dois sentidos. É uma condição bem mais forte, e a quinta medida conta
     * quantos dos 880 a cumprem.
     */
    public boolean diabolico() {
        if (!magico()) return false;

        int alvo = constante();

        for (int deslocamento = 0; deslocamento < ordem; deslocamento++) {
            int descendo = 0;
            int subindo = 0;

            for (int passo = 0; passo < ordem; passo++) {
                descendo += em(passo, (passo + deslocamento) % ordem);
                subindo += em(passo, (deslocamento - passo % ordem + ordem * 2) % ordem);
            }

            if (descendo != alvo || subindo != alvo) return false;
        }

        return true;
    }

    /* ===================== as oito simetrias ===================== */

    public Quadrado girado() {
        int[] novo = new int[celulas.length];

        for (int linha = 0; linha < ordem; linha++)
            for (int coluna = 0; coluna < ordem; coluna++)
                novo[coluna * ordem + (ordem - 1 - linha)] = em(linha, coluna);

        return new Quadrado(ordem, novo);
    }

    public Quadrado espelhado() {
        int[] novo = new int[celulas.length];

        for (int linha = 0; linha < ordem; linha++)
            for (int coluna = 0; coluna < ordem; coluna++)
                novo[linha * ordem + (ordem - 1 - coluna)] = em(linha, coluna);

        return new Quadrado(ordem, novo);
    }

    /**
     * AS OITO FORMAS do mesmo quadrado: quatro giros, e os mesmos quatro espelhados.
     *
     * <p>Isso é o grupo de simetrias do quadrado, o diedral de ordem 8. Girar e espelhar não
     * produz um quadrado mágico novo, produz o mesmo de outro ângulo, e é essa distinção que
     * separa os 7.040 dos 880.
     */
    public java.util.List<Quadrado> todasAsFormas() {
        java.util.List<Quadrado> formas = new java.util.ArrayList<>();

        Quadrado atual = this;

        for (int giro = 0; giro < 4; giro++) {
            formas.add(atual);
            formas.add(atual.espelhado());

            atual = atual.girado();
        }

        return formas;
    }

    /**
     * A forma canônica: entre as oito, a menor na ordem de leitura.
     *
     * <p>Serve para contar quadrados distintos sem contar o mesmo oito vezes. Dois quadrados
     * são "o mesmo" exatamente quando têm a mesma canônica.
     */
    public Quadrado canonico() {
        Quadrado menor = null;

        for (Quadrado forma : todasAsFormas())
            if (menor == null || menorQue(forma, menor)) menor = forma;

        return menor;
    }

    private static boolean menorQue(Quadrado a, Quadrado b) {
        for (int posicao = 0; posicao < a.celulas().length; posicao++) {
            if (a.celulas()[posicao] < b.celulas()[posicao]) return true;
            if (a.celulas()[posicao] > b.celulas()[posicao]) return false;
        }

        return false;
    }

    public String chave() {
        StringBuilder saida = new StringBuilder();

        for (int valor : celulas) saida.append(valor).append(',');

        return saida.toString();
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Quadrado q && q.ordem() == ordem
                && java.util.Arrays.equals(q.celulas(), celulas);
    }

    @Override
    public int hashCode() {
        return 31 * ordem + java.util.Arrays.hashCode(celulas);
    }

    @Override
    public String toString() {
        StringBuilder saida = new StringBuilder();

        int largura = String.valueOf(ordem * ordem).length();

        for (int linha = 0; linha < ordem; linha++) {
            for (int coluna = 0; coluna < ordem; coluna++)
                saida.append(String.format("%" + largura + "d ", em(linha, coluna)));

            saida.append('\n');
        }

        return saida.toString();
    }
}
