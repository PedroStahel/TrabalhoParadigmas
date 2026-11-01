import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tabuleiro {
    public static final int TAM = 5;

    private static final Posicao CENTRO = new Posicao(3, 3);

    // as 4 direções, para percorrer com um for
    private static final Direcao[] DIRECOES = { Direcao.NORTE, Direcao.SUL, Direcao.LESTE, Direcao.OESTE };

    // O desenho do mapa ta no fim do arquivo
    private static final String[][] MAPA = {
        // col 1   col 2   col 3   col 4   col 5
        { "SL",   "LO",   "SO",   "SL",   "SO" },
        { "NS",   "S",    "NSL",  "NSO",  "NS" },
        { "NL",   "NLO",  "NSLO", "NLO",  "NO" },
        { "SL",   "SLO",  "NLO",  "LO",   "SO" },
        { "NL",   "NLO",  "LO",   "LO",   "NO" },
    };

    private final Setor[][] setores;
    private final Random random;
    private Posicao fonteInfeccao;

    public Tabuleiro() {
        this.setores = new Setor[TAM][TAM];
        this.random = new Random();

        inicializarSetores();
        validarMapa();
        sortearPosicaoFonte();

        // os jogadores começam no centro, que já conta como visitado e não tem inimigos
        setorCentral().marcarSetorVisitado();
    }

    // ===================== consultas =====================

    public Setor getSetor(Posicao posicao) {
        if (posicao == null) {
            throw new ArgumentoInvalido("A posição não pode ser nula.");
        }

        return this.setores[posicao.getX() - 1][posicao.getY() - 1];
    }

    public Setor setorCentral() {
        return getSetor(CENTRO);
    }

    public Posicao getFonteInfeccao() {
        return this.fonteInfeccao;
    }

    // diz se o setor nessa posição tem porta na direção pedida
    public boolean temPorta(Posicao posicao, Direcao direcao) {
        if (direcao == null) {
            throw new ArgumentoInvalido("A direção não pode ser nula.");
        }

        return getSetor(posicao).getPortas()[direcao.getIndicePorta()];
    }

    // ===================== movimento =====================

    public boolean moverJogador(Jogador jogador, Direcao direcao) {
        if (jogador == null) {
            throw new ArgumentoInvalido("O jogador não pode ser nulo.");
        }

        Posicao origem = jogador.getPosicao();

        if (!jogador.podeSairDoSetor(getSetor(origem))) {
            return false;
        }

        if (!temPorta(origem, direcao)) {
            return false;
        }

        Posicao destino = origem.vizinha(direcao);
        if (destino == null) {
            return false;
        }

        jogador.setPosicao(destino);

        Setor setorDestino = getSetor(destino);
        if (!setorDestino.isVisitado()) {
            setorDestino.marcarSetorVisitado();

            if (!setorDestino.isContemFonte()) {
                gerarInimigos(setorDestino);
            }
        }

        return true;
    }

    // ===================== montagem do tabuleiro =====================

    // cria os 25 setores, cada um com as portas definidas no MAPA
    private void inicializarSetores() {
        for (int linha = 0; linha < TAM; linha++) {
            for (int coluna = 0; coluna < TAM; coluna++) {
                Posicao posicao = new Posicao(linha + 1, coluna + 1);
                boolean[] portas = converterPortas(MAPA[linha][coluna]);

                this.setores[linha][coluna] = criarSetor(posicao, portas);
            }
        }
    }

    // o centro é sempre normal; as outras casas sorteiam entre normal, oculto e privado
    private Setor criarSetor(Posicao posicao, boolean[] portas) {
        if (posicao.equals(CENTRO)) {
            return new SetorNormal(posicao, portas);
        }

        int tipo = this.random.nextInt(3); // 0, 1 ou 2

        if (tipo == 0) {
            return new SetorNormal(posicao, portas);
        } else if (tipo == 1) {
            return new SetorOculto(posicao, portas);
        } else {
            return new SetorPrivado(posicao, portas);
        }
    }

    // transforma as letras de uma casa do MAPA  
    private static boolean[] converterPortas(String letras) {
        boolean[] portas = new boolean[4];

        portas[Direcao.NORTE.getIndicePorta()] = letras.contains("N");
        portas[Direcao.SUL.getIndicePorta()] = letras.contains("S");
        portas[Direcao.LESTE.getIndicePorta()] = letras.contains("L");
        portas[Direcao.OESTE.getIndicePorta()] = letras.contains("O");

        return portas;
    }

    // sorteia a casa da fonte de infecção, repetindo enquanto cair no centro
    private void sortearPosicaoFonte() {
        Posicao sorteada = CENTRO;

        while (sorteada.equals(CENTRO)) {
            int linha = this.random.nextInt(1, TAM + 1);  // 1 a 5
            int coluna = this.random.nextInt(1, TAM + 1); // 1 a 5
            sorteada = new Posicao(linha, coluna);
        }

        this.fonteInfeccao = sorteada;
        getSetor(sorteada).marcarFonte();
    }

    // cria de 1 a 3 inimigos no setor 
    private void gerarInimigos(Setor setor) {
        int quantidade = this.random.nextInt(1, 4); // 1 a 3

        for (int i = 0; i < quantidade; i++) {
            setor.adicionarInimigo(new Inimigo(setor.getPosicao()));
        }
    }

    // ===================== validação do mapa =====================

    // confere se o MAPA respeita as regras do enunciado. Se não, o jogo nem começa.
    private void validarMapa() {
        for (int linha = 1; linha <= TAM; linha++) {
            for (int coluna = 1; coluna <= TAM; coluna++) {
                Posicao posicao = new Posicao(linha, coluna);

                for (int i = 0; i < DIRECOES.length; i++) {
                    Direcao direcao = DIRECOES[i];
                    if (temPorta(posicao, direcao)) {
                        Posicao vizinha = posicao.vizinha(direcao);

                        // regra 1: a borda do tabuleiro é sempre parede
                        if (vizinha == null) {
                            throw new EstadoInvalido("Mapa inválido: porta na borda em " + posicao + ".");
                        }

                        // regra 2: a porta precisa existir dos dois lados
                        if (!temPorta(vizinha, direcao.oposta())) {
                            throw new EstadoInvalido("Mapa inválido: porta entre " + posicao + " e " + vizinha + " só existe de um lado.");
                        }
                    }
                }
            }
        }

        // regra 3: o centro tem as 4 portas
        for (int i = 0; i < DIRECOES.length; i++) {
            Direcao direcao = DIRECOES[i];
            if (!temPorta(CENTRO, direcao)) {
                throw new EstadoInvalido("Mapa inválido: o centro precisa ter as 4 portas.");
            }
        }

        // regra 4: dá para chegar em todas as casas a partir do centro
        if (!todosAlcancaveis()) {
            throw new EstadoInvalido("Mapa inválido: existem setores que não podem ser alcançados a partir do centro.");
        }
    }

    // a partit do centro, deve ser possivel chegar em tds as casas  
    private boolean todosAlcancaveis() {
        boolean[][] pintada = new boolean[TAM][TAM];
        List<Posicao> fila = new ArrayList<>();

        fila.add(CENTRO);
        pintada[CENTRO.getX() - 1][CENTRO.getY() - 1] = true;

        int proxima = 0;
        while (proxima < fila.size()) {
            Posicao atual = fila.get(proxima);
            proxima++;

            for (int i = 0; i < DIRECOES.length; i++) {
                Direcao direcao = DIRECOES[i];
                if (temPorta(atual, direcao)) {
                    Posicao vizinha = atual.vizinha(direcao);

                    if (vizinha != null && !pintada[vizinha.getX() - 1][vizinha.getY() - 1]) {
                        pintada[vizinha.getX() - 1][vizinha.getY() - 1] = true;
                        fila.add(vizinha);
                    }
                }
            }
        }

        return fila.size() == TAM * TAM;
    }

    // ===================== desenho =====================

    public void desenhar(List<Jogador> jogadores) {
        if (jogadores == null) {
            throw new ArgumentoInvalido("A lista de jogadores não pode ser nula.");
        }

        List<String> grade = montarGrade(jogadores);
        List<String> painel = montarPainel(jogadores);

        System.out.println("-----------------------------");
        System.out.println("|   Antivírus por um dia    |");
        System.out.println("-----------------------------");
        System.out.println();

        // o painel começa na altura da linha 1 do tabuleiro (terceira linha da grade)
        int inicioPainel = 2;

        for (int i = 0; i < grade.size(); i++) {
            String linha = completarComEspacos(grade.get(i), 28);

            int linhaPainel = i - inicioPainel;
            if (linhaPainel >= 0 && linhaPainel < painel.size()) {
                linha = linha + painel.get(linhaPainel);
            }

            System.out.println(linha);
        }

        System.out.println();
    }

    // monta as linhas da grade 5x5, com números das colunas no topo e das linhas à esquerda
    private List<String> montarGrade(List<Jogador> jogadores) {
        List<String> linhas = new ArrayList<>();

        linhas.add("      1   2   3   4   5");
        linhas.add("    " + linhaHorizontal(0));

        for (int linha = 1; linha <= TAM; linha++) {
            String texto = linha + "   |";

            for (int coluna = 1; coluna <= TAM; coluna++) {
                Posicao posicao = new Posicao(linha, coluna);
                texto = texto + conteudoDaCasa(posicao, jogadores);

                if (coluna < TAM && portaVisivel(posicao, Direcao.LESTE)) {
                    texto = texto + "*";
                } else {
                    texto = texto + "|";
                }
            }

            linhas.add(texto);
            linhas.add("    " + linhaHorizontal(linha));
        }

        return linhas;
    }

    // linha de "---" entre a linha do tabuleiro informada e a de baixo 
    private String linhaHorizontal(int linha) {
        String texto = "|";

        for (int coluna = 1; coluna <= TAM; coluna++) {
            boolean temPortaAqui = linha >= 1 && linha < TAM
                    && portaVisivel(new Posicao(linha, coluna), Direcao.SUL);

            if (temPortaAqui) {
                texto = texto + "-*-|";
            } else {
                texto = texto + "---|";
            }
        }

        return texto;
    }

    // uma porta só aparece se existir e se pelo menos um dos dois setores já foi visitado
    private boolean portaVisivel(Posicao posicao, Direcao direcao) {
        if (!temPorta(posicao, direcao)) {
            return false;
        }

        Posicao vizinha = posicao.vizinha(direcao);

        return getSetor(posicao).isVisitado() || getSetor(vizinha).isVisitado();
    }

    // o que aparece dentro de uma casa da grade (sempre 3 caracteres)
    private String conteudoDaCasa(Posicao posicao, List<Jogador> jogadores) {
        if (posicao.equals(CENTRO)) {
            return " C ";
        }

        List<String> nomes = new ArrayList<>();
        for (int i = 0; i < jogadores.size(); i++) {
            Jogador jogador = jogadores.get(i);

            if (jogador.estaVivo() && jogador.getPosicao().equals(posicao)) {
                nomes.add("P" + (i + 1));
            }
        }

        if (nomes.size() >= 2) {
            return " P ";
        } else if (nomes.size() == 1) {
            return nomes.get(0) + " ";
        }

        if (posicao.equals(this.fonteInfeccao)) {
            return " X ";
        }

        return "   ";
    }

    // monta uma caixa para cada setor ocupado por jogadores vivos, lado a lado
    private List<String> montarPainel(List<Jogador> jogadores) {
        List<Posicao> setoresOcupados = new ArrayList<>();

        for (Jogador jogador : jogadores) {
            if (jogador.estaVivo() && !setoresOcupados.contains(jogador.getPosicao())) {
                setoresOcupados.add(jogador.getPosicao());
            }
        }

        List<String> painel = new ArrayList<>();

        for (Posicao posicao : setoresOcupados) {
            List<String> caixa = montarCaixa(posicao, jogadores);

            for (int i = 0; i < caixa.size(); i++) {
                if (i < painel.size()) {
                    painel.set(i, painel.get(i) + "   " + caixa.get(i));
                } else {
                    painel.add(caixa.get(i));
                }
            }
        }

        return painel;
    }

    // caixa de um setor: portas nas bordas, inimigos em cima, jogadores embaixo (15 caracteres de largura)
    private List<String> montarCaixa(Posicao posicao, List<Jogador> jogadores) {
        Setor setor = getSetor(posicao);

        String inimigos = "";
        for (Inimigo inimigo : setor.getInimigos()) {
            if (inimigo.estaVivo()) {
                inimigos = inimigos + " " + inimigo.getAtaque() + "/" + inimigo.getDefesa();
            }
        }

        List<String> nomes = new ArrayList<>();
        List<String> atributos = new ArrayList<>();
        for (int i = 0; i < jogadores.size(); i++) {
            Jogador jogador = jogadores.get(i);

            if (jogador.estaVivo() && jogador.getPosicao().equals(posicao)) {
                nomes.add("P" + (i + 1));
                atributos.add(jogador.getAtaque() + "/" + jogador.getDefesa());
            }
        }

        String esquerda = temPorta(posicao, Direcao.OESTE) ? "*" : "|";
        String direita = temPorta(posicao, Direcao.LESTE) ? "*" : "|";

        List<String> caixa = new ArrayList<>();
        caixa.add("  " + completarComEspacos("Setor " + posicao, 13));
        caixa.add("               ");
        caixa.add(bordaDaCaixa(temPorta(posicao, Direcao.NORTE)));
        caixa.add("|" + completarComEspacos(inimigos, 13) + "|");
        caixa.add("|             |");
        caixa.add(esquerda + "             " + direita);
        caixa.add("|" + duasColunas(nomes) + "|");
        caixa.add("|" + duasColunas(atributos) + "|");
        caixa.add(bordaDaCaixa(temPorta(posicao, Direcao.SUL)));

        return caixa;
    }

    // coloca 1 ou 2 textos lado a lado dentro dos 13 espaços da caixa (ex.: " P1      P2  ")
    private String duasColunas(List<String> textos) {
        if (textos.size() >= 2) {
            return " " + completarComEspacos(textos.get(0), 8) + completarComEspacos(textos.get(1), 4);
        } else if (textos.size() == 1) {
            return " " + completarComEspacos(textos.get(0), 12);
        }
        return "             ";
    }

    // coloca espaços no fim do texto até ele ter o tamanho pedido (ex.: "abc" com 5 vira "abc  ")
    private String completarComEspacos(String texto, int tamanho) {
        String resultado = texto;
        while (resultado.length() < tamanho) {
            resultado = resultado + " ";
        }
        return resultado;
    }

    private String bordaDaCaixa(boolean temPorta) {
        if (temPorta) {
            return "|------*------|";
        }
        return "|-------------|";
    }
}

/*
 * Esboço do MAPA (* = porta, | e --- = parede):
 *
 *      1   2   3   4   5
 *    |---|---|---|---|---|
 *  1 |   *   *   |   *   |
 *    |-*-|---|-*-|-*-|-*-|
 *  2 |   |   |   *   |   |
 *    |-*-|-*-|-*-|-*-|-*-|
 *  3 |   *   *   *   *   |
 *    |---|---|-*-|---|---|
 *  4 |   *   *   *   *   |
 *    |-*-|-*-|---|---|-*-|
 *  5 |   *   *   *   *   |
 *    |---|---|---|---|---|
 */
