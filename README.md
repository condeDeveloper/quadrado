# Quadrados mágicos

Quadrados mágicos do zero em Java 21, contados por varredura exaustiva. Os 8 de ordem 3, os 7.040 de ordem 4, e o zero de ordem 2.

Em 1693 Frénicle de Bessy publicou que existem **880** quadrados mágicos de ordem 4. Ele contou à mão. Este repositório conta de novo, e a parte interessante não é o 880 confirmar: é o que aparece quando você tem todos eles na memória e pode perguntar qualquer coisa.

```
mvn -q exec:java -Dexec.args=ordem4
```

## O que a varredura precisa cortar

Testar todas as arrumações de ordem 4 é impossível de propósito: são 16 fatoriais, uns 20 trilhões. O que torna a conta viável é cortar cedo, e cada corte aqui é a definição aplicada antes da hora, nunca uma esperteza.

O corte que paga a conta é o do **penúltimo**: quando falta uma só casa numa linha, numa coluna ou numa diagonal, o valor dali já está determinado por subtração. Se o número que falta estiver fora de 1 a n², ou já tiver sido usado, não há o que tentar.

Ele trocou 3.125.959.840 tentativas por **52.511.986**, e 37 segundos por 1,2. Com o mesmo resultado, célula por célula.

## As medidas

### 1. Tudo o que a varredura achou passa pela definição crua

| ordem | achados | não mágicos | tentativas |
|---:|---:|---:|---:|
| 1 | 1 | 0 | 1 |
| 2 | 0 | 0 | 16 |
| 3 | 8 | 0 | 1.697 |
| 4 | 7.040 | 0 | 52.511.986 |

Cortar cedo é onde mora o risco: um corte errado descarta quadrado mágico sem avisar, e o programa termina feliz com a resposta errada.

Por isso cada achado é reconferido pela definição inteira, escrita do jeito mais burro possível, somando linha por linha sem reaproveitar nada. Isso prova que nada falso entrou.

Que nada verdadeiro ficou de fora é outra pergunta, e ela tem um teste próprio: gerar **todas as 362.880** arrumações de 1 a 9, sem corte nenhum, e conferir que dão exatamente os mesmos oito quadrados. Onde dá para conferir, confere. Onde não dá, o 880 de 1693 é a segunda testemunha.

### 2. Os oito de ordem 3, que são um só

```
2 7 6
9 5 1
4 3 8
```

Existem oito arrumações de 1 a 9 que somam 15 em toda linha, coluna e diagonal. As oito são **a mesma figura**, girada e espelhada. Não existe outro quadrado mágico de ordem 3, e isso não é dificuldade de achar: é contagem fechada.

E com os oito na mão, três coisas aparecem sem precisar de álgebra nenhuma:

- o valor do **centro**, nos oito: sempre 5
- os valores dos **cantos**: sempre 2, 4, 6 e 8
- os valores dos **meios de borda**: sempre 1, 3, 7 e 9

Nada disso foi programado. A varredura só testou a definição, e isso sobrou.

### 3. Quantos existem, por ordem

| ordem | constante | brutos | distintos |
|---:|---:|---:|---:|
| 1 | 1 | 1 | 1 |
| 2 | 5 | **0** | 0 |
| 3 | 15 | 8 | 1 |
| 4 | 34 | 7.040 | 880 |

A constante não é escolha: a soma de 1 até n² se reparte igualmente entre as n linhas, então ela é obrigatoriamente `n(n²+1)/2`.

A linha da ordem 2 é resultado, não lacuna. Com quatro números a soma de cada linha teria de ser 5, e o 4 só faz 5 com o 1, então 4 e 1 teriam de estar na mesma linha e na mesma coluna ao mesmo tempo.

O método siamês, de 1691, constrói um quadrado mágico de qualquer ordem ímpar, e o teste confirma que ele funciona em 3, 5, 7, 9 e 11. Mas ele devolve **um**, sempre o mesmo, e nunca um diabólico. Construir é fácil; construir não responde quantos existem.

### 4. Girar e espelhar não cria quadrado novo

```
quadrados de ordem 4                 7.040
formas testadas (oito cada)         56.320
formas que deixaram de ser mágicas       0
quadrados com menos de 8 formas          0
grupos de oito                         880
```

7.040 dividido por 8 dá 880, exato, e isso não é acidente de arredondamento. Se algum quadrado fosse igual a si mesmo girado ou espelhado, ele teria menos de oito formas, o grupo dele contaria menos, e a divisão teria resto.

Nenhum dos 880 tem essa simetria. É isso que faz a conta fechar.

### 5. Os diabólicos, onde até a diagonal quebrada soma

Diagonal quebrada é a que sai por uma borda e volta pela outra, como se a grade fosse um cilindro nos dois sentidos.

| ordem | mágicos distintos | diabólicos distintos | fração |
|---:|---:|---:|---:|
| 3 | 1 | **0** | 0,0% |
| 4 | 880 | 48 | 5,5% |

Exigir as diagonais quebradas é muito mais forte que ser mágico: dos 880, só 48 aguentam. E de ordem 3 não existe nenhum, com os oito testados um por um.

## O que as medidas me corrigiram

**O corte que eu tinha escrito estava certo e era quase inútil.** A primeira versão só conferia soma quando a linha ou a coluna fechava, e gastou 3,1 bilhões de tentativas em 37 segundos. Conferir o penúltimo, onde o valor já está determinado por subtração, derrubou para 52 milhões e 1,2 segundo. O mesmo resultado, sessenta vezes mais barato, e a diferença não foi esperteza nova: foi aplicar a definição um passo antes.

**"880 quadrados mágicos de ordem 4" e "7.040" são os dois verdadeiros, e quase sempre se cita um sem dizer qual.** A diferença são as oito simetrias da grade, e ela só é limpa porque nenhum dos 880 é igual a si mesmo girado.

**O centro 5 não precisa de prova para aparecer.** Eu ia escrever o argumento algébrico de por que o centro de um quadrado mágico de ordem 3 tem de ser 5. Com os oito na memória, a pergunta vira uma linha de código e a resposta vira um conjunto de um elemento só.

## Rodar

```bash
mvn -q verify                              # 26 testes
mvn -q exec:java                           # todas as medidas, uns 2 segundos
mvn -q exec:java -Dexec.args=correcao      # 1
mvn -q exec:java -Dexec.args=ordem3        # 2
mvn -q exec:java -Dexec.args=ordem4        # 3
mvn -q exec:java -Dexec.args=simetrias     # 4
mvn -q exec:java -Dexec.args=diabolicos    # 5
```

Java 21, sem dependência fora do JUnit. Nenhum número deste README foi digitado à mão: todos saem de `Medidas`, e os testes conferem os que aparecem escritos aqui.
